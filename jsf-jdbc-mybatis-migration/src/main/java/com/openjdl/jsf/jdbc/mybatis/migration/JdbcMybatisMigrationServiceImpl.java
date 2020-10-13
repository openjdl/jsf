package com.openjdl.jsf.jdbc.mybatis.migration;

import com.openjdl.jsf.core.utils.SpringUtils;
import com.openjdl.jsf.core.utils.StringUtils;
import com.openjdl.jsf.jdbc.boot.JsfJdbcProperties;
import com.openjdl.jsf.jdbc.datasource.RoutingDataSource;
import com.openjdl.jsf.jdbc.mybatis.migration.annotation.Migrator;
import com.openjdl.jsf.jdbc.mybatis.migration.boot.JsfJdbcMybatisMigrationProperties;
import com.openjdl.jsf.jdbc.mybatis.migration.data.definition.MigratorDefinition;
import com.openjdl.jsf.jdbc.mybatis.migration.data.mapper.MigrationMapper;
import com.openjdl.jsf.jdbc.mybatis.migration.data.mapper.MigratorMapper;
import com.openjdl.jsf.jdbc.mybatis.migration.data.po.MigrationPo;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created at 2020-10-11 18:22:05
 *
 * @author kidal
 * @since 0.4
 */
public class JdbcMybatisMigrationServiceImpl implements JdbcMybatisMigrationService {
  private final Logger log = LoggerFactory.getLogger(JdbcMybatisMigrationServiceImpl.class);

  private final JsfJdbcMybatisMigrationProperties properties;
  private final JsfJdbcProperties jdbcProperties;
  private final SpringUtils springUtils;
  private final RoutingDataSource routingDataSource;
  private final MigrationMapper migrationMapper;
  private final MigratorMapper migratorMapper;

  private final Map<String, Map<String, MigratorDefinition>> definitionMap = new HashMap<>();
  private final StringBuilder logs = new StringBuilder();

  /**
   *
   */
  public JdbcMybatisMigrationServiceImpl(JsfJdbcMybatisMigrationProperties properties,
                                         JsfJdbcProperties jdbcProperties,
                                         SpringUtils springUtils,
                                         RoutingDataSource routingDataSource,
                                         MigrationMapper migrationMapper,
                                         MigratorMapper migratorMapper) {
    this.properties = properties;
    this.jdbcProperties = jdbcProperties;
    this.springUtils = springUtils;
    this.routingDataSource = routingDataSource;
    this.migrationMapper = migrationMapper;
    this.migratorMapper = migratorMapper;
    this.registerSelf();
  }

  /**
   *
   */
  @NotNull
  @Override
  public String getJsfServiceName() {
    return JdbcMybatisMigrationService.class.getSimpleName();
  }

  /**
   *
   */
  @Override
  public void onJsfServiceInitializeStage() throws Exception {
    springUtils
      .getApplicationContext()
      .getBeansOfType(BaseMigrator.class)
      .forEach((beanName, bean) -> {
        BaseMigrator unwrappedBean = springUtils.unwrapProxy(bean);

        Migrator migrator = unwrappedBean.getClass().getAnnotation(Migrator.class);
        if (migrator == null) {
          throw new IllegalStateException(String.format(
            "实现了接口 %s 的类 `%s` 缺少注解 %s",
            BaseMigrator.class.getName(),
            unwrappedBean.getClass().getName(),
            Migrator.class.getName()
          ));
        }

        String name = unwrappedBean.getClass().getSimpleName();
        String order = StringUtils.isBlank(migrator.order()) ? name : migrator.order();
        String group = StringUtils.isNotBlank(bean.getDataSourceGroupName())
          ? bean.getDataSourceGroupName()
          : jdbcProperties.getRoutingDataSource().getDefaultGroupName();

        definitionMap
          .computeIfAbsent(group, k -> new HashMap<>())
          .put(name, new MigratorDefinition(name, order, bean));
      });

    up();
  }

  /**
   *
   */
  private void info(@NotNull String group, @NotNull String fmt, Object... argArray) {
    FormattingTuple tuple = MessageFormatter.format(fmt, argArray);

    logs.append(group).append(": ").append(tuple.getMessage()).append("\n");
    log.info(tuple.getMessage());
  }

  /**
   *
   */
  @NotNull
  public String getLogs() {
    return logs.toString();
  }

  /**
   *
   */
  private void up() throws Exception {
    // 列出全部数据源
    Set<String> groupSet = definitionMap.keySet();

    // 确保每个数据源都建立好了迁移记录表
    for (String group : groupSet) {
      routingDataSource.run(group, false, () -> {
        migrationMapper.createTableIfNotExists(properties.getTablePrefix());
        return null;
      });
    }

    // 开始迁移
    for (Map.Entry<String, Map<String, MigratorDefinition>> entry : definitionMap.entrySet()) {
      String group = entry.getKey();
      Map<String, MigratorDefinition> migratorDefinitionMap = entry.getValue();

      // 读取已运行的迁移器
      Set<String> ran = Objects.requireNonNull(routingDataSource
        .run(group, false, () -> migrationMapper.list(properties.getTablePrefix())))
        .stream()
        .map(MigrationPo::getMigrator)
        .collect(Collectors.toSet());

      // 筛选出待执行的迁移器
      List<MigratorDefinition> run = migratorDefinitionMap
        .entrySet()
        .stream()
        .filter(it -> !ran.contains(it.getKey()))
        .map(Map.Entry::getValue)
        .collect(Collectors.toList());
      if (run.isEmpty()) {
        info(group, "无需迁移");
      }

      // 排序
      run.sort(Comparator.comparing(MigratorDefinition::getOrder));

      // 执行迁移
      for (MigratorDefinition definition : run) {
        up(group, definition);
      }
    }
  }

  /**
   *
   */
  private void up(@NotNull String group, @NotNull MigratorDefinition definition) throws Exception {
    // run
    routingDataSource.run(group, false, () -> {
      // up
      definition.getMigrator().up(migratorMapper);

      // record
      migrationMapper.create(properties.getTablePrefix(), definition.getName(), new Date(System.currentTimeMillis()));

      // done
      return null;
    });

    // log
    info(group, "Up: {}", definition.getName());
  }
}
