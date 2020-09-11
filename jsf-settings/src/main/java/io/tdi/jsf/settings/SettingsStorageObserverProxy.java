package io.tdi.jsf.settings;

import io.tdi.jsf.core.observe.TaggedObservable;
import io.tdi.jsf.core.observe.TaggedObserver;
import io.tdi.jsf.core.utils.ReflectionUtils;
import io.tdi.jsf.settings.event.SettingsObserverEventArgs;
import io.tdi.jsf.settings.event.SettingsStoragePropertiesChangedEventArgs;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.text.MessageFormat;

/**
 * Created at 2020-09-11 16:14:35
 *
 * @author kidal
 * @since 0.3
 */
public class SettingsStorageObserverProxy implements TaggedObserver, Ordered {
  private static final Logger LOG = LoggerFactory.getLogger(SettingsStorageObserverProxy.class);
  private final Object target;
  private final Method method;
  private final Parameter[] parameters;
  private final String[] keys;
  private final int order;

  /**
   *
   */
  public SettingsStorageObserverProxy(Object target, Method method, String[] keys, int order) {
    ReflectionUtils.makeAccessible(method);

    this.target = target;
    this.method = method;
    this.parameters = method.getParameters();
    this.keys = keys;
    this.order = order;
  }

  /**
   *
   */
  private void checkReturn() {
    if (method.getReturnType() != void.class && method.getReturnType() != Void.class) {
      throw new IllegalStateException(MessageFormat.format(
        "Incorrect configuration observer method {0}.{1}: must return void",
        target.getClass().getName(), method.getName()));
    }
  }

  /**
   *
   */
  private void checkParameters() {
    for (Parameter parameter : parameters) {
      if (
        !SettingsStorage.class.isAssignableFrom(parameter.getType())
          && !SettingsObserverEventArgs.class.isAssignableFrom(parameter.getType())
          && parameter.getType() != String.class
      ) {
        throw new IllegalStateException(MessageFormat.format(
          "Incorrect configuration observer method {0}.{1}",
          target.getClass().getName(), method.getName()));
      }
    }
  }

  /**
   *
   */
  private void invoke(Object storage, String tag, Object arg) throws InvocationTargetException, IllegalAccessException {
    if (keys.length > 0 && SettingsObserveTags.AFTER_REFRESH.equals(tag)) {
      @SuppressWarnings("rawtypes")
      SettingsStoragePropertiesChangedEventArgs e = (SettingsStoragePropertiesChangedEventArgs) arg;
      //noinspection unchecked
      if (e.getChangedProperties().keySet().stream().noneMatch(key -> ArrayUtils.contains(keys, key))) {
        return;
      }
    }

    if (parameters.length == 0) {
      method.invoke(isStatic() ? null : target);
    } else {
      Object[] arguments = new Object[parameters.length];
      for (int i = 0; i < parameters.length; i++) {
        if (parameters[i].getType().isAssignableFrom(storage.getClass())) {
          arguments[i] = storage;
        } else if (arg != null && parameters[i].getType().isAssignableFrom(arg.getClass())) {
          arguments[i] = arg;
        } else if (parameters[i].getType() == String.class) {
          arguments[i] = tag;
        } else {
          arguments[i] = null;
        }
      }
      method.invoke(isStatic() ? null : target, arguments);
    }

    LOG.debug("Invoked observer method: {}.{}", target.getClass().getName(), method.getName());
  }

  /**
   *
   */
  @Override
  public void onObservableChanged(@NotNull TaggedObservable o, @NotNull String tag, @NotNull Object arg) {
    try {
      invoke(o, tag, arg);
    } catch (InvocationTargetException | IllegalAccessException e) {
      LOG.error("An exception has occurred while invoking configuration observer method {}.{}",
        target.getClass().getName(), method.getName(), e);
    }
  }

  /**
   *
   */
  public boolean isStatic() {
    return Modifier.isStatic(method.getModifiers());
  }

  /**
   *
   */
  public Object getTarget() {
    return target;
  }

  /**
   *
   */
  public Method getMethod() {
    return method;
  }

  /**
   *
   */
  public Parameter[] getParameters() {
    return parameters;
  }

  /**
   *
   */
  public String[] getKeys() {
    return keys;
  }

  /**
   *
   */
  @Override
  public int getOrder() {
    return order;
  }
}
