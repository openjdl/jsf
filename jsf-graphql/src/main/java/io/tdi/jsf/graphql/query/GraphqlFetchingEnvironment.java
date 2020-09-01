package io.tdi.jsf.graphql.query;

import graphql.schema.DataFetchingEnvironment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import io.tdi.jsf.core.exception.JsfException;
import io.tdi.jsf.core.exception.JsfExceptions;
import io.tdi.jsf.core.pagination.PageArgs;
import io.tdi.jsf.core.pagination.PageSortArg;
import io.tdi.jsf.core.sugar.BeanAccessor;
import io.tdi.jsf.core.sugar.BeanPropertyAccessor;
import io.tdi.jsf.core.sugar.MapBeanPropertyAccessor;
import io.tdi.jsf.core.utils.StringUtils;
import org.springframework.core.convert.ConversionService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created at 2020-08-05 17:42:46
 *
 * @author kidal
 * @since 0.1.0
 */
public class GraphqlFetchingEnvironment implements BeanAccessor {
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
  @NotNull
  private final BeanAccessor parameters;

  /**
   *
   */
  public GraphqlFetchingEnvironment(@NotNull DataFetchingEnvironment environment,
                                    @NotNull GraphqlFetchingContext context) {
    this.environment = environment;
    this.context = context;
    this.parameters = new BeanAccessor() {
      private final BeanPropertyAccessor beanPropertyAccessor = new MapBeanPropertyAccessor(environment.getArguments());

      @NotNull
      @Override
      public BeanPropertyAccessor getPropertyAccessor() {
        return beanPropertyAccessor;
      }

      @Override
      public ConversionService getConversionService() {
        return context.getConversionService();
      }

      @NotNull
      @Override
      public Supplier<RuntimeException> getExceptionSupplier() {
        return () -> new JsfException(JsfExceptions.BAD_REQUEST);
      }
    };
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
    List<PageSortArg> pageSortArgs = sorts.stream()
      .map(it -> it.split(" "))
      .filter(it -> it.length == 2 && StringUtils.isNoneBlank(it[0], it[1]))
      .map(pair -> new PageSortArg(pair[0], "desc".equals(pair[1].toLowerCase())))
      .collect(Collectors.toList());

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

  /**
   *
   */
  @NotNull
  public BeanAccessor getParameters() {
    return parameters;
  }

  /**
   *
   */
  @NotNull
  @Override
  public BeanPropertyAccessor getPropertyAccessor() {
    return parameters.getPropertyAccessor();
  }

  /**
   *
   */
  @Nullable
  @Override
  public ConversionService getConversionService() {
    return parameters.getConversionService();
  }

  /**
   *
   */
  @NotNull
  @Override
  public Supplier<RuntimeException> getExceptionSupplier() {
    return parameters.getExceptionSupplier();
  }
}
