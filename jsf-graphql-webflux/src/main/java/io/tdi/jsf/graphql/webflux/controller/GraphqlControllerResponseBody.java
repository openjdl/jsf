package io.tdi.jsf.graphql.webflux.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Created at 2020-08-05 22:39:47
 *
 * @author kidal
 * @since 0.1.0
 */
public class GraphqlControllerResponseBody {
  /**
   * 本次查询消耗时间（毫秒）
   */
  private final long took;

  /**
   * 警告
   */
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private final List<String> warnings;

  /**
   * 错误
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Nullable
  private final GraphqlControllerResponseBodyError error;

  /**
   * 数据
   */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Nullable
  private final Object data;

  /**
   * 错误
   */
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  @NotNull
  private final List<GraphqlControllerResponseBodyRelayError> errors;

  /**
   * 扩展
   */
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  @NotNull
  private final Map<Object, Object> extensions;

  /**
   *
   */
  public GraphqlControllerResponseBody(long took,
                                       List<String> warnings,
                                       @Nullable GraphqlControllerResponseBodyError error,
                                       @Nullable Object data,
                                       @NotNull List<GraphqlControllerResponseBodyRelayError> errors,
                                       @NotNull Map<Object, Object> extensions) {
    this.took = took;
    this.warnings = warnings;
    this.error = error;
    this.data = data;
    this.errors = errors;
    this.extensions = extensions;
  }

  /**
   *
   */
  public long getTook() {
    return took;
  }

  /**
   *
   */
  public List<String> getWarnings() {
    return warnings;
  }

  /**
   *
   */
  @Nullable
  public GraphqlControllerResponseBodyError getError() {
    return error;
  }

  /**
   *
   */
  @Nullable
  public Object getData() {
    return data;
  }

  /**
   *
   */
  @NotNull
  public List<GraphqlControllerResponseBodyRelayError> getErrors() {
    return errors;
  }

  /**
   *
   */
  @NotNull
  public Map<Object, Object> getExtensions() {
    return extensions;
  }
}
