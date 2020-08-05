package org.kidal.jsf.graphql.webflux;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created at 2020-08-05 22:41:01
 *
 * @author kidal
 * @since 0.1.0
 */
public class GraphqlControllerResponseBodyErrorItem {
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
   * 报错GraphQL查询路径
   */
  @NotNull
  private final List<String> path;

  /**
   *
   */
  public GraphqlControllerResponseBodyErrorItem(long id, @NotNull String code, @NotNull String message, @NotNull List<String> path) {
    this.id = id;
    this.code = code;
    this.message = message;
    this.path = path;
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
  @NotNull
  public List<String> getPath() {
    return path;
  }
}
