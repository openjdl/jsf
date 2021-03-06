package com.openjdl.jsf.graphql.boot;

import com.openjdl.jsf.core.boot.JsfCoreProperties;
import com.openjdl.jsf.core.boot.JsfCorePropertiesAutoConfiguration;
import com.openjdl.jsf.core.utils.SpringUtils;
import com.openjdl.jsf.graphql.GraphqlService;
import com.openjdl.jsf.graphql.GraphqlServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.ConversionService;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Created at 2020-08-06 10:31:48
 *
 * @author kidal
 * @since 0.1.0
 */
@Configuration
@EnableConfigurationProperties(JsfGraphqlProperties.class)
@ConditionalOnProperty(value = JsfGraphqlProperties.P_ENABLED, havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter(JsfCorePropertiesAutoConfiguration.class)
public class JsfGraphqlPropertiesAutoConfiguration {
  /**
   *
   */
  private final JsfGraphqlProperties properties;

  /**
   *
   */
  public JsfGraphqlPropertiesAutoConfiguration(JsfGraphqlProperties properties) {
    this.properties = properties;
  }

  /**
   *
   */
  @Primary
  @Bean(JsfGraphqlProperties.B_GRAPHQL_SERVICE)
  public GraphqlService graphqlServiceFactory(
    @Qualifier(JsfCoreProperties.B_SPRING_UTILS)
      SpringUtils springUtils,
    @Autowired(required = false)
    @Qualifier(JsfCoreProperties.B_THREAD_POOL_TASK_EXECUTOR)
      ThreadPoolTaskExecutor threadPoolTaskExecutor,
    @Qualifier(JsfCoreProperties.B_CONVERSION_SERVICE)
      ConversionService conversionService
  ) {
    return new GraphqlServiceImpl(properties, springUtils, threadPoolTaskExecutor, conversionService);
  }
}
