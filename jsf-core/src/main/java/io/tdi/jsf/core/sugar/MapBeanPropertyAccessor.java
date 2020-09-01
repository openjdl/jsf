package io.tdi.jsf.core.sugar;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Created at 2020-08-13 15:52:00
 *
 * @author kidal
 * @since 0.1.0
 */
public class MapBeanPropertyAccessor implements BeanPropertyAccessor {
  /**
   *
   */
  @NotNull
  private final Map<String, ?> bean;

  /**
   *
   */
  public MapBeanPropertyAccessor(@NotNull Map<String, ?> bean) {
    this.bean = bean;
  }

  /**
   *
   */
  @SuppressWarnings("unchecked")
  @Override
  public <T> T getProperty(@NotNull String propertyName) {
    return (T) bean.get(propertyName);
  }
}
