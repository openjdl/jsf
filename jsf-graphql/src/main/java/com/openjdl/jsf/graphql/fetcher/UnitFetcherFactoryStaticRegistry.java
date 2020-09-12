package com.openjdl.jsf.graphql.fetcher;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Created at 2020-08-05 21:51:30
 *
 * @author kidal
 * @since 0.1.0
 */
public class UnitFetcherFactoryStaticRegistry {
  /**
   *
   */
  private static final Map<Class<? extends BaseUnitFetcherFactory>, BaseUnitFetcherFactory> FETCHERS = Maps.newHashMap();

  /**
   * 注册
   */
  public static void register(@NotNull BaseUnitFetcherFactory factory) {
    FETCHERS.put(factory.getClass(), factory);
  }

  /**
   * 注销
   */
  public static void deregister(@NotNull BaseUnitFetcherFactory factory) {
    FETCHERS.remove(factory.getClass());
  }

  /**
   * 获取
   */
  @Nullable
  public static BaseUnitFetcherFactory get(@NotNull Class<? extends BaseUnitFetcherFactory> type) {
    return FETCHERS.get(type);
  }

  /**
   *
   */
  private UnitFetcherFactoryStaticRegistry() {
    throw new IllegalStateException("UnitFetcherFactoryStaticRegistry is a static class");
  }
}
