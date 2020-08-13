package org.kidal.jsf.core.sugar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kidal.jsf.core.exception.JsfException;
import org.kidal.jsf.core.exception.JsfExceptions;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Created at 2020-08-13 15:53:40
 *
 * @author kidal
 * @since 0.1.0
 */
public class GenericBeanAccessor implements BeanAccessor {
  /**
   *
   */
  @NotNull
  private final BeanPropertyAccessor beanPropertyAccessor;

  /**
   *
   */
  @NotNull
  private final Supplier<RuntimeException> exceptionSupplier;

  /**
   *
   */
  @SuppressWarnings("unchecked")
  public GenericBeanAccessor(@NotNull Object bean, @Nullable Supplier<RuntimeException> exceptionSupplier) {
    this.beanPropertyAccessor = bean instanceof Map
      ? new MapBeanPropertyAccessor((Map<String, ?>) bean)
      : new ObjectBeanPropertyAccessor(bean);
    this.exceptionSupplier = exceptionSupplier != null
      ? exceptionSupplier
      : () -> new JsfException(JsfExceptions.BAD_PARAMETER);
  }

  /**
   *
   */
  @NotNull
  @Override
  public BeanPropertyAccessor getPropertyAccessor() {
    return beanPropertyAccessor;
  }

  /**
   *
   */
  @NotNull
  @Override
  public Supplier<RuntimeException> getExceptionSupplier() {
    return exceptionSupplier;
  }
}
