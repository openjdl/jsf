package org.kidal.jsf.core.sugar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kidal.jsf.core.exception.JsfException;
import org.kidal.jsf.core.exception.JsfExceptions;

import java.util.function.Supplier;

/**
 * Created at 2020-08-13 16:03:40
 *
 * @author kidal
 * @since 1.0.0
 */
public class EmptyBeanAccessor implements BeanAccessor {
  /**
   *
   */
  @NotNull
  private final BeanPropertyAccessor beanPropertyAccessor = new BeanPropertyAccessor() {
    @Nullable
    @Override
    public <T> T getProperty(@NotNull String propertyName) {
      return null;
    }
  };


  /**
   *
   */
  @NotNull
  private final Supplier<RuntimeException> exceptionSupplier;

  /**
   *
   */
  public EmptyBeanAccessor(@Nullable Supplier<RuntimeException> exceptionSupplier) {
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
