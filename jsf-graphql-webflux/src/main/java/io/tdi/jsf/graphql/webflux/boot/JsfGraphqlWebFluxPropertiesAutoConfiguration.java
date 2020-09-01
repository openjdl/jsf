package io.tdi.jsf.graphql.webflux.boot;

import io.tdi.jsf.graphql.GraphqlService;
import io.tdi.jsf.graphql.boot.JsfGraphqlPropertiesAutoConfiguration;
import io.tdi.jsf.graphql.webflux.GraphqlWebFluxService;
import io.tdi.jsf.graphql.webflux.GraphqlWebFluxServiceImpl;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;

/**
 * Created at 2020-08-06 10:48:30
 *
 * @author kidal
 * @since 0.1.0
 */
@Configuration
@EnableConfigurationProperties(JsfGraphqlWebFluxProperties.class)
@ConditionalOnProperty(value = JsfGraphqlWebFluxProperties.P_ENABLED, havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter(JsfGraphqlPropertiesAutoConfiguration.class)
public class JsfGraphqlWebFluxPropertiesAutoConfiguration {
  /**
   *
   */
  private final JsfGraphqlWebFluxProperties properties;

  /**
   *
   */
  public JsfGraphqlWebFluxPropertiesAutoConfiguration(JsfGraphqlWebFluxProperties properties) {
    this.properties = properties;
  }

  /**
   *
   */
  @Primary
  @Bean(JsfGraphqlWebFluxProperties.B_GRAPHQL_WEBFLUX_SERVICE)
  public GraphqlWebFluxService graphqlWebFluxService(
    RequestMappingHandlerMapping requestMappingHandlerMapping,
    GraphqlService graphqlService
  ) {
    return new GraphqlWebFluxServiceImpl(
      properties,
      requestMappingHandlerMapping,
      graphqlService
    );
  }
}
