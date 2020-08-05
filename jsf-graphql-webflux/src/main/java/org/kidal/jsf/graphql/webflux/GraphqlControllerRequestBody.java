package org.kidal.jsf.graphql.webflux;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Created at 2020-08-05 22:49:39
 *
 * @author kidal
 * @since 0.1.0
 */
public class GraphqlControllerRequestBody {
  /**
   * 操作名
   */
  @Nullable
  private String operationName = null;

  /**
   * 语句
   */
  @Nullable
  private String query = null;

  /**
   * 参数
   */
  @Nullable
  private Map<String, Object> variables = null;

  /**
   *
   */
  public GraphqlControllerRequestBody() {

  }

  /**
   *
   */
  public GraphqlControllerRequestBody(@Nullable String operationName,
                                      @Nullable String query,
                                      @Nullable Map<String, Object> variables) {
    this.operationName = operationName;
    this.query = query;
    this.variables = variables;
  }

  /**
   *
   */
  @Nullable
  public String getOperationName() {
    return operationName;
  }

  /**
   *
   */
  public void setOperationName(@Nullable String operationName) {
    this.operationName = operationName;
  }

  /**
   *
   */
  @Nullable
  public String getQuery() {
    return query;
  }

  /**
   *
   */
  public void setQuery(@Nullable String query) {
    this.query = query;
  }

  /**
   *
   */
  @Nullable
  public Map<String, Object> getVariables() {
    return variables;
  }

  /**
   *
   */
  public void setVariables(@Nullable Map<String, Object> variables) {
    this.variables = variables;
  }
}
