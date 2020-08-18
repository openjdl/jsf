package org.kidal.jsf.core.cache;

import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created at 2020-08-18 20:52:27
 *
 * @author kidal
 * @since 0.1.0
 */
public class CacheStaticRegistry {
  /**
   *
   */
  private static final ConcurrentMap<String, LoadingCache<?, ?>> GUAVA_LOADING_CACHE_MAP = Maps.newConcurrentMap();

  /**
   * 获取全部 Guava loading cache.
   */
  @NotNull
  public static Map<String, LoadingCache<?, ?>> unmodifiableGuavaLoadingCacheMap() {
    return Collections.unmodifiableMap(Maps.newHashMap(GUAVA_LOADING_CACHE_MAP));
  }

  /**
   * 注册缓存
   */
  public static LoadingCache<?, ?> register(@NotNull String key, @NotNull LoadingCache<?, ?> cache) {
    return GUAVA_LOADING_CACHE_MAP.put(key, cache);
  }

  /**
   * 注销缓存
   */
  public static boolean deregister(@NotNull String key, @NotNull LoadingCache<?, ?> cache) {
    return GUAVA_LOADING_CACHE_MAP.remove(key, cache);
  }
}
