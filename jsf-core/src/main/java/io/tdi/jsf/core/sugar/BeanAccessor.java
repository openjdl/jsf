package io.tdi.jsf.core.sugar;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.convert.ConversionService;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created at 2020-08-13 14:38:44
 *
 * @author kidal
 * @since 0.1.0
 */
public interface BeanAccessor {
  /**
   * 获取属性访问器
   *
   * @return 属性访问器
   */
  @NotNull
  BeanPropertyAccessor getPropertyAccessor();

  /**
   * 获取错误处理器
   */
  @NotNull
  Supplier<RuntimeException> getExceptionSupplier();

  /**
   * 获取转换服务
   *
   * @return 转换服务
   */
  @Nullable
  default ConversionService getConversionService() {
    return null;
  }

  /**
   * 获取属性
   */
  @NotNull
  default <T> Optional<T> get(@NotNull String propertyName, Class<T> propertyType) {
    Object property = getPropertyAccessor().getProperty(propertyName);

    if (property == null) {
      return Optional.empty();
    }

    ConversionService conversionService = getConversionService();

    if (conversionService == null) {
      //noinspection unchecked
      return Optional.of((T) property);
    }

    if (conversionService.canConvert(property.getClass(), propertyType)) {
      return Optional.ofNullable(conversionService.convert(property, propertyType));
    }

    return Optional.empty();
  }

  /**
   * 获取布尔
   */
  default Optional<Boolean> getBoolean(@NotNull String propertyName) {
    return get(propertyName, Boolean.class);
  }

  /**
   * 获取整数
   */
  default Optional<Integer> getInteger(@NotNull String propertyName) {
    return get(propertyName, Integer.class);
  }

  /**
   * 获取长整数
   */
  default Optional<Long> getLong(@NotNull String propertyName) {
    return get(propertyName, Long.class);
  }

  /**
   * 获取浮点数
   */
  default Optional<Float> getFloat(@NotNull String propertyName) {
    return get(propertyName, Float.class);
  }

  /**
   * 获取双精度浮点数
   */
  default Optional<Double> getDouble(@NotNull String propertyName) {
    return get(propertyName, Double.class);
  }

  /**
   * 获取定点数
   */
  default Optional<BigDecimal> getBigDecimal(@NotNull String propertyName) {
    return get(propertyName, BigDecimal.class);
  }

  /**
   * 获取字符串
   */
  default Optional<String> getString(@NotNull String propertyName) {
    return get(propertyName, String.class);
  }

  /**
   * 获取列表
   */
  @SuppressWarnings("unchecked")
  default <T> Optional<List<T>> getList(@NotNull String propertyName, @NotNull Class<T> elementType) {
    Object property = getPropertyAccessor().getProperty(propertyName);

    if (property == null) {
      return Optional.empty();
    }

    Stream<Object> stream;
    Class<?> incomingElementType;

    if (property instanceof List) {
      List<Object> list = (List<Object>) property;

      if (list.isEmpty()) {
        return Optional.of(Lists.newArrayList());
      }

      stream = Stream.of(list);
      incomingElementType = list.get(0).getClass();
    } else if (property.getClass().isArray()) {
      if (Array.getLength(property) == 0) {
        return Optional.of(Lists.newArrayList());
      }

      stream = Stream.of(property);
      incomingElementType = Array.get(property, 0).getClass();
    } else {
      return Optional.empty();
    }

    ConversionService conversionService = getConversionService();

    if (conversionService == null) {
      return Optional.of((List<T>) stream.collect(Collectors.toList()));
    }

    if (conversionService.canConvert(incomingElementType, elementType)) {
      return Optional.of(stream.map(it -> conversionService.convert(it, elementType)).collect(Collectors.toList()));
    } else {
      return Optional.empty();
    }
  }

  /**
   * 获取属性
   */
  @NotNull
  default <T> T require(@NotNull String propertyName, Class<T> propertyType) {
    return get(propertyName, propertyType).orElseThrow(getExceptionSupplier());
  }

  /**
   * 获取布尔
   */
  default Boolean requireBoolean(@NotNull String propertyName) {
    return getBoolean(propertyName).orElseThrow(getExceptionSupplier());
  }

  /**
   * 获取整数
   */
  default Integer requireInteger(@NotNull String propertyName) {
    return getInteger(propertyName).orElseThrow(getExceptionSupplier());
  }

  /**
   * 获取长整数
   */
  default Long requireLong(@NotNull String propertyName) {
    return getLong(propertyName).orElseThrow(getExceptionSupplier());
  }

  /**
   * 获取浮点数
   */
  default Float requireFloat(@NotNull String propertyName) {
    return getFloat(propertyName).orElseThrow(getExceptionSupplier());
  }

  /**
   * 获取双精度浮点数
   */
  default Double requireDouble(@NotNull String propertyName) {
    return getDouble(propertyName).orElseThrow(getExceptionSupplier());
  }

  /**
   * 获取定点数
   */
  default BigDecimal requireBigDecimal(@NotNull String propertyName) {
    return getBigDecimal(propertyName).orElseThrow(getExceptionSupplier());
  }

  /**
   * 获取字符串
   */
  default String requireString(@NotNull String propertyName) {
    return getString(propertyName).orElseThrow(getExceptionSupplier());
  }

  /**
   * 获取列表
   */
  default <T> List<T> requireList(@NotNull String propertyName, @NotNull Class<T> elementType) {
    return getList(propertyName, elementType).orElseThrow(getExceptionSupplier());
  }

  /**
   * 获取属性
   *
   * @deprecated 使用 {{@link #get(String, Class)}} 方法
   */
  @Deprecated
  @NotNull
  default <T> Optional<T> getAny(@NotNull String propertyName, Class<T> propertyType) {
    return get(propertyName, propertyType);
  }

  /**
   * 获取属性
   *
   * @deprecated 使用 {{@link #require(String, Class)}} 方法
   */
  @Deprecated
  @NotNull
  default <T> T requireAny(@NotNull String propertyName, Class<T> propertyType) {
    return get(propertyName, propertyType).orElseThrow(getExceptionSupplier());
  }
}
