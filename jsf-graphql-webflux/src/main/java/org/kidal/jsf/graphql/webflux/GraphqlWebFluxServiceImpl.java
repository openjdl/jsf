package org.kidal.jsf.graphql.webflux;

import org.jetbrains.annotations.NotNull;
import org.kidal.jsf.graphql.GraphqlService;
import org.kidal.jsf.graphql.webflux.boot.JsfGraphqlWebFluxProperties;
import org.kidal.jsf.graphql.webflux.controller.GraphqlController;
import org.kidal.jsf.graphql.webflux.controller.GraphqlControllerRequestBody;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.server.ServerWebExchange;

import java.lang.reflect.Method;

/**
 * Created at 2020-08-06 10:57:27
 *
 * @author kidal
 * @since 0.1.0
 */
public class GraphqlWebFluxServiceImpl implements GraphqlWebFluxService {
  /**
   *
   */
  @NotNull
  private final JsfGraphqlWebFluxProperties properties;

  /**
   *
   */
  @NotNull
  private final RequestMappingHandlerMapping requestMappingHandlerMapping;

  /**
   *
   */
  @NotNull
  private final GraphqlService graphqlService;

  /**
   *
   */
  public GraphqlWebFluxServiceImpl(@NotNull JsfGraphqlWebFluxProperties properties,
                                   @NotNull RequestMappingHandlerMapping requestMappingHandlerMapping,
                                   @NotNull GraphqlService graphqlService) {
    this.registerSelf();
    this.properties = properties;
    this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    this.graphqlService = graphqlService;
  }

  /**
   *
   */
  @Override
  public void initializeJsfService() throws Exception {
    // 注册请求控制器
    GraphqlController controller = new GraphqlController(graphqlService);
    Method queryMethod = controller.getClass().getDeclaredMethod(
      "query", GraphqlControllerRequestBody.class, ServerWebExchange.class
    );
    RequestMappingInfo mapping = RequestMappingInfo
      .paths(properties.getEndpoint())
      .methods(RequestMethod.POST)
      .produces(MediaType.APPLICATION_JSON_VALUE)
      .build();
    requestMappingHandlerMapping.registerMapping(mapping, controller, queryMethod);
  }

  /**
   *
   */
  @NotNull
  @Override
  public String getJsfServiceName() {
    return "GraphqlWebFluxService";
  }
}
