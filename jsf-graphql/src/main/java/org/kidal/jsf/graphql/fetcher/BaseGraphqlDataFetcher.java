package org.kidal.jsf.graphql.fetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kidal.jsf.graphql.query.GraphqlFetchingContext;
import org.kidal.jsf.graphql.query.GraphqlFetchingEnvironment;
import org.kidal.jsf.graphql.query.GraphqlFetchingWarningType;

/**
 * Created at 2020-08-05 17:41:07
 *
 * @author kidal
 * @since 0.1.0
 */
public abstract class BaseGraphqlDataFetcher<T> implements DataFetcher<T> {
  /**
   * 获取字段
   *
   * @param env 环境
   * @return 可序列化的结果
   * @throws Exception 一切异常
   */
  @Nullable
  public abstract T fetch(@NotNull GraphqlFetchingEnvironment env) throws Exception;

  /**
   * This is called by the graphql engine to fetch the value.  The {@link graphql.schema.DataFetchingEnvironment} is a composite
   * context object that tells you all you need to know about how to fetch a data value in graphql type terms.
   *
   * @param environment this is the data fetching environment which contains all the context you need to fetch a value
   * @return a value of type T. May be wrapped in a {@link graphql.execution.DataFetcherResult}
   * @throws Exception to relieve the implementations from having to wrap checked exceptions. Any exception thrown
   *                   from a {@code DataFetcher} will eventually be handled by the registered {@link graphql.execution.DataFetcherExceptionHandler}
   *                   and the related field will have a value of {@code null} in the result.
   */
  @Override
  @Nullable
  public T get(@NotNull DataFetchingEnvironment environment) throws Exception {
    // 获取上下文
    GraphqlFetchingContext context = environment.getContext();

    // 添加否决警告
    if (environment.getFieldDefinition().isDeprecated()) {
      GraphQLType parentType = environment.getParentType();
      String typeName = parentType instanceof GraphQLObjectType ? ((GraphQLObjectType) parentType).getName() : "?";
      context.addWarning(GraphqlFetchingWarningType.DEPRECATED,
        typeName,
        environment.getFieldDefinition().getName(),
        environment.getFieldDefinition().getDeprecationReason()
      );
    }

    // 下一级
    return fetch(new GraphqlFetchingEnvironment(environment, context));
  }
}
