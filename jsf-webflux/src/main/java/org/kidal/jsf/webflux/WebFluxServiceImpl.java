package org.kidal.jsf.webflux;

import org.jetbrains.annotations.NotNull;
import org.kidal.jsf.core.utils.SpringUtils;
import org.kidal.jsf.webflux.boot.JsfWebFluxProperties;

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
  @Override
  public void initializeJsfService() throws Exception {

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
