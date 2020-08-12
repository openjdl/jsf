package org.kidal.jsf.webflux.websocket;

import org.jetbrains.annotations.NotNull;
import org.kidal.jsf.core.utils.MapLikeArgumentsGetter;

import java.util.Map;

/**
 * Created at 2020-08-12 22:26:43
 *
 * @author kidal
 * @since 0.1.0
 */
public class WebSocketMessageHandlingContext {
  /**
   *
   */
  @NotNull
  private final SessionManager sessionManager;

  /**
   *
   */
  @NotNull
  private final Session session;

  /**
   *
   */
  @NotNull
  private final Payload payload;

  /**
   *
   */
  @NotNull
  private final MapLikeArgumentsGetter parameters;

  /**
   *
   */
  public WebSocketMessageHandlingContext(@NotNull SessionManager sessionManager,
                                         @NotNull Session session,
                                         @NotNull Payload payload) {
    this.sessionManager = sessionManager;
    this.session = session;
    this.payload = payload;

    if (payload.getData() != null && payload.getData() instanceof Map) {
      //noinspection unchecked
      parameters = new MapLikeArgumentsGetter(
        (Map<String, Object>) payload.getData(),
        sessionManager.getConversionService(),
        null
      );
    } else {
      parameters = new MapLikeArgumentsGetter(
        new MapLikeArgumentsGetter.DataHolder() {
          @Override
          public <T> T getArgument(@NotNull String key) {
            return null;
          }
        }
        ,
        sessionManager.getConversionService(),
        null
      );
    }
  }

  //--------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------

  @NotNull
  public SessionManager getSessionManager() {
    return sessionManager;
  }

  @NotNull
  public Session getSession() {
    return session;
  }

  @NotNull
  public Payload getPayload() {
    return payload;
  }

  @NotNull
  public MapLikeArgumentsGetter getParameters() {
    return parameters;
  }
}
