package org.kidal.jsf.webflux.boot;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.jetbrains.annotations.NotNull;
import org.kidal.jsf.core.utils.json.DoubleSerializer;
import org.kidal.jsf.core.utils.json.FloatSerializer;
import org.kidal.jsf.core.utils.json.Iso8601JsonSerializer;
import org.kidal.jsf.core.utils.json.UncertainDateJsonDeserializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.util.Date;

/**
 * Created at 2020-08-05 11:51:12
 *
 * @author kidal
 * @since 0.1.0
 */
@Configuration
public class JsfWebFluxPropertiesAutoConfiguration implements WebFluxConfigurer {
  /**
   * 跨域
   */
  @Override
  public void addCorsMappings(@NotNull CorsRegistry registry) {
    // TODO: 通过配置来设置下面的参数
    registry.addMapping("/**")
      .allowCredentials(true)
      .allowedOrigins("*")
      .allowedHeaders("*")
      .allowedMethods("*")
      .exposedHeaders(HttpHeaders.SET_COOKIE);
  }

  /**
   * 一些常用类型的编码
   */
  @Override
  public void configureHttpMessageCodecs(@NotNull ServerCodecConfigurer configurer) {
    final Version version = new Version(1, 0, 0, "RELEASE", "org.kidal", "jsf-webflux");
    final ObjectMapper objectMapper = new ObjectMapper();
    final SimpleModule module = new SimpleModule("org.kidal.jsf.webflux", version)
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
}
