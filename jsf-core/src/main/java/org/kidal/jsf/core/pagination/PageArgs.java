package org.kidal.jsf.core.pagination;

import org.jetbrains.annotations.NotNull;
import org.kidal.jsf.core.utils.MathUtils;
import org.kidal.jsf.core.utils.callback.Action1;


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
  private PageSortArg[] sorts;

  /**
   * 空分页
   *
   * @return 分页参数
   */
  @NotNull
  public static PageArgs ofZero() {
    return new PageArgs(1, 0, new PageSortArg[0]);
  }

  /**
   * 一个元素
   *
   * @return 分页参数
   */
  @NotNull
  public static PageArgs ofOne() {
    return new PageArgs(1, 1, new PageSortArg[0]);
  }

  /**
   * 默认页个数分页
   *
   * @return 分页参数
   */
  @NotNull
  public static PageArgs of(int page) {
    return new PageArgs(page, DEFAULT_LIMIT, new PageSortArg[0]);
  }

  /**
   * 分页
   *
   * @return 分页参数
   */
  @NotNull
  public static PageArgs of(int page, int limit) {
    return new PageArgs(page, limit, new PageSortArg[0]);
  }

  /**
   * 分页
   *
   * @return 分页参数
   */
  @NotNull
  public static PageArgs of(int page, int limit, @NotNull PageSortArg[] sorts) {
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
  public PageArgs(int page, int limit, @NotNull PageSortArg[] sorts) {
    setPage(page);
    setLimit(limit);
  }

  /**
   * 遍历数据
   */
  public <T> void forEach(@NotNull Action1<PageResults<T>, PageArgs> loader,
                          @NotNull Action1<Void, PageResults<T>> consumer) {
    PageArgs pageArgs = new PageArgs(getPage(), getLimit(), getSorts());
    PageResults<T> results;
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
  public PageSortArg[] getSorts() {
    return sorts;
  }

  /**
   *
   */
  public void setSorts(PageSortArg[] sorts) {
    this.sorts = sorts;
  }
}
