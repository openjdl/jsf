package com.openjdl.jsf.core.pagination;

import com.google.common.base.Objects;
import com.openjdl.jsf.core.utils.MathUtils;
import com.openjdl.jsf.core.utils.callback.Action1;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;


/**
 * Created at 2020-08-04 17:30:00
 *
 * @author kidal
 * @since 0.1.0
 */
public class PageArgs {
  /**
   * 默认页码
   */
  public static final int DEFAULT_PAGE = 1;

  /**
   * 默认每页个数
   */
  public static final int DEFAULT_LIMIT = 10;

  /**
   * 每页最大个数
   */
  public static final int MAX_LIMIT = 2000;

  /**
   * 页码
   */
  private int page = DEFAULT_PAGE;

  /**
   * 每页最大个数
   */
  private int limit = DEFAULT_LIMIT;

  /**
   * 排序参数
   */
  private List<PageSortArg> sorts;

  /**
   * 空分页
   *
   * @return 分页参数
   */
  @NotNull
  public static PageArgs ofZero() {
    return new PageArgs(DEFAULT_PAGE, 0, Collections.emptyList());
  }

  /**
   * 一个元素
   *
   * @return 分页参数
   */
  @NotNull
  public static PageArgs ofOne() {
    return new PageArgs(DEFAULT_PAGE, 1, Collections.emptyList());
  }

  /**
   * 全部元素
   *
   * @return 分页参数
   */
  @NotNull
  public static PageArgs ofMax() {
    return new PageArgs(DEFAULT_PAGE, MAX_LIMIT, Collections.emptyList());
  }

  /**
   * 默认页个数分页
   *
   * @return 分页参数
   */
  @NotNull
  public static PageArgs of(int page) {
    return new PageArgs(page, DEFAULT_LIMIT, Collections.emptyList());
  }

  /**
   * 分页
   *
   * @return 分页参数
   */
  @NotNull
  public static PageArgs of(int page, int limit) {
    return new PageArgs(page, limit, Collections.emptyList());
  }

  /**
   * 分页
   *
   * @return 分页参数
   */
  @NotNull
  public static PageArgs of(int page, int limit, @NotNull List<PageSortArg> sorts) {
    return new PageArgs(page, limit, sorts);
  }

  /**
   *
   */
  public PageArgs() {

  }

  /**
   *
   */
  public PageArgs(int page, int limit, @NotNull List<PageSortArg> sorts) {
    setPage(page);
    setLimit(limit);
    this.sorts = sorts;
  }

  /**
   *
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PageArgs pageArgs = (PageArgs) o;
    return page == pageArgs.page &&
      limit == pageArgs.limit &&
      Objects.equal(sorts, pageArgs.sorts);
  }

  /**
   *
   */
  @Override
  public int hashCode() {
    return Objects.hashCode(page, limit, sorts);
  }

  /**
   *
   */
  @Override
  public String toString() {
    return "PageArgs{" +
      "page=" + page +
      ", limit=" + limit +
      ", sorts=" + sorts +
      '}';
  }

  /**
   * 遍历数据
   */
  public <T> void forEach(@NotNull Action1<Page<T>, PageArgs> loader,
                          @NotNull Action1<Void, Page<T>> consumer) {
    PageArgs pageArgs = new PageArgs(getPage(), getLimit(), getSorts());
    Page<T> results;
    do {
      results = loader.call(pageArgs);
      consumer.call(results);
      pageArgs = new PageArgs(pageArgs.getPage() + 1, pageArgs.getLimit(), pageArgs.getSorts());
    } while (results.hasNextPage());
  }

  /**
   *
   */
  public int getPage() {
    return page;
  }

  /**
   *
   */
  public void setPage(int page) {
    this.page = MathUtils.clamp(page, 1, Integer.MAX_VALUE);
  }

  /**
   *
   */
  public int getLimit() {
    return limit;
  }

  /**
   *
   */
  public void setLimit(int limit) {
    this.limit = MathUtils.clamp(limit, -1, MAX_LIMIT);
  }

  /**
   *
   */
  public List<PageSortArg> getSorts() {
    return sorts;
  }

  /**
   *
   */
  public void setSorts(List<PageSortArg> sorts) {
    this.sorts = sorts;
  }
}
