package io.tdi.jsf.webflux.boot;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.Maps;
import io.tdi.jsf.core.boot.JsfCoreProperties;
import io.tdi.jsf.core.boot.JsfCorePropertiesAutoConfiguration;
import io.tdi.jsf.core.utils.SpringUtils;
import io.tdi.jsf.core.utils.json.DoubleSerializer;
import io.tdi.jsf.core.utils.json.FloatSerializer;
import io.tdi.jsf.core.utils.json.Iso8601JsonSerializer;
import io.tdi.jsf.core.utils.json.UncertainDateJsonDeserializer;
import io.tdi.jsf.webflux.WebFluxService;
import io.tdi.jsf.webflux.WebFluxServiceImpl;
import io.tdi.jsf.webflux.websocket.SessionManager;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.Date;
import java.util.Map;

/**
 * Created at 2020-08-05 11:51:12
 *
 * @author kidal
 * @since 0.1.0
 */
@Configuration
@EnableConfigurationProperties(JsfWebFluxProperties.class)
@ConditionalOnProperty(value = JsfWebFluxProperties.P_ENABLED, havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter(JsfCorePropertiesAutoConfiguration.class)
public class JsfWebFluxPropertiesAutoConfiguration implements WebFluxConfigurer {
  /**
   *
   */
  private final JsfWebFluxProperties properties;

  /**
   *
   */
  public JsfWebFluxPropertiesAutoConfiguration(JsfWebFluxProperties properties) {
    this.properties = properties;
  }

  /**
   * 跨域
   */
  @Override
  public void addCorsMappings(@NotNull CorsRegistry registry) {
    JsfWebFluxProperties.Cors cors = properties.getCors();
    if (cors.isEnabled()) {
      registry.addMapping(cors.getPathPattern())
        .allowCredentials(cors.isAllowCredentials())
        .allowedOrigins(cors.getAllowedOrigins().toArray(new String[0]))
        .allowedHeaders(cors.getAllowedHeaders().toArray(new String[0]))
        .allowedMethods(cors.getAllowedMethods().toArray(new String[0]))
        .exposedHeaders(cors.getExposedHeaders().toArray(new String[0]));
    }
  }

  /**
   * 一些常用类型的编码
   */
  @Override
  public void configureHttpMessageCodecs(@NotNull ServerCodecConfigurer configurer) {
    final Version version = new Version(1, 0, 0, "RELEASE", "io.tdi", "jsf-webflux");
    final ObjectMapper objectMapper = new ObjectMapper();
    final SimpleModule module = new SimpleModule("io.tdi.jsf.webflux", version)
      .addSerializer(float.class, new FloatSerializer())
      .addSerializer(Float.class, new FloatSerializer())
      .addSerializer(double.class, new DoubleSerializer())
      .addSerializer(Double.class, new DoubleSerializer())
      .addSerializer(Date.class, new Iso8601JsonSerializer())
      .addDeserializer(Date.class, new UncertainDateJsonDeserializer());
    objectMapper.registerModule(module);
    objectMapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);

    configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
    configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
  }

  /**
   *
   */
  @Primary
  @Bean(JsfWebFluxProperties.B_WEBFLUX_SERVICE)
  public WebFluxService webFluxService(
    @Qualifier(JsfCoreProperties.B_SPRING_UTILS)
      SpringUtils springUtils
  ) {
    return new WebFluxServiceImpl(properties, springUtils);
  }

  /**
   *
   */
  @Bean
  @ConditionalOnProperty(value = JsfWebFluxProperties.P_WEBSOCKET_ENABLED, havingValue = "true")
  public WebSocketHandlerAdapter webSocketHandlerAdapter() {
    return new WebSocketHandlerAdapter();
  }

  /**
   *
   */
  @Bean(JsfWebFluxProperties.B_SESSION_MANAGER)
  @ConditionalOnProperty(value = JsfWebFluxProperties.P_WEBSOCKET_ENABLED, havingValue = "true")
  public SessionManager sessionManager(
    @Qualifier(JsfCoreProperties.B_SPRING_UTILS)
      SpringUtils springUtils,
    @Qualifier(JsfCoreProperties.B_CONVERSION_SERVICE)
      ConversionService conversionService
  ) {
    return new SessionManager(springUtils, conversionService);
  }

  /**
   *
   */
  @Bean
  @ConditionalOnProperty(value = JsfWebFluxProperties.P_WEBSOCKET_ENABLED, havingValue = "true")
  public HandlerMapping handlerMapping(
    @Qualifier(JsfCoreProperties.B_SPRING_UTILS)
      SpringUtils springUtils,
    @Qualifier(JsfCoreProperties.B_CONVERSION_SERVICE)
      ConversionService conversionService
  ) {
    Map<String, WebSocketHandler> handlerMap = Maps.newHashMap();
    handlerMap.put(properties.getWebsocket().getPath(), sessionManager(springUtils, conversionService));

    SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
    mapping.setUrlMap(handlerMap);
    mapping.setOrder(-1);
    return mapping;
  }
}
