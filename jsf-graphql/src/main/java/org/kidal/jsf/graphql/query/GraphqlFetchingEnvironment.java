package org.kidal.jsf.graphql.query;

import graphql.schema.DataFetchingEnvironment;
import org.jetbrains.annotations.NotNull;
import org.kidal.jsf.core.pagination.PageArgs;
import org.kidal.jsf.core.pagination.PageSortArg;
import org.kidal.jsf.core.utils.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
  public GraphqlFetchingEnvironment(@NotNull DataFetchingEnvironment environment,
                                    @NotNull GraphqlFetchingContext context) {
    this.environment = environment;
    this.context = context;
  }

  /**
   * 获取上层的对象
   */
  @SuppressWarnings("unchecked")
  public <T> T getSourceObject() {
    Object sourceObject = environment.getSource();
    if (sourceObject instanceof Map) {
      return (T) ((Map<String, Object>) sourceObject).getOrDefault(".source", null);
    }
    return (T) sourceObject;
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
  public Optional<Boolean> getAsBoolean(String name) {
    Object argument = environment.getArgument(name);
    if (argument == null) {
      return Optional.empty();
    }
    if (context.getConversionService().canConvert(argument.getClass(), Boolean.class)) {
      return Optional.ofNullable(context.getConversionService().convert(argument, Boolean.class));
    } else {
      return Optional.empty();
    }
  }

  /**
   *
   */
  public Optional<Integer> getAsInteger(String name) {
    Object argument = environment.getArgument(name);
    if (argument == null) {
      return Optional.empty();
    }
    if (context.getConversionService().canConvert(argument.getClass(), Integer.class)) {
      return Optional.ofNullable(context.getConversionService().convert(argument, Integer.class));
    } else {
      return Optional.empty();
    }
  }

  /**
   *
   */
  public Optional<Long> getAsLong(String name) {
    Object argument = environment.getArgument(name);
    if (argument == null) {
      return Optional.empty();
    }
    if (context.getConversionService().canConvert(argument.getClass(), Long.class)) {
      return Optional.ofNullable(context.getConversionService().convert(argument, Long.class));
    } else {
      return Optional.empty();
    }
  }

  /**
   *
   */
  public Optional<String> getAsString(String name) {
    Object argument = environment.getArgument(name);
    if (argument == null) {
      return Optional.empty();
    }
    if (context.getConversionService().canConvert(argument.getClass(), String.class)) {
      return Optional.ofNullable(context.getConversionService().convert(argument, String.class));
    } else {
      return Optional.empty();
    }
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
