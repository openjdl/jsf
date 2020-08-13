package org.kidal.jsf.webflux.websocket;

import org.jetbrains.annotations.NotNull;
import org.kidal.jsf.core.exception.JsfException;
import org.kidal.jsf.core.exception.JsfExceptions;
import org.kidal.jsf.core.sugar.BeanAccessor;
import org.kidal.jsf.core.sugar.BeanPropertyAccessor;
import org.kidal.jsf.core.sugar.EmptyBeanAccessor;
import org.kidal.jsf.core.sugar.GenericBeanAccessor;

import java.util.function.Supplier;

/**
 * Created at 2020-08-12 22:26:43
 *
 * @author kidal
 * @since 0.1.0
 */
public class WebSocketMessageHandlingContext implements BeanAccessor {
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
  private final BeanAccessor parameters;

  /**
   *
   */
  public WebSocketMessageHandlingContext(@NotNull SessionManager sessionManager,
                                         @NotNull Session session,
                                         @NotNull Payload payload) {
    this.sessionManager = sessionManager;
    this.session = session;
    this.payload = payload;

    if (payload.getData() != null) {
      parameters = new GenericBeanAccessor(payload.getData(), () -> new JsfException(JsfExceptions.BAD_REQUEST));
    } else {
      parameters = new EmptyBeanAccessor(() -> new JsfException(JsfExceptions.BAD_REQUEST));
    }
  }

  //--------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------

  @NotNull
  @Override
  public BeanPropertyAccessor getPropertyAccessor() {
    return getParameters().getPropertyAccessor();
  }

  @NotNull
  @Override
  public Supplier<RuntimeException> getExceptionSupplier() {
    return getParameters().getExceptionSupplier();
  }

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
  public BeanAccessor getParameters() {
    return parameters;
  }
}
