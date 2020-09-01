package io.tdi.jsf.core.sugar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import io.tdi.jsf.core.exception.JsfException;
import io.tdi.jsf.core.exception.JsfExceptions;
import org.springframework.core.convert.ConversionService;

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
  private final ConversionService conversionService;

  /**
   *
   */
  @NotNull
  private final Supplier<RuntimeException> exceptionSupplier;

  /**
   *
   */
  public EmptyBeanAccessor(@NotNull ConversionService conversionService, @Nullable Supplier<RuntimeException> exceptionSupplier) {
    this.conversionService = conversionService;
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
  @Override
  @NotNull
  public ConversionService getConversionService() {
    return conversionService;
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
