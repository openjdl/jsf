package org.kidal.jsf.jdbc.mybatis.utils;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.kidal.jsf.core.pagination.PageArgs;
import org.kidal.jsf.core.pagination.PageSortArg;

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
   * 标准化
   */
  public <E> List<E> normalize(List<E> list) {
    if (list == null) {
      return null;
    } else if (list.isEmpty()) {
      return null;
    } else {
      return list;
    }
  }

  /**
   * 分页
   */
  public void withPage(@NotNull PageArgs pageArgs, boolean withOrder) {
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
  public void withOrder(PageSortArg[] sorts) {
    for (PageSortArg sort : sorts) {
      addOrder(sort);
    }
  }

  /**
   *
   */
  public void addOrder(PageSortArg sort) {
    addOrder(Collections.singletonList(sort.getName()), sort.isDescending());
  }

  /**
   *
   */
  public void addOrder(List<String> fields, boolean descending) {
    // filter
    final List<String> filtered = fields.stream().filter(this::canOrder).collect(Collectors.toList());
    if (filtered.isEmpty()) {
      return;
    }

    // rename
    final List<String> renamed = filtered.stream().map(this::renameOrderField).collect(Collectors.toList());

    // add
    this.useOrder = true;
    if (this.orders == null) {
      this.orders = Lists.newArrayList();
    }
    this.orders.add(new Order(renamed, descending ? "DESC" : "ASC"));

    // distinct
    this.orders = this.orders
      .stream()
      .distinct()
      .collect(Collectors.toList());
  }

  /**
   *
   */
  public boolean canOrder(@NotNull String field) {
    return false;
  }

  /**
   *
   */
  public String renameOrderField(@NotNull String field) {
    return field;
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
