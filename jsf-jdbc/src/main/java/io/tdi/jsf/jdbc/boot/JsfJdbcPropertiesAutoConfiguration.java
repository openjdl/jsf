package io.tdi.jsf.jdbc.boot;

import io.tdi.jsf.jdbc.datasource.RoutingDataSourceImpl;
import io.tdi.jsf.jdbc.datasource.RoutingDataSourceLookupKeysGroup;
import org.jetbrains.annotations.NotNull;
import io.tdi.jsf.jdbc.JdbcService;
import io.tdi.jsf.jdbc.JdbcServiceImpl;
import io.tdi.jsf.jdbc.datasource.RoutingDataSource;
import io.tdi.jsf.jdbc.datasource.RoutingDataSourceAspect;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.BeanFactoryDataSourceLookup;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created at 2020-08-06 17:55:59
 *
 * @author kidal
 * @since 0.1.0
 */
@Configuration
@EnableConfigurationProperties(JsfJdbcProperties.class)
@ConditionalOnProperty(value = JsfJdbcProperties.P_ENABLED, havingValue = "true", matchIfMissing = true)
public class JsfJdbcPropertiesAutoConfiguration implements ApplicationContextAware, InitializingBean {
  /**
   *
   */
  @NotNull
  private final JsfJdbcProperties properties;

  /**
   *
   */
  @NotNull
  private GenericApplicationContext applicationContext;

  /**
   *
   */
  public JsfJdbcPropertiesAutoConfiguration(@NotNull JsfJdbcProperties properties) {
    this.properties = properties;
  }

  /**
   *
   */
  @Override
  public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
    if (applicationContext instanceof GenericApplicationContext) {
      this.applicationContext = (GenericApplicationContext) applicationContext;
    }
  }

  /**
   *
   */
  @Override
  public void afterPropertiesSet() {
    properties.getDataSources().forEach(this::initializeDataSource);
  }

  /**
   *
   */
  private void initializeDataSource(JsfJdbcProperties.DataSourceProperties dataSourceProperties) {
    dataSourceProperties = dataSourceProperties.complete(properties.getDefaultDataSource());

    try {
      GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
      beanDefinition.setBeanClass(Class.forName(dataSourceProperties.getDataSourceClassName()));
      dataSourceProperties.getPropertyValues()
        .forEach((key, value) -> beanDefinition.getPropertyValues().addPropertyValue(key, value));
      beanDefinition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_NO);
      beanDefinition.setSynthetic(true);

      String beanName = JsfJdbcProperties.makeDataSourceBeanName(dataSourceProperties.getName());
      applicationContext.registerBeanDefinition(beanName, beanDefinition);
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   *
   */
  @Primary
  @Bean(JsfJdbcProperties.B_JDBC_SERVICE)
  public JdbcService jdbcService() {
    return new JdbcServiceImpl();
  }

  /**
   *
   */
  @Primary
  @Bean(JsfJdbcProperties.B_ROUTING_DATASOURCE)
  public DataSource dataSource() {
    RoutingDataSourceImpl routingDataSource = new RoutingDataSourceImpl(properties);

    // 设置数据源查询方式
    routingDataSource.setDataSourceLookup(beanFactoryDataSourceLookup());

    // 设置目标数据源字典
    Map<Object, Object> targetDataSources = properties.getDataSources().stream()
      .collect(Collectors.toMap(
        JsfJdbcProperties.DataSourceProperties::getName,
        dataSource -> JsfJdbcProperties.makeDataSourceBeanName(dataSource.getName()),
        (u, v) -> {
          throw new IllegalStateException(String.format("Duplicate key %s", u));
        },
        LinkedHashMap::new));
    routingDataSource.setTargetDataSources(targetDataSources);

    // 添加数据源键仓库
    properties.getRoutingDataSource().getGroups().forEach(group -> {
      RoutingDataSourceLookupKeysGroup keysGroup = new RoutingDataSourceLookupKeysGroup();
      group.getMasters().forEach(key -> keysGroup.getMasterKeys().add(key));
      group.getSlaves().forEach(key -> keysGroup.getSlaveKeys().add(key));
      routingDataSource.addLookupKeysGroup(group.getName(), keysGroup);
    });

    // done
    return routingDataSource;
  }

  /**
   *
   */
  @Bean
  public BeanFactoryDataSourceLookup beanFactoryDataSourceLookup() {
    return new BeanFactoryDataSourceLookup();
  }

  /**
   *
   */
  @Bean(JsfJdbcProperties.B_ROUTING_DATASOURCE_ASPECT)
  @ConditionalOnProperty(value = JsfJdbcProperties.P_ASPECT_ENABLED, havingValue = "true", matchIfMissing = true)
  public RoutingDataSourceAspect aspectRoutingDataSource() {
    return new RoutingDataSourceAspect((RoutingDataSource) dataSource(), properties.getAspect().getOrder());
  }

  /**
   *
   */
  @EnableTransactionManagement
  @AutoConfigureAfter(JsfJdbcPropertiesAutoConfiguration.class)
  @ConditionalOnProperty(value = JsfJdbcProperties.P_TRANSACTION_ENABLED, havingValue = "true", matchIfMissing = true)
  public static class EnableTransaction {
    /**
     *
     */
    @Primary
    @Bean(JsfJdbcProperties.B_TRANSACTION_MANAGER)
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
      return new DataSourceTransactionManager(dataSource);
    }
  }
}
