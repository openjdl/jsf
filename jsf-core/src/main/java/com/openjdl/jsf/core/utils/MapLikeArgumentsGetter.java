package com.openjdl.jsf.core.utils;

import com.openjdl.jsf.core.exception.JsfException;
import com.openjdl.jsf.core.exception.JsfExceptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.convert.ConversionService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created at 2020-08-11 18:12:51
 *
 * @author kidal
 * @since 0.1.0
 */
public class MapLikeArgumentsGetter {

  /**
   *
   */
  public interface DataHolder {
    <T> T getArgument(@NotNull String key);
  }

  /**
   *
   */
  @NotNull
  private final DataHolder dataHolder;

  /**
   *
   */
  @NotNull
  private final ConversionService conversionService;

  /**
   *
   */
  @NotNull
  private final Supplier<?> exceptionSupplier;

  /**
   *
   */
  public MapLikeArgumentsGetter(@NotNull DataHolder dataHolder,
                                @NotNull ConversionService conversionService,
                                @Nullable Supplier<?> exceptionSupplier) {
    this.dataHolder = dataHolder;
    this.conversionService = conversionService;
    this.exceptionSupplier = exceptionSupplier == null ? () -> new JsfException(JsfExceptions.BAD_REQUEST) : exceptionSupplier;
  }

  /**
   *
   */
  public MapLikeArgumentsGetter(@NotNull Map<String, Object> dataHolder,
                                @NotNull ConversionService conversionService,
                                @Nullable Supplier<?> exceptionSupplier) {
    this(
      new DataHolder() {
        @SuppressWarnings("unchecked")
        @Override
        public <T> T getArgument(@NotNull String key) {
          return (T) dataHolder.get(key);
        }
      },
      conversionService,
      exceptionSupplier
    );
  }

  /**
   *
   */
  public <T> Optional<T> getAny(@NotNull String name, Class<T> type) {
    Object argument = dataHolder.getArgument(name);
    if (argument == null) {
      return Optional.empty();
    }
    if (conversionService.canConvert(argument.getClass(), type)) {
      return Optional.ofNullable(conversionService.convert(argument, type));
    } else {
      return Optional.empty();
    }
  }

  /**
   *
   */
  public Optional<Boolean> getBoolean(String name) {
    return getAny(name, Boolean.class);
  }

  /**
   *
   */
  public Optional<Integer> getInteger(String name) {
    return getAny(name, Integer.class);
  }

  /**
   *
   */
  public Optional<Long> getLong(String name) {
    return getAny(name, Long.class);
  }

  /**
   *
   */
  public Optional<String> getString(String name) {
    return getAny(name, String.class);
  }

  /**
   *
   */
  @SuppressWarnings("unchecked")
  public <T> Optional<List<T>> getList(String name, Class<T> contentClass) {
    Object argument = dataHolder.getArgument(name);
    if (argument == null) {
      return Optional.empty();
    }

    if (!(argument instanceof List)) {
      return Optional.empty();
    }
    List<Object> list = (List<Object>) argument;
    if (list.isEmpty()) {
      return Optional.of(Collections.emptyList());
    }

    Object firstElement = list.get(0);

    if (conversionService.canConvert(firstElement.getClass(), contentClass)) {
      List<T> converted = list
        .stream()
        .map(it -> conversionService.convert(it, contentClass))
        .collect(Collectors.toList());
      return Optional.of(converted);
    } else {
      return Optional.empty();
    }
  }

  /**
   *
   */
  public boolean requireBoolean(String name) {
    return getBoolean(name).orElseThrow(() -> new JsfException(JsfExceptions.BAD_REQUEST));
  }

  /**
   *
   */
  public int requireInteger(String name) {
    return getInteger(name).orElseThrow(() -> new JsfException(JsfExceptions.BAD_REQUEST));
  }

  /**
   *
   */
  public long requireLong(String name) {
    return getLong(name).orElseThrow(() -> new JsfException(JsfExceptions.BAD_REQUEST));
  }

  /**
   *
   */
  @NotNull
  public String requireString(String name) {
    return getString(name).orElseThrow(() -> new JsfException(JsfExceptions.BAD_REQUEST));
  }

  /**
   *
   */
  @NotNull
  public <T> T requireAny(String name, Class<T> type) {
    return getAny(name, type).orElseThrow(() -> new JsfException(JsfExceptions.BAD_REQUEST));
  }

  /**
   *
   */
  @NotNull
  public <T> List<T> requireList(String name, Class<T> contentClass) {
    return getList(name, contentClass).orElseThrow(() -> new JsfException(JsfExceptions.BAD_REQUEST));
  }

  //--------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------


  @NotNull
  public DataHolder getDataHolder() {
    return dataHolder;
  }

  @NotNull
  public ConversionService getConversionService() {
    return conversionService;
  }

  @NotNull
  public Supplier<?> getExceptionSupplier() {
    return exceptionSupplier;
  }
}
