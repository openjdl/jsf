package com.openjdl.jsf.core.sugar;

import com.openjdl.jsf.core.exception.JsfException;
import com.openjdl.jsf.core.exception.JsfExceptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.convert.ConversionService;

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
  private final ConversionService conversionService;

  /**
   *
   */
  @NotNull
  private final Supplier<RuntimeException> exceptionSupplier;

  /**
   *
   */
  @SuppressWarnings("unchecked")
  public GenericBeanAccessor(@NotNull Object bean, @NotNull ConversionService conversionService, @Nullable Supplier<RuntimeException> exceptionSupplier) {
    this.beanPropertyAccessor = bean instanceof Map
      ? new MapBeanPropertyAccessor((Map<String, ?>) bean)
      : new ObjectBeanPropertyAccessor(bean);
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
  @Nullable
  @Override
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
