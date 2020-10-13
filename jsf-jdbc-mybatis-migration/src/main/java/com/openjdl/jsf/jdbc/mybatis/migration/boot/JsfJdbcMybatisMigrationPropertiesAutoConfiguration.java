package com.openjdl.jsf.jdbc.mybatis.migration.boot;

import com.openjdl.jsf.core.utils.SpringUtils;
import com.openjdl.jsf.jdbc.boot.JsfJdbcProperties;
import com.openjdl.jsf.jdbc.datasource.RoutingDataSource;
import com.openjdl.jsf.jdbc.mybatis.migration.JdbcMybatisMigrationService;
import com.openjdl.jsf.jdbc.mybatis.migration.JdbcMybatisMigrationServiceImpl;
import com.openjdl.jsf.jdbc.mybatis.migration.data.mapper.MigrationMapper;
import com.openjdl.jsf.jdbc.mybatis.migration.data.mapper.MigratorMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Created at 2020-10-11 19:34:20
 *
 * @author kidal
 * @since 0.4
 */
@Configuration
@EnableConfigurationProperties(JsfJdbcMybatisMigrationProperties.class)
@ConditionalOnProperty(value = JsfJdbcMybatisMigrationProperties.P_ENABLED, havingValue = "true", matchIfMissing = true)
public class JsfJdbcMybatisMigrationPropertiesAutoConfiguration {
  private final JsfJdbcMybatisMigrationProperties properties;

  /**
   *
   */
  public JsfJdbcMybatisMigrationPropertiesAutoConfiguration(JsfJdbcMybatisMigrationProperties properties) {
    this.properties = properties;
  }

  /**
   *
   */
  @Primary
  @Bean(JsfJdbcMybatisMigrationProperties.B_JDBC_MYBATIS_MIGRATION_SERVICE)
  public JdbcMybatisMigrationService jdbcMybatisMigrationService(
    JsfJdbcProperties jdbcProperties,
    SpringUtils springUtils,
    @Qualifier(JsfJdbcProperties.B_ROUTING_DATASOURCE)
      RoutingDataSource routingDataSource,
    MigrationMapper migrationMapper,
    MigratorMapper migratorMapper
  ) {
    return new JdbcMybatisMigrationServiceImpl(properties, jdbcProperties, springUtils, routingDataSource, migrationMapper, migratorMapper);
  }
}
