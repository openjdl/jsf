package com.openjdl.jsf.jdbc.datasource;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.util.concurrent.Callable;

/**
 * Created at 2020-08-06 17:25:34
 *
 * @author kidal
 * @since 0.1.0
 */
public interface RoutingDataSource extends DataSource {
  /**
   * 添加查询键组
   *
   * @param groupName 组名
   * @param keysGroup 键组
   */
  void addLookupKeysGroup(@NotNull String groupName, @NotNull RoutingDataSourceLookupKeysGroup keysGroup);

  /**
   * 推入数据源组
   *
   * @param groupName 数据源组名.
   * @param readOnly  是否使用只读数据源.
   */
  void push(String groupName, boolean readOnly);

  /**
   * 推入数据源组
   *
   * @param groupName 数据源组名.
   */
  void push(String groupName);

  /**
   * 推入数据源组
   *
   * @param readOnly 是否使用只读数据源.
   */
  void push(boolean readOnly);

  /**
   * 推入数据源组
   */
  void push();

  /**
   * 弹出数据源组
   *
   * @param groupName 数据源组名.
   */
  void pop(String groupName);

  /**
   * 弹出数据源组
   */
  void pop();

  /**
   * @param groupName 数据源组名.
   * @param readOnly  是否使用只读数据源.
   * @param callable  回调方法.
   */
  @Nullable
  <V> V run(@NotNull String groupName, boolean readOnly, @NotNull Callable<V> callable) throws Exception;

  /**
   * @param groupName 数据源组名.
   * @param readOnly  是否使用只读数据源.
   * @param callable  回调方法.
   */
  @Nullable
  <V> V runUnsafe(@NotNull String groupName, boolean readOnly, @NotNull Callable<V> callable) throws RuntimeException;
}
