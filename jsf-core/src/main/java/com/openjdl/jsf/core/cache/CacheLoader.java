package com.openjdl.jsf.core.cache;

import com.openjdl.jsf.core.pagination.Page;
import com.openjdl.jsf.core.pagination.PageArgs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Created at 2020-08-18 20:47:00
 *
 * @author kidal
 * @since 0.1.0
 */
public interface CacheLoader<T, K, S> {
  /**
   * 加载文档.
   *
   * @param key 文档唯一ID.
   * @return 与key对应的文档.
   */
  @Nullable
  default T loadSingle(@NotNull K key) {
    return null;
  }

  /**
   * 加载列表
   */
  @NotNull
  default List<T> loadList(@NotNull S key) {
    return Collections.emptyList();
  }

  /**
   * 加载文档集合.
   *
   * @param key 搜索条件.
   * @return 与key对应的集合.
   */
  @NotNull
  default Page<T> loadPage(@NotNull S key) {
    return Page.of(PageArgs.of(PageArgs.DEFAULT_PAGE), 0, Collections.emptyList());
  }

  /**
   * 加载计算值.
   *
   * @param key 计算值的唯一ID.
   * @return 计算的结果.
   */
  @Nullable
  default Object loadComputedValue(CacheComputedValueKey key) {
    return null;
  }
}
