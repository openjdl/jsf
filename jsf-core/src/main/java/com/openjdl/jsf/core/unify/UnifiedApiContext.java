package com.openjdl.jsf.core.unify;

import com.openjdl.jsf.core.sugar.BeanAccessor;
import com.openjdl.jsf.core.sugar.BeanPropertyAccessor;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Created at 2020-08-13 16:10:23
 *
 * @author kidal
 * @since 0.1.0
 */
public class UnifiedApiContext implements BeanAccessor {
  /**
   * 原始上下文
   */
  @NotNull
  private final Object originalContext;

  /**
   * 参数查询器
   */
  @NotNull
  private final BeanAccessor parameters;

  /**
   *
   */
  public UnifiedApiContext(@NotNull Object originalContext, @NotNull BeanAccessor parameters) {
    this.originalContext = originalContext;
    this.parameters = parameters;
  }

  /**
   *
   */
  @NotNull
  @Override
  public BeanPropertyAccessor getPropertyAccessor() {
    return parameters.getPropertyAccessor();
  }

  /**
   *
   */
  @NotNull
  @Override
  public Supplier<RuntimeException> getExceptionSupplier() {
    return parameters.getExceptionSupplier();
  }

  /**
   *
   */
  public boolean is(@NotNull Class<?> originalContextType) {
    return originalContextType.isAssignableFrom(originalContext.getClass());
  }

  /**
   *
   */
  @NotNull
  public Object getOriginalContext() {
    return originalContext;
  }

  /**
   *
   */
  @NotNull
  public <T> T getOriginalContext(@NotNull Class<T> originalContextType) {
    //noinspection unchecked
    return (T) originalContext;
  }

  /**
   *
   */
  @NotNull
  public BeanAccessor getParameters() {
    return parameters;
  }
}
