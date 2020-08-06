package org.kidal.jsf.graphql.webflux.controller;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created at 2020-08-05 22:42:24
 *
 * @author kidal
 * @since 0.1.0
 */
public class GraphqlControllerResponseBodyError {
  /**
   * 错误ID
   */
  private final long id;

  /**
   * 错误编号
   */
  @NotNull
  private final String code;

  /**
   * 错误信息
   */
  @NotNull
  private final String message;

  /**
   *
   */
  @Nullable
  private final List<GraphqlControllerResponseBodyErrorItem> errors;

  /**
   *
   */
  public GraphqlControllerResponseBodyError(long id,
                                            @NotNull String code,
                                            @NotNull String message,
                                            @Nullable List<GraphqlControllerResponseBodyErrorItem> errors) {
    this.id = id;
    this.code = code;
    this.message = message;
    this.errors = errors;
  }

  /**
   *
   */
  public long getId() {
    return id;
  }

  /**
   *
   */
  @NotNull
  public String getCode() {
    return code;
  }

  /**
   *
   */
  @NotNull
  public String getMessage() {
    return message;
  }

  /**
   *
   */
  @Nullable
  public List<GraphqlControllerResponseBodyErrorItem> getErrors() {
    return errors;
  }
}
