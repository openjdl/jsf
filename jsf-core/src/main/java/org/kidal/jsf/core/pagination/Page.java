package org.kidal.jsf.core.pagination;

import com.google.common.base.Objects;
import org.jetbrains.annotations.NotNull;
import org.kidal.jsf.core.utils.callback.Action0;
import org.kidal.jsf.core.utils.callback.Action1;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Created at 2020-08-04 17:30:00
 *
 * @author kidal
 * @since 0.1.0
 */
public class Page<T> {
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
  private final List<T> nodes;

  /**
   *
   */
  @NotNull
  public static <E> Page<E> of(@NotNull PageArgs pageArgs, int totalCount, @NotNull List<E> nodes) {
    return new Page<>(pageArgs, totalCount, nodes);
  }

  /**
   *
   */
  @NotNull
  public static <E> Page<E> of(@NotNull PageArgs pageArgs,
                               @NotNull Action1<Integer, PageArgs> counter,
                               @NotNull Action1<List<E>, PageArgs> selector) {
    // 先计数
    int totalCount = counter.call(pageArgs);

    // 分页
    List<E> nodes = pageArgs.getLimit() > 0 ? selector.call(pageArgs) : Collections.emptyList();

    // done
    return Page.of(pageArgs, totalCount, nodes);
  }

  /**
   *
   */
  @NotNull
  public static <E> Page<E> of(@NotNull PageArgs pageArgs,
                               @NotNull Action0<Integer> counter,
                               @NotNull Action0<List<E>> selector) {
    // 先计数
    int totalCount = counter.call();

    // 分页
    List<E> nodes = pageArgs.getLimit() > 0 ? selector.call() : Collections.emptyList();

    // done
    return Page.of(pageArgs, totalCount, nodes);
  }

  /**
   *
   */
  public Page(@NotNull PageArgs pageArgs, int totalCount, @NotNull List<T> nodes) {
    this.pageArgs = pageArgs;
    this.totalCount = totalCount;
    this.nodes = nodes;
  }

  /**
   *
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Page<?> page = (Page<?>) o;
    return totalCount == page.totalCount &&
      Objects.equal(pageArgs, page.pageArgs) &&
      Objects.equal(nodes, page.nodes);
  }

  /**
   *
   */
  @Override
  public int hashCode() {
    return Objects.hashCode(pageArgs, totalCount, nodes);
  }

  /**
   *
   */
  @NotNull
  public <E> Page<E> map(@NotNull Action1<E, T> transform) {
    return new Page<>(
      pageArgs,
      totalCount,
      nodes.stream().map(transform::call).collect(Collectors.toList())
    );
  }

  /**
   *
   */
  @NotNull
  public Optional<T> first() {
    return Optional.ofNullable(isEmpty() ? null : nodes.get(0));
  }

  /**
   *
   */
  @NotNull
  public Optional<T> at(int index) {
    return Optional.ofNullable(index >= 0 && index < nodes.size() ? nodes.get(index) : null);
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
    return nodes.size() == 0;
  }

  /**
   *
   */
  public boolean isNotEmpty() {
    return nodes.size() > 0;
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
  public List<T> getNodes() {
    return nodes;
  }
}
