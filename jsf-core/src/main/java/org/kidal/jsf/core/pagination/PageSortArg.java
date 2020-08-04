package org.kidal.jsf.core.pagination;


/**
 * Created at 2020-08-04 17:30:00
 *
 * @author kidal
 * @since 0.1.0
 */
public class PageSortArg {
  /**
   * 参数名
   */
  private final String name;

  /**
   * 降序
   */
  private final boolean descending;

  /**
   *
   */
  public PageSortArg(String name, boolean descending) {
    this.name = name;
    this.descending = descending;
  }

  /**
   *
   */
  public String getName() {
    return name;
  }

  /**
   *
   */
  public boolean isDescending() {
    return descending;
  }
}
