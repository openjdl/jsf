package com.openjdl.jsf.jdbc.mybatis.migration;

import com.openjdl.jsf.jdbc.mybatis.migration.data.mapper.MigratorMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created at 2020-10-11 19:14:49
 *
 * @author kidal
 * @since 0.4
 */
public interface BaseMigrator {
  /**
   * 获取数据源组
   */
  @Nullable
  default String getDataSourceGroupName() {
    return null;
  }

  /**
   * 升级版本
   */
  void up(@NotNull MigratorMapper migratorMapper);
}
