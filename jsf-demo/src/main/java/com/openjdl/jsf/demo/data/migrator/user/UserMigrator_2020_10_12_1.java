package com.openjdl.jsf.demo.data.migrator.user;

import com.openjdl.jsf.jdbc.mybatis.migration.BaseMigrator;
import com.openjdl.jsf.jdbc.mybatis.migration.annotation.Migrator;
import com.openjdl.jsf.jdbc.mybatis.migration.data.mapper.MigratorMapper;
import com.openjdl.jsf.jdbc.mybatis.utils.SqlAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * Created at 2020-10-12 15:49:10
 *
 * @author kidal
 * @since 0.4
 */
@Component
@Migrator
public class UserMigrator_2020_10_12_1 implements BaseMigrator {
  @Override
  public void up(@NotNull MigratorMapper migratorMapper) {
    migratorMapper.update(SqlAdapter.of(
      "create table user",
      "(",
      "    id             int                                       not null auto_increment primary key,",
      "    username       varchar(50)  default ''                   not null,",
      "    password       varchar(128) default ''                   not null,",
      "    createdAt      datetime(3)  default CURRENT_TIMESTAMP(3) not null,",
      "    updatedAt      datetime(3)  default CURRENT_TIMESTAMP(3) not null,",
      "    constraint u_idx_username unique (username)",
      ");"));
  }
}
