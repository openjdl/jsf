package org.kidal.jsf.jdbc.datasource;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kidal.jsf.core.utils.RandomUtils;
import org.kidal.jsf.core.utils.StringUtils;
import org.kidal.jsf.jdbc.boot.JsfJdbcProperties;
import org.kidal.jsf.jdbc.exception.MasterSlaveSwitchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created at 2020-08-06 17:31:51
 *
 * @author kidal
 * @since 0.1.0
 */
public class RoutingDataSourceImpl extends AbstractRoutingDataSource implements RoutingDataSource {
  /**
   * 日志
   */
  private static final Logger LOG = LoggerFactory.getLogger(RoutingDataSourceImpl.class);

  /**
   *
   */
  private final JsfJdbcProperties properties;

  /**
   *
   */
  private final ThreadLocal<ContextDO> context = ThreadLocal.withInitial(ContextDO::new);

  /**
   *
   */
  private final Map<String, RoutingDataSourceLookupKeysGroup> lookupKeysGroupMap = new HashMap<>();

  /**
   *
   */
  public RoutingDataSourceImpl(JsfJdbcProperties properties) {
    this.properties = properties;
  }

  /**
   *
   */
  @Override
  public void addLookupKeysGroup(@NotNull String groupName, @NotNull RoutingDataSourceLookupKeysGroup keysGroup) {
    lookupKeysGroupMap.put(groupName, keysGroup);
  }

  /**
   *
   */
  @Override
  public void push(String groupName, boolean readOnly) {
    // trace
    if (LOG.isDebugEnabled()) {
      LOG.debug("Push {}(readOnly={})", groupName, readOnly);
    }

    // 获取上下文
    ContextDO context = this.context.get();

    // 获取当前数据源信息
    DataSourceDO info = context.getMap().get(groupName);
    if (info != null) {
      if (info.isReadOnly() != readOnly) {
        String message = MessageFormat.format("Can not switch {}(readOnly={}) to {}(readOnly={})",
          info.getGroupName(), info.isReadOnly(), groupName, readOnly);
        LOG.error(message);
        throw new MasterSlaveSwitchException(message);
      }
    } else {
      info = new DataSourceDO(groupName, readOnly, null);
      context.getMap().put(groupName, info);
    }

    // 记录入栈
    context.getStack().push(info);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void push(String groupName) {
    push(groupName, properties.getRoutingDataSource().isDefaultReadOnly());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void push(boolean readOnly) {
    push(properties.getRoutingDataSource().getDefaultGroupName(), readOnly);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void push() {
    push(properties.getRoutingDataSource().getDefaultGroupName(), properties.getRoutingDataSource().isDefaultReadOnly());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void pop(String groupName) {
    // trace
    if (LOG.isDebugEnabled()) {
      LOG.debug("Pop {}(readOnly=?)", groupName);
    }

    // 获取上下文
    ContextDO context = this.context.get();

    // 检查栈
    if (context.getStack().isEmpty()) {
      LOG.error("Pop failed: dataSource stack is empty");
      return;
    }

    // 出栈
    DataSourceDO info = context.getStack().peek();
    //noinspection ConstantConditions
    if (!info.getGroupName().equals(groupName)) {
      LOG.error("Pop failed: current top is {}(readOnly={}) not {}(readOnly=?)",
        info.getGroupName(), info.isReadOnly(), groupName);
      return;
    }
    context.getStack().pop();

    // 移除缓存
    long count = context.getStack().stream()
      .filter(item -> item == info)
      .count();
    if (count == 0) {
      context.getMap().remove(info.getGroupName());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void pop() {
    pop(properties.getRoutingDataSource().getDefaultGroupName());
  }

  /**
   * {@inheritDoc}
   */
  @Nullable
  @Override
  public <V> V run(@NotNull String groupName, boolean readOnly, @NotNull Callable<V> callable) throws Exception {
    push(groupName, readOnly);
    try {
      return callable.call();
    } finally {
      pop(groupName);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Nullable
  @Override
  public <V> V runUnsafe(@NotNull String groupName, boolean readOnly, @NotNull Callable<V> callable) throws RuntimeException {
    try {
      return run(groupName, readOnly, callable);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   *
   */
  @Override
  protected Object determineCurrentLookupKey() {
    String groupName, lookupKey = null;
    boolean readOnly, useDefault;

    // use default?
    ContextDO context = this.context.get();
    useDefault = context.getStack().size() == 0;

    // go
    if (useDefault) {
      groupName = properties.getRoutingDataSource().getDefaultGroupName();
      readOnly = properties.getRoutingDataSource().isDefaultReadOnly();
    } else {
      DataSourceDO info = context.getStack().peek();

      // set group
      //noinspection ConstantConditions
      groupName = info.getGroupName();
      readOnly = info.isReadOnly();

      // set key
      if (StringUtils.isNotBlank(info.getLookupKey())) {
        lookupKey = info.getLookupKey();
      }
      info.setLookupKey(lookupKey);
    }

    if (lookupKey == null) {
      lookupKey = determineLookupKey(groupName, readOnly);
    }

    // trace
    if (LOG.isDebugEnabled()) {
      if (useDefault) {
        LOG.debug("Using default {}(readOnly={}, key={})", groupName, readOnly, lookupKey);
      } else {
        LOG.debug("Using {}(readOnly={}, key={})", groupName, readOnly, lookupKey);
      }
    }

    // done
    return lookupKey;
  }

  /**
   *
   */
  private String determineLookupKey(String groupName, boolean readOnly) {
    // 获取查询键仓库
    RoutingDataSourceLookupKeysGroup lookupKeysGroup = lookupKeysGroupMap.get(groupName);
    if (lookupKeysGroup == null) {
      throw new IllegalStateException("Group " + groupName + " not exists");
    }

    // 获取数据源键
    String lookupKey;
    List<String> lookupKeys = readOnly
      ? lookupKeysGroup.getSlaveKeys()
      : lookupKeysGroup.getMasterKeys();
    if (lookupKeys.size() == 0) {
      throw new IllegalStateException("Empty group " + groupName);
    } else if (lookupKeys.size() == 1) {
      lookupKey = lookupKeys.get(0);
    } else {
      lookupKey = lookupKeys.get(RandomUtils.nextInt(0, lookupKeys.size() - 1));
    }
    return lookupKey;
  }

  //--------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------

  public static class DataSourceDO {
    @NotNull
    private String groupName;
    private boolean readOnly;
    @Nullable
    private String lookupKey;

    public DataSourceDO(@NotNull String groupName, boolean readOnly, @Nullable String lookupKey) {
      this.groupName = groupName;
      this.readOnly = readOnly;
      this.lookupKey = lookupKey;
    }

    @NotNull
    public String getGroupName() {
      return groupName;
    }

    public void setGroupName(@NotNull String groupName) {
      this.groupName = groupName;
    }

    public boolean isReadOnly() {
      return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
      this.readOnly = readOnly;
    }

    @Nullable
    public String getLookupKey() {
      return lookupKey;
    }

    public void setLookupKey(@Nullable String lookupKey) {
      this.lookupKey = lookupKey;
    }
  }

  public static class ContextDO {
    private final Map<String, DataSourceDO> map = new HashMap<>();
    private final LinkedList<DataSourceDO> stack = new LinkedList<>();

    public Map<String, DataSourceDO> getMap() {
      return map;
    }

    public LinkedList<DataSourceDO> getStack() {
      return stack;
    }
  }
}
