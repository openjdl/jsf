package com.openjdl.jsf.settings;

import com.openjdl.jsf.core.observe.TaggedObservable;
import com.openjdl.jsf.core.observe.TaggedObserver;
import com.openjdl.jsf.core.utils.ReflectionUtils;
import com.openjdl.jsf.settings.event.SettingsStoragePropertiesChangedEventArgs;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

import java.lang.reflect.Field;
import java.text.MessageFormat;

/**
 * Created at 2020-09-11 17:02:34
 *
 * @author kidal
 * @since 0.3
 */
public class SettingsValueObserverProxy implements TaggedObserver, Ordered {
  private static final Logger LOG = LoggerFactory.getLogger(SettingsValueObserverProxy.class);
  private final Object target;
  private final Field field;
  private final InjectSettingsValueProvider provider;
  private final Object key;

  /**
   *
   */
  public SettingsValueObserverProxy(Object target, Field field, InjectSettingsValueProvider provider, Object key) {
    this.target = target;
    this.field = field;
    this.provider = provider;
    this.key = key;
  }

  /**
   *
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  private void inject(SettingsStorage storage, SettingsStoragePropertiesChangedEventArgs event) {
    if (event.hasChanged() && event.getChangedProperties().containsKey(key)) {
      Object value = storage.get(key);
      if (value == null && provider.isRequired()) {
        throw new IllegalStateException(MessageFormat.format(
          "Inject configuration value {0}.{1} failed: value not found", field.getType(), key));
      }

      ReflectionUtils.makeAccessible(field);
      ReflectionUtils.setField(field, target, value);

      LOG.debug("Inject value: {}.{} = {}",
        target.getClass().getName(), field.getName(), value);
    }
  }

  /**
   *
   */
  @SuppressWarnings("rawtypes")
  @Override
  public void onObservableChanged(@NotNull TaggedObservable o, @NotNull String tag, @NotNull Object arg) {
    if (SettingsObserveTags.PROPERTY_CHANGED.equals(tag)) {
      inject((SettingsStorage) o, (SettingsStoragePropertiesChangedEventArgs) arg);
    }
  }

  /**
   *
   */
  @Override
  public int getOrder() {
    return HIGHEST_PRECEDENCE;
  }
}
