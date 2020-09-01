package io.tdi.jsf.graphql.query;

import graphql.ExecutionResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Created at 2020-08-05 21:20:24
 *
 * @author kidal
 * @since 0.1.0
 */
public class GraphqlQueryResults {
  /**
   * GraphQL执行结果
   */
  @NotNull
  private final ExecutionResult executionResult;

  /**
   * 查询了否决字段的报错
   */
  @NotNull
  private final Map<GraphqlFetchingWarningType, List<GraphqlFetchingWarning>> warnings;

  /**
   * ¬
   * 饼干
   */
  @NotNull
  private final List<GraphqlCookie> cookies;

  /**
   *
   */
  public GraphqlQueryResults(@NotNull ExecutionResult executionResult,
                             @NotNull Map<GraphqlFetchingWarningType, List<GraphqlFetchingWarning>> warnings,
                             @NotNull List<GraphqlCookie> cookies) {
    this.executionResult = executionResult;
    this.warnings = warnings;
    this.cookies = cookies;
  }

  /**
   *
   */
  @NotNull
  public ExecutionResult getExecutionResult() {
    return executionResult;
  }

  /**
   *
   */
  @NotNull
  public Map<GraphqlFetchingWarningType, List<GraphqlFetchingWarning>> getWarnings() {
    return warnings;
  }

  /**
   *
   */
  @NotNull
  public List<GraphqlCookie> getCookies() {
    return cookies;
  }
}
