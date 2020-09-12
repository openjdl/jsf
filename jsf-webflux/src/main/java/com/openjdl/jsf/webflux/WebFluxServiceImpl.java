package com.openjdl.jsf.webflux;

import com.openjdl.jsf.core.utils.SpringUtils;
import com.openjdl.jsf.webflux.boot.JsfWebFluxProperties;
import org.jetbrains.annotations.NotNull;

/**
 * Created at 2020-08-11 16:17:01
 *
 * @author kidal
 * @since 0.1.0
 */
public class WebFluxServiceImpl implements WebFluxService {
  /**
   *
   */
  @NotNull
  private final JsfWebFluxProperties properties;

  /**
   *
   */
  @NotNull
  private final SpringUtils springUtils;

  /**
   *
   */
  public WebFluxServiceImpl(@NotNull JsfWebFluxProperties properties,
                            @NotNull SpringUtils springUtils) {
    this.registerSelf();
    this.properties = properties;
    this.springUtils = springUtils;
  }

  /**
   *
   */
  @NotNull
  @Override
  public String getJsfServiceName() {
    return "WebFluxService";
  }
}
