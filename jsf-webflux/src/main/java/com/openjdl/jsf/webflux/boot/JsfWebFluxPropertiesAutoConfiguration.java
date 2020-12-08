package com.openjdl.jsf.webflux.boot;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.Maps;
import com.openjdl.jsf.core.boot.JsfCoreProperties;
import com.openjdl.jsf.core.boot.JsfCorePropertiesAutoConfiguration;
import com.openjdl.jsf.core.utils.SpringUtils;
import com.openjdl.jsf.core.utils.json.DoubleSerializer;
import com.openjdl.jsf.core.utils.json.FloatSerializer;
import com.openjdl.jsf.core.utils.json.Iso8601JsonSerializer;
import com.openjdl.jsf.core.utils.json.UncertainDateJsonDeserializer;
import com.openjdl.jsf.webflux.WebFluxService;
import com.openjdl.jsf.webflux.WebFluxServiceImpl;
import com.openjdl.jsf.webflux.modbus.dtu.ModbusDtuSessionManager;
import com.openjdl.jsf.webflux.websocket.WebSocketSessionManager;
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
import org.springframework.scheduling.TaskScheduler;
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
    final Version version = new Version(1, 0, 0, "RELEASE", "com.openjdl", "jsf-webflux");
    final ObjectMapper objectMapper = new ObjectMapper();
    final SimpleModule module = new SimpleModule("com.openjdl.jsf.webflux", version)
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

  //--------------------------------------------------------------------------------------------------------------
  // ModbusDtu
  //--------------------------------------------------------------------------------------------------------------
  //region

  /**
   *
   */
  @Bean(JsfWebFluxProperties.B_MODBUS_DTU_SESSION_MANAGER)
  @ConditionalOnProperty(value = JsfWebFluxProperties.P_MODBUS_DTU_ENABLED, havingValue = "true")
  public ModbusDtuSessionManager modbusDtuSessionManager(
    @Qualifier(JsfCoreProperties.B_SPRING_UTILS)
      SpringUtils springUtils,
    TaskScheduler taskScheduler
  ) {
    return new ModbusDtuSessionManager(springUtils, taskScheduler, properties.getModbusDtu());
  }

  //endregion

  //--------------------------------------------------------------------------------------------------------------
  // WebSocket
  //--------------------------------------------------------------------------------------------------------------
  //region

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
  @Bean(JsfWebFluxProperties.B_WEBSOCKET_SESSION_MANAGER)
  @ConditionalOnProperty(value = JsfWebFluxProperties.P_WEBSOCKET_ENABLED, havingValue = "true")
  public WebSocketSessionManager webSocketSessionManager(
    @Qualifier(JsfCoreProperties.B_SPRING_UTILS)
      SpringUtils springUtils,
    @Qualifier(JsfCoreProperties.B_CONVERSION_SERVICE)
      ConversionService conversionService
  ) {
    return new WebSocketSessionManager(springUtils, conversionService);
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
    handlerMap.put(properties.getWebsocket().getPath(), webSocketSessionManager(springUtils, conversionService));

    SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
    mapping.setUrlMap(handlerMap);
    mapping.setOrder(-1);
    return mapping;
  }

  //endregion
}
