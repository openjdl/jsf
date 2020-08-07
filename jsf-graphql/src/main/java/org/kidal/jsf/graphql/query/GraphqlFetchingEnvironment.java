package org.kidal.jsf.graphql.query;

import graphql.schema.DataFetchingEnvironment;
import org.jetbrains.annotations.NotNull;
import org.kidal.jsf.core.exception.JsfException;
import org.kidal.jsf.core.exception.JsfExceptions;
import org.kidal.jsf.core.pagination.PageArgs;
import org.kidal.jsf.core.pagination.PageSortArg;
import org.kidal.jsf.core.utils.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
  public Optional<Boolean> getBoolean(String name) {
    return getAny(name, Boolean.class);
  }

  /**
   *
   */
  public Optional<Integer> getInteger(String name) {
    return getAny(name, Integer.class);
  }

  /**
   *
   */
  public Optional<Long> getLong(String name) {
    return getAny(name, Long.class);
  }

  /**
   *
   */
  public Optional<String> getString(String name) {
    return getAny(name, String.class);
  }

  /**
   *
   */
  public <T> Optional<T> getAny(String name, Class<T> type) {
    Object argument = environment.getArgument(name);
    if (argument == null) {
      return Optional.empty();
    }
    if (context.getConversionService().canConvert(argument.getClass(), type)) {
      return Optional.ofNullable(context.getConversionService().convert(argument, type));
    } else {
      return Optional.empty();
    }
  }

  /**
   *
   */
  @SuppressWarnings("unchecked")
  public <T> Optional<List<T>> getList(String name, Class<T> contentClass) {
    Object argument = environment.getArgument(name);
    if (argument == null) {
      return Optional.empty();
    }
    if (!(argument instanceof List)) {
      return Optional.empty();
    }
    List<Object> list = (List<Object>) argument;
    Object firstElement = list.get(0);

    if (context.getConversionService().canConvert(firstElement.getClass(), contentClass)) {
      List<T> converted = list
        .stream()
        .map(it -> context.getConversionService().convert(it, contentClass))
        .collect(Collectors.toList());
      return Optional.of(converted);
    } else {
      return Optional.empty();
    }
  }

  /**
   *
   */
  public boolean requireBoolean(String name) {
    return getBoolean(name).orElseThrow(() -> new JsfException(JsfExceptions.BAD_REQUEST));
  }

  /**
   *
   */
  public int requireInteger(String name) {
    return getInteger(name).orElseThrow(() -> new JsfException(JsfExceptions.BAD_REQUEST));
  }

  /**
   *
   */
  public long requireLong(String name) {
    return getLong(name).orElseThrow(() -> new JsfException(JsfExceptions.BAD_REQUEST));
  }

  /**
   *
   */
  @NotNull
  public String requireString(String name) {
    return getString(name).orElseThrow(() -> new JsfException(JsfExceptions.BAD_REQUEST));
  }

  /**
   *
   */
  @NotNull
  public <T> T requireAny(String name, Class<T> type) {
    return getAny(name, type).orElseThrow(() -> new JsfException(JsfExceptions.BAD_REQUEST));
  }

  /**
   *
   */
  @NotNull
  public <T> List<T> requireList(String name, Class<T> contentClass) {
    return getList(name, contentClass).orElseThrow(() -> new JsfException(JsfExceptions.BAD_REQUEST));
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
