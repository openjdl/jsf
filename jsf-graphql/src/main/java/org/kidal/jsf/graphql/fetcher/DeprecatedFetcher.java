package org.kidal.jsf.graphql.fetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.jetbrains.annotations.NotNull;
import org.kidal.jsf.graphql.GraphqlFetchingContext;
import org.kidal.jsf.graphql.GraphqlFetchingWarningType;

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
    // TODO: typeName
    GraphqlFetchingContext context = environment.getContext();
    context.addWarning(
      GraphqlFetchingWarningType.DEPRECATED,
      "TODO",
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
