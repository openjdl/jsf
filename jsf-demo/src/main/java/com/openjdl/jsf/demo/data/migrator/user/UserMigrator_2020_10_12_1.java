package com.openjdl.jsf.demo.data.migrator.user;

import com.openjdl.jsf.jdbc.mybatis.migration.BaseClassPathResourceMigrator;
import com.openjdl.jsf.jdbc.mybatis.migration.annotation.Migrator;
import org.springframework.stereotype.Component;

/**
 * Created at 2020-10-12 15:49:10
 *
 * @author kidal
 * @since 0.4
 */
@Component
@Migrator
public class UserMigrator_2020_10_12_1 extends BaseClassPathResourceMigrator {
  public UserMigrator_2020_10_12_1() {
    super(null, null);
  }
}
