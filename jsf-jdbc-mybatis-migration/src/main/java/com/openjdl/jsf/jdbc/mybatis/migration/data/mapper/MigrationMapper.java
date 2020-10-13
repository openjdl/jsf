package com.openjdl.jsf.jdbc.mybatis.migration.data.mapper;

import com.openjdl.jsf.jdbc.mybatis.migration.data.po.MigrationPo;
import org.apache.ibatis.annotations.Param;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

/**
 * Created at 2020-10-09 12:48:43
 *
 * @author kidal
 * @since 0.4
 */
public interface MigrationMapper {
  /**
   * 创建迁移记录表
   */
  void createTableIfNotExists(@NotNull @Param("prefix") String prefix);

  /**
   * 添加迁移记录
   */
  void create(@NotNull @Param("prefix") String prefix,
              @NotNull @Param("migrator") String migrator,
              @NotNull @Param("createdAt") Date createdAt);

  /**
   * 列出全部已经运行的迁移记录
   */
  List<MigrationPo> list(@NotNull @Param("prefix") String prefix);

  /**
   * 删除对应的`migrator`的迁移记录
   *
   * @param migrator 迁移器
   */
  void removeByMigrator(@NotNull @Param("prefix") String prefix,
                        @Param("migrator") String migrator);
}
