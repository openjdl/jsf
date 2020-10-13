package com.openjdl.jsf.demo.boot;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * Created at 2020-10-11 20:49:03
 *
 * @author kidal
 * @since 0.4
 */
@ConfigurationProperties(AppProperties.P_PATH)
public class AppProperties implements EnvironmentAware {
  public static final int MICRO_SERVICE_ID = 101;
  public static final String MICRO_SERVICE_NAME = "openjdl_jsf_demo";

  public static final String UIN_CATEGORY_USER = "user";

  public static final String P_PATH = "app";
  public static final String B_PATH = "app-";

  @Override
  public void setEnvironment(@NotNull Environment environment) {
    if (!MICRO_SERVICE_NAME.equals(environment.getProperty("spring.application.name"))) {
      throw new IllegalStateException("`spring.application.name`不一致");
    }
  }
}
