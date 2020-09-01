package io.tdi.jsf.graphql.query;

import org.jetbrains.annotations.NotNull;

/**
 * Created at 2020-08-05 17:21:26
 *
 * @author kidal
 * @since 0.1.0
 */
public class GraphqlFetchingWarning {
  /**
   *
   */
  @NotNull
  private final String typeName;

  /**
   *
   */
  @NotNull
  private final String fieldName;

  /**
   *
   */
  @NotNull
  private final String message;

  /**
   *
   */
  public GraphqlFetchingWarning(@NotNull String typeName, @NotNull String fieldName, @NotNull String message) {
    this.typeName = typeName;
    this.fieldName = fieldName;
    this.message = message;
  }

  /**
   *
   */
  @NotNull
  public String getTypeName() {
    return typeName;
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
  public String getMessage() {
    return message;
  }
}
