package org.kidal.jsf.graphql.fetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;
import org.jetbrains.annotations.NotNull;
import org.kidal.jsf.graphql.query.GraphqlFetchingContext;
import org.kidal.jsf.graphql.query.GraphqlFetchingWarningType;

/**
 * Created at 2020-08-05 16:54:31
 *
 * @author kidal
 * @since 0.1.0
 */
public class DeprecatedFetcher<T> implements DataFetcher<T> {
  /**
   * 原始fetcher
   */
  private final DataFetcher<T> fetcher;

  /**
   * 字段名
   */
  private final String fieldName;

  /**
   * 否决的原因
   */
  private final String reason;

  /**
   *
   */
  public DeprecatedFetcher(@NotNull DataFetcher<T> fetcher, @NotNull String fieldName, @NotNull String reason) {
    this.fetcher = fetcher;
    this.fieldName = fieldName;
    this.reason = reason;
  }

  /**
   *
   */
  @Override
  public T get(@NotNull DataFetchingEnvironment environment) throws Exception {
    GraphqlFetchingContext context = environment.getContext();

    GraphQLType parentType = environment.getParentType();
    String typeName = parentType instanceof GraphQLObjectType ? ((GraphQLObjectType) parentType).getName() : "?";
    context.addWarning(
      GraphqlFetchingWarningType.DEPRECATED,
      typeName,
      environment.getFieldDefinition().getName(),
      reason
    );

    // 使用原始fetcher
    return fetcher.get(environment);
  }

  /**
   *
   */
  @NotNull
  public DataFetcher<T> getFetcher() {
    return fetcher;
  }

  /**
   *
   */
  @NotNull
  public String getFieldName() {
    return fieldName;
  }

  /**
   *
   */
  @NotNull
  public String getReason() {
    return reason;
  }
}
