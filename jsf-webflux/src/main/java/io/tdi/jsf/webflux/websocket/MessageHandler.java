package io.tdi.jsf.webflux.websocket;

import io.tdi.jsf.webflux.websocket.annotation.WebSocketParameters;
import org.jetbrains.annotations.NotNull;
import io.tdi.jsf.core.cipher.UserIdentificationNumber;
import io.tdi.jsf.core.pagination.PageArgs;
import io.tdi.jsf.core.unify.UnifiedApiContext;
import io.tdi.jsf.core.utils.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created at 2020-08-12 10:39:04
 *
 * @author kidal
 * @since 0.1.0
 */
public class MessageHandler {
  /**
   * 目标
   */
  @NotNull
  private final Object bean;

  /**
   * 方法
   */
  @NotNull
  private final Method method;

  /**
   *
   */
  public MessageHandler(@NotNull Object bean, @NotNull Method method) {
    this.bean = bean;
    this.method = method;
  }

  /**
   * 处理消息
   */
  public Object handle(@NotNull WebSocketMessageHandlingContext context) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
    // 参数
    Object[] parameters = new Object[method.getParameterCount()];
    for (int i = 0; i < method.getParameterCount(); i++) {
      Class<?> type = method.getParameterTypes()[i];
      if (type == WebSocketMessageHandlingContext.class) {
        parameters[i] = context;
      } else if (type == PageArgs.class) {
        parameters[i] = context.getPageArgs();
      } else if (type == UserIdentificationNumber.class) {
        parameters[i] = context.getSession().isSignedIn()
          ? context.getSession().getUin()
          : null;
      } else if (type == Session.class) {
        parameters[i] = context.getSession();
      } else if (type == UnifiedApiContext.class) {
        parameters[i] = new UnifiedApiContext(context, context.getParameters());
      } else {
        Annotation annotation = Stream.of(method.getParameterAnnotations()[i]).findFirst().orElse(null);

        if (annotation instanceof WebSocketParameters) {
          if (context.getPayload().getData() instanceof Map) {
            //noinspection unchecked
            parameters[i] = ReflectionUtils.mapToPojo(
              (Map<String, Object>) context.getPayload().getData(),
              type,
              context.getSessionManager().getConversionService()
            );
          }
        }
      }
    }

    return method.invoke(bean, parameters);
  }

  //--------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------

  @NotNull
  public Object getBean() {
    return bean;
  }

  @NotNull
  public Method getMethod() {
    return method;
  }
}
