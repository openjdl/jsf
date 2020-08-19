package org.kidal.jsf.core.cache;

import org.jetbrains.annotations.NotNull;
import org.kidal.jsf.core.pagination.Page;
import org.kidal.jsf.core.utils.ReflectionUtils;
import org.kidal.jsf.core.utils.StringUtils;
import org.kidal.jsf.core.utils.callback.Action1;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created at 2020-08-18 20:54:06
 *
 * @author kidal
 * @since 0.1.0
 */
public class CachesInMemory<T, K, S>
  implements Caches<T, K, S> {
  /**
   * 缓存时效时间.
   */
  public static final long EXPIRE_DURATION = 30L;

  /**
   * 缓存失效时间单位.
   */
  public static final TimeUnit EXPIRE_TIME_UNIT = TimeUnit.MINUTES;

  /**
   * 文档类型.
   */
  private final Class<T> type;

  /**
   * 文档加载器.
   */
  private final CacheLoader<T, K, S> loader;

  /**
   * 单文档缓存.
   */
  private final com.google.common.cache.LoadingCache<K, Optional<T>> single;

  /**
   * 单文档缓存.
   */
  private final com.google.common.cache.LoadingCache<S, List<T>> list;

  /**
   * 集合缓存.
   */
  private final com.google.common.cache.LoadingCache<S, Page<T>> page;

  /**
   * 计算值缓存.
   */
  private final com.google.common.cache.LoadingCache<CacheComputedValueKey, Optional<Object>> computedValues;

  /**
   * ctor.
   */
  public CachesInMemory(Class<T> type, CacheLoader<T, K, S> loader) {
    this.type = type;
    this.loader = loader;

    this.single = com.google.common.cache.CacheBuilder.newBuilder()
      .recordStats()
      .expireAfterWrite(EXPIRE_DURATION, EXPIRE_TIME_UNIT)
      .build(com.google.common.cache.CacheLoader.from((key) ->
        Optional.ofNullable(loader.loadSingle(Objects.requireNonNull(key)))
      ));
    this.page = com.google.common.cache.CacheBuilder.newBuilder()
      .recordStats()
      .expireAfterWrite(EXPIRE_DURATION, EXPIRE_TIME_UNIT)
      .build(com.google.common.cache.CacheLoader.from(loader::loadPage));
    this.list = com.google.common.cache.CacheBuilder.newBuilder()
      .recordStats()
      .expireAfterWrite(EXPIRE_DURATION, EXPIRE_TIME_UNIT)
      .build(com.google.common.cache.CacheLoader.from(loader::loadList));
    this.computedValues = com.google.common.cache.CacheBuilder.newBuilder()
      .recordStats()
      .expireAfterWrite(EXPIRE_DURATION, EXPIRE_TIME_UNIT)
      .build(com.google.common.cache.CacheLoader.from((key) ->
        Optional.ofNullable(loader.loadComputedValue(key))
      ));

    final String prefix = StringUtils.lowercaseAndSplitWith(type.getSimpleName(), "-");
    CacheStaticRegistry.register(prefix + ".single", this.single);
    CacheStaticRegistry.register(prefix + ".page", this.page);
    CacheStaticRegistry.register(prefix + ".list", this.list);
    CacheStaticRegistry.register(prefix + ".computed-values", this.computedValues);
  }

  @NotNull
  @Override
  public Class<T> getType() {
    return type;
  }

  @NotNull
  @Override
  public CacheLoader<T, K, S> getLoader() {
    return loader;
  }

  @Override
  public T get(@NonNull K key) {
    return single.getUnchecked(key).orElse(null);
  }

  @NotNull
  @Override
  public List<T> list(@NotNull S key) {
    return list.getUnchecked(key);
  }

  @NonNull
  @Override
  public Page<T> getPage(@NonNull S key) {
    return page.getUnchecked(key);
  }

  @Override
  public void purge() {
    single.invalidateAll();
    page.invalidateAll();
    list.invalidateAll();
    computedValues.invalidateAll();
  }

  @Override
  public void evict(@NonNull K key) {
    single.invalidate(key);
    page.invalidateAll();
    list.invalidateAll();
    computedValues.invalidateAll();
  }

  @Override
  public void notifyAdded(@NonNull K key) {
    single.invalidate(key);
    page.invalidateAll();
    list.invalidateAll();
    computedValues.invalidateAll();
  }

  @Override
  public void notifyUpdated(@NonNull K key) {
    single.invalidate(key);
    page.invalidateAll();
    list.invalidateAll();
    computedValues.invalidateAll();
  }

  @Override
  public void notifyRemoved(@NonNull K key) {
    notifyUpdated(key);
  }

  @Override
  public <V> V watchedAdd(@NonNull K key, @NonNull Callable<V> callable) {
    V r = null;
    try {
      r = callable.call();
    } catch (Exception ex) {
      ReflectionUtils.rethrowRuntimeException(ex);
    }
    notifyAdded(key);
    return r;
  }

  @Override
  public <V> V watchedAdd(@NonNull Callable<V> callable, @NonNull Action1<K, V> keyGetter) {
    V r = null;
    try {
      r = callable.call();
    } catch (Exception ex) {
      ReflectionUtils.rethrowRuntimeException(ex);
    }
    K key = keyGetter.call(r);
    notifyAdded(key);
    return r;
  }

  @Override
  public <V> V watchedUpdated(@NonNull K key, @NonNull Callable<V> callable) {
    V r = null;
    try {
      r = callable.call();
    } catch (Exception ex) {
      ReflectionUtils.rethrowRuntimeException(ex);
    }
    notifyUpdated(key);
    return r;
  }

  @Override
  public <V> V watchedRemove(@NonNull K key, @NonNull Callable<V> callable) {
    return watchedUpdated(key, callable);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <V> V getComputedValue(@NonNull CacheComputedValueKey key) {
    return (V) computedValues.getUnchecked(key).orElse(null);
  }
}
