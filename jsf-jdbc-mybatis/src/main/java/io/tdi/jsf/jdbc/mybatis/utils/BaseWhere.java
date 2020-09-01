package io.tdi.jsf.jdbc.mybatis.utils;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import io.tdi.jsf.core.pagination.Page;
import io.tdi.jsf.core.pagination.PageArgs;
import io.tdi.jsf.core.pagination.PageSortArg;
import io.tdi.jsf.core.utils.callback.Action0;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created at 2020-08-06 22:36:33
 *
 * @author kidal
 * @since 0.1.0
 */
public class BaseWhere {
  /**
   *
   */
  private PageArgs pageArgs;

  /**
   *
   */
  private boolean useLimit = false;

  /**
   *
   */
  private int start = 0;

  /**
   *
   */
  private int limit = 0;

  /**
   *
   */
  private boolean clearOrderOnNextSet = false;

  /**
   *
   */
  private boolean useOrder = false;

  /**
   *
   */
  private List<Order> orders = null;

  /**
   *
   */
  private boolean lockForShare = false;

  /**
   *
   */
  private boolean lockForUpdate = false;

  //--------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------

  /**
   *
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BaseWhere baseWhere = (BaseWhere) o;
    return useLimit == baseWhere.useLimit &&
      start == baseWhere.start &&
      limit == baseWhere.limit &&
      clearOrderOnNextSet == baseWhere.clearOrderOnNextSet &&
      useOrder == baseWhere.useOrder &&
      lockForShare == baseWhere.lockForShare &&
      lockForUpdate == baseWhere.lockForUpdate &&
      Objects.equal(pageArgs, baseWhere.pageArgs) &&
      Objects.equal(orders, baseWhere.orders);
  }

  /**
   *
   */
  @Override
  public int hashCode() {
    return Objects.hashCode(pageArgs, useLimit, start, limit, clearOrderOnNextSet, useOrder, orders, lockForShare, lockForUpdate);
  }

  /**
   *
   */
  @Override
  public String toString() {
    return "BaseWhere{" +
      "pageArgs=" + pageArgs +
      ", useLimit=" + useLimit +
      ", start=" + start +
      ", limit=" + limit +
      ", clearOrderOnNextSet=" + clearOrderOnNextSet +
      ", useOrder=" + useOrder +
      ", orders=" + orders +
      ", lockForShare=" + lockForShare +
      ", lockForUpdate=" + lockForUpdate +
      '}';
  }

  /**
   * 标准化
   */
  public void normalize() {

  }

  /**
   * 标准化
   */
  public <E> List<E> normalize(List<E> list) {
    if (list == null) {
      return null;
    } else if (list.isEmpty()) {
      return null;
    } else {
      return list.stream().sorted().collect(Collectors.toList());
    }
  }

  /**
   * 分页
   */
  public void withPage(@NotNull PageArgs pageArgs, boolean withOrder) {
    this.pageArgs = pageArgs;

    useLimit = true;
    start = (pageArgs.getPage() - 1) * pageArgs.getLimit();
    limit = pageArgs.getLimit();

    if (withOrder) {
      withOrder(pageArgs.getSorts());
    }
  }

  /**
   *
   */
  public void withPageZero() {
    withPage(PageArgs.ofZero(), false);
  }

  /**
   *
   */
  public void withPageOne() {
    withPage(PageArgs.ofOne(), false);
  }

  /**
   *
   */
  public void withOrder(@NotNull List<PageSortArg> sorts) {
    for (PageSortArg sort : sorts) {
      addOrder(sort);
    }
  }

  /**
   *
   */
  public void addOrder(@NotNull PageSortArg sort) {
    addOrder(Collections.singletonList(sort.getName()), sort.isDescending());
  }

  /**
   *
   */
  public void addOrder(@NotNull String field, boolean descending) {
    addOrder(Collections.singletonList(field), descending);
  }

  /**
   *
   */
  public void addOrder(@NotNull List<String> fields, boolean descending) {
    // filter
    final List<String> filtered = fields.stream().filter(this::canSort).collect(Collectors.toList());
    if (filtered.isEmpty()) {
      return;
    }

    // rename
    final List<String> renamed = filtered.stream().map(this::renameOrderField).collect(Collectors.toList());

    // add
    this.useOrder = true;

    if (orders == null) {
      orders = Lists.newArrayList();
    }

    if (clearOrderOnNextSet) {
      clearOrderOnNextSet = false;
      orders.clear();
    }

    orders.add(new Order(renamed, descending ? "DESC" : "ASC"));

    // distinct
    orders = orders
      .stream()
      .distinct()
      .collect(Collectors.toList());
  }

  /**
   *
   */
  public void setDefaultOrder(@NotNull String field, boolean descending) {
    setDefaultOrder(Collections.singletonList(field), descending);
  }

  /**
   *
   */
  public void setDefaultOrder(@NotNull List<String> fields, boolean descending) {
    useOrder = true;
    orders = Lists.newArrayList(new Order(fields, descending ? "DESC" : "ASC"));
    clearOrderOnNextSet = true;
  }

  /**
   *
   */
  public boolean canSort(@NotNull String field) {
    return false;
  }

  /**
   *
   */
  public String renameOrderField(@NotNull String field) {
    return field;
  }

  /**
   * 分页
   */
  public <T> Page<T> page(@NotNull Action0<Integer> counter,
                          @NotNull Action0<List<T>> selector) {
    normalize();
    return Page.of(pageArgs, counter, selector);
  }

  //--------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------

  public static class Order {
    private List<String> fields;
    private String order;

    public Order(List<String> fields, String order) {
      this.fields = fields;
      this.order = order;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Order order1 = (Order) o;
      return Objects.equal(fields, order1.fields) &&
        Objects.equal(order, order1.order);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(fields, order);
    }

    @Override
    public String toString() {
      return "Order{" +
        "fields=" + fields +
        ", order='" + order + '\'' +
        '}';
    }

    public List<String> getFields() {
      return fields;
    }

    public void setFields(List<String> fields) {
      this.fields = fields;
    }

    public String getOrder() {
      return order;
    }

    public void setOrder(String order) {
      this.order = order;
    }
  }

  //--------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------

  public boolean isPaging() {
    return useLimit && limit > 0;
  }

  public boolean isCounting() {
    return useLimit && limit == 0;
  }

  //--------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------

  public PageArgs getPageArgs() {
    return pageArgs;
  }

  public void setPageArgs(@Nullable PageArgs pageArgs) {
    this.pageArgs = pageArgs;
  }

  public boolean isUseLimit() {
    return useLimit;
  }

  public void setUseLimit(boolean useLimit) {
    this.useLimit = useLimit;
  }

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public boolean isUseOrder() {
    return useOrder;
  }

  public void setUseOrder(boolean useOrder) {
    this.useOrder = useOrder;
  }

  public List<Order> getOrders() {
    return orders;
  }

  public void setOrders(List<Order> orders) {
    this.orders = orders;
  }

  public boolean isLockForShare() {
    return lockForShare;
  }

  public void setLockForShare(boolean lockForShare) {
    this.lockForShare = lockForShare;
  }

  public boolean isLockForUpdate() {
    return lockForUpdate;
  }

  public void setLockForUpdate(boolean lockForUpdate) {
    this.lockForUpdate = lockForUpdate;
  }
}
