package com.openjdl.jsf.core.cache;

import com.openjdl.jsf.core.pagination.Page;
import com.openjdl.jsf.core.utils.callback.Action1;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created at 2020-08-18 20:44:52
 *
 * @author kidal
 * @since 0.1.0
 */
public interface Caches<T, K, S> {
  /**
   * 类型
   */
  @NotNull
  Class<T> getType();

  /**
   * 加载器
   */
  @NotNull
  CacheLoader<T, K, S> getLoader();

  /**
   * 获取单个文档.
   *
   * @param key 文档唯一ID.
   * @return 与key对用的文档; null: 没有key对应的文档.
   */
  @Nullable
  T get(@NotNull K key);

  /**
   * 获取文档集合
   *
   * @param key 搜索条件
   * @return 与key对应的文档集合.
   */
  @NotNull
  List<T> list(@NotNull S key);

  /**
   * 获取文档集合
   *
   * @param key 搜索条件
   * @return 与key对应的文档集合.
   */
  @NotNull
  Page<T> getPage(@NotNull S key);

  /**
   * 清理
   */
  void purge();

  /**
   * 驱逐单个文档
   *
   * @param key 文档唯一ID.
   */
  void evict(@NotNull K key);

  /**
   * 通知缓存有新的文档被添加.
   *
   * @param key 文档唯一ID.
   */
  void notifyAdded(@NotNull K key);

  /**
   * 通知缓存key对应的文档已更新.
   *
   * @param key 文档唯一ID.
   */
  void notifyUpdated(@NotNull K key);

  /**
   * 通知缓存key对应的文档已删除.
   *
   * @param key 文档唯一ID.
   */
  void notifyRemoved(@NotNull K key);

  /**
   * 监控添加文档操作.
   *
   * @param key      文档唯一ID.
   * @param callable 操作.
   * @return 操作返回值.
   */
  <V> V watchedAdd(@NotNull K key, @NotNull Callable<V> callable);

  /**
   * 监控添加文档操作.
   *
   * @param callable  操作.
   * @param keyGetter 文档唯一ID获取器.
   * @return 操作返回值.
   */
  <V> V watchedAdd(@NotNull Callable<V> callable, @NotNull Action1<K, V> keyGetter);

  /**
   * 监控文档更新.
   *
   * @param key      文档唯一ID.
   * @param callable 操作.
   * @return 操作返回值.
   */
  <V> V watchedUpdated(@NotNull K key, @NotNull Callable<V> callable);

  /**
   * 监控文档移除.
   * ¬
   *
   * @param key      文档唯一ID.
   * @param callable 操作.
   * @return 操作返回值.
   */
  <V> V watchedRemove(@NotNull K key, @NotNull Callable<V> callable);

  /**
   * 获取计算值.
   *
   * @param key 计算值的唯一ID.
   * @return 计算值.
   */
  <V> V getComputedValue(@NotNull CacheComputedValueKey key);
}
