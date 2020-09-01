package io.tdi.jsf.core.pagination;


import com.google.common.base.Objects;

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
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PageSortArg that = (PageSortArg) o;
    return descending == that.descending &&
      Objects.equal(name, that.name);
  }

  /**
   *
   */
  @Override
  public int hashCode() {
    return Objects.hashCode(name, descending);
  }

  /**
   *
   */
  @Override
  public String toString() {
    return "PageSortArg{" +
      "name='" + name + '\'' +
      ", descending=" + descending +
      '}';
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
