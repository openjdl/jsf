package io.tdi.jsf.webflux;

import io.tdi.jsf.webflux.boot.JsfWebFluxProperties;
import org.jetbrains.annotations.NotNull;
import io.tdi.jsf.core.utils.SpringUtils;

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
