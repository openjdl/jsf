package io.tdi.jsf.graphql.webflux.controller;

import com.google.common.collect.Lists;
import graphql.ErrorType;
import graphql.language.SourceLocation;

import java.util.List;

/**
 * Created at 2020-08-05 22:44:56
 *
 * @author kidal
 * @since 0.1.0
 */
public class GraphqlControllerResponseBodyRelayError {
  /**
   *
   */
  private long id = 0;

  /**
   *
   */
  private String code = "";

  /**
   *
   */
  private String message = "";

  /**
   *
   */
  private List<SourceLocation> locations = Lists.newArrayList();

  /**
   *
   */
  private ErrorType errorType = ErrorType.ExecutionAborted;

  /**
   *
   */
  private List<Object> path = Lists.newArrayList();

  /**
   *
   */
  public GraphqlControllerResponseBodyRelayError() {
  }

  /**
   *
   */
  public GraphqlControllerResponseBodyRelayError(long id, String code, String message, List<SourceLocation> locations, ErrorType errorType, List<Object> path) {
    this.id = id;
    this.code = code;
    this.message = message;
    this.locations = locations;
    this.errorType = errorType;
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
  public void setId(long id) {
    this.id = id;
  }

  /**
   *
   */
  public String getMessage() {
    return message;
  }

  /**
   *
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   *
   */
  public List<SourceLocation> getLocations() {
    return locations;
  }

  /**
   *
   */
  public void setLocations(List<SourceLocation> locations) {
    this.locations = locations;
  }

  /**
   *
   */
  public ErrorType getErrorType() {
    return errorType;
  }

  /**
   *
   */
  public void setErrorType(ErrorType errorType) {
    this.errorType = errorType;
  }

  /**
   *
   */
  public List<Object> getPath() {
    return path;
  }

  /**
   *
   */
  public void setPath(List<Object> path) {
    this.path = path;
  }

  /**
   *
   */
  public String getCode() {
    return code;
  }

  /**
   *
   */
  public void setCode(String code) {
    this.code = code;
  }
}
