package org.kidal.jsf.graphql.query;

import graphql.schema.DataFetchingEnvironment;
import org.jetbrains.annotations.NotNull;
import org.kidal.jsf.core.pagination.PageArgs;
import org.kidal.jsf.core.pagination.PageSortArg;
import org.kidal.jsf.core.utils.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created at 2020-08-05 17:42:46
 *
 * @author kidal
 * @since 0.1.0
 */
public class GraphqlFetchingEnvironment {
  /**
   *
   */
  @NotNull
  private final DataFetchingEnvironment environment;

  /**
   *
   */
  @NotNull
  private final GraphqlFetchingContext context;

  /**
   *
   */
  public GraphqlFetchingEnvironment(@NotNull DataFetchingEnvironment environment, @NotNull GraphqlFetchingContext context) {
    this.environment = environment;
    this.context = context;
  }

  /**
   * 获取上层的对象
   */
  @SuppressWarnings("unchecked")
  public <T> T getSourceObject() {
    Object environmentSource = environment.getSource();
    if (!(environmentSource instanceof Map)) {
      return null;
    }
    return (T) ((Map<String, Object>) environmentSource).getOrDefault("__root", null);
  }

  /**
   * 获取分页参数
   *
   * @return 分页参数
   */
  @NotNull
  public PageArgs getPageArgs() {
    // 页码
    int page = environment.getArgumentOrDefault("page", PageArgs.DEFAULT_PAGE);

    // 每页个数
    int limit = environment.getArgumentOrDefault("limit", PageArgs.DEFAULT_LIMIT);

    // 排序
    List<String> sorts = environment.getArgumentOrDefault("sorts", Collections.emptyList());
    PageSortArg[] pageSortArgs = sorts.stream()
      .map(it -> it.split(" "))
      .filter(it -> it.length == 2 && StringUtils.isNoneBlank(it[0], it[1]))
      .map(pair -> new PageSortArg(pair[0], "desc".equals(pair[1].toLowerCase())))
      .toArray(PageSortArg[]::new);

    // done
    return PageArgs.of(page, limit, pageSortArgs);
  }

  /**
   *
   */
  @NotNull
  public DataFetchingEnvironment getEnvironment() {
    return environment;
  }

  /**
   *
   */
  @NotNull
  public GraphqlFetchingContext getContext() {
    return context;
  }
}
