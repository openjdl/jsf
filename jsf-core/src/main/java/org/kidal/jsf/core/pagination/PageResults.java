package org.kidal.jsf.core.pagination;

import org.jetbrains.annotations.NotNull;
import org.kidal.jsf.core.utils.callback.Action1;

import java.util.Arrays;
import java.util.Optional;

/**
 * 分页结果
 *
 * @author kidal
 */
public class PageResults<T> {
  /**
   * 分页参数
   */
  private final PageArgs pageArgs;

  /**
   * 数据总个数
   */
  private final int totalCount;

  /**
   * 当前页的数据
   */
  private final T[] nodes;

  /**
   *
   */
  public PageResults(@NotNull PageArgs pageArgs, int totalCount, @NotNull T[] nodes) {
    this.pageArgs = pageArgs;
    this.totalCount = totalCount;
    this.nodes = nodes;
  }

  /**
   *
   */
  @NotNull
  public <E> PageResults<E> map(@NotNull Action1<E, T> transform) {
    //noinspection unchecked
    return new PageResults<>(
      pageArgs,
      totalCount,
      (E[]) Arrays.stream(nodes)
        .map(transform::call)
        .toArray()
    );
  }

  /**
   *
   */
  @NotNull
  public Optional<T> first() {
    return Optional.ofNullable(isEmpty() ? null : nodes[0]);
  }

  /**
   *
   */
  @NotNull
  public Optional<T> at(int index) {
    return Optional.ofNullable(index >= 0 && index < nodes.length ? nodes[index] : null);
  }

  /**
   *
   */
  public boolean hasNextPage() {
    return totalCount > (pageArgs.getPage() * pageArgs.getLimit());
  }

  /**
   *
   */
  public boolean hasPreviousPage() {
    return pageArgs.getPage() > 1;
  }

  /**
   *
   */
  public boolean isEmpty() {
    return nodes.length == 0;
  }

  /**
   *
   */
  public boolean isNotEmpty() {
    return nodes.length > 0;
  }

  /**
   *
   */
  @NotNull
  public PageArgs getPageArgs() {
    return pageArgs;
  }

  /**
   *
   */
  public int getTotalCount() {
    return totalCount;
  }

  /**
   *
   */
  @NotNull
  public T[] getNodes() {
    return nodes;
  }
}
