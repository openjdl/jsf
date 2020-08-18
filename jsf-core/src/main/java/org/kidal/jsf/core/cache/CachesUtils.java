package org.kidal.jsf.core.cache;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;

/**
 * Created at 2020-08-18 21:04:49
 *
 * @author kidal
 * @since 0.1.0
 */
public class CachesUtils {
  /**
   * 创建内存缓存
   *
   * @param type   文档类型.
   * @param loader 文档加载器.
   * @return 文档内存缓存.
   */
  @NotNull
  @Contract("_, _ -> new")
  public static <T, K, S> Caches<T, K, S> newInMemoryCaches(@NonNull Class<T> type, @NonNull CacheLoader<T, K, S> loader) {
    return new CachesInMemory<>(type, loader);
  }

  /**
   *
   */
  private CachesUtils() {
    throw new IllegalStateException();
  }
}
