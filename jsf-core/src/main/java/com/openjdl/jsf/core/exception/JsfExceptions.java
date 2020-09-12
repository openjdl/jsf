package com.openjdl.jsf.core.exception;


/**
 * Created at 2020-08-04 18:24:58
 *
 * @author kidal
 * @since 0.1.0
 */
public enum JsfExceptions implements JsfExceptionDataContract {
  /**
   * 成功
   */
  OK(
    JsfExceptionDataUtils.ofId(0, 0, 0),
    JsfExceptionDataUtils.ofCode("jsf", "core", "OK"),
    "成功"
  ),

  /**
   * 失败
   */
  FAIL(
    JsfExceptionDataUtils.ofId(0, 0, 1),
    JsfExceptionDataUtils.ofCode("jsf", "core", "Fail"),
    "失败"
  ),

  /**
   * 错误的请求
   */
  BAD_REQUEST(
    JsfExceptionDataUtils.ofId(0, 0, 2),
    JsfExceptionDataUtils.ofCode("jsf", "core", "BadRequest"),
    "错误的请求"
  ),

  /**
   * 无效的用户身份识别码
   */
  INCORRECT_UIN(
    JsfExceptionDataUtils.ofId(0, 0, 3),
    JsfExceptionDataUtils.ofCode("jsf", "core", "IncorrectUin"),
    "无效的用户身份识别码"
  ),

  /**
   * 无效的暗号
   */
  INCORRECT_CIPHER(
    JsfExceptionDataUtils.ofId(0, 0, 4),
    JsfExceptionDataUtils.ofCode("jsf", "core", "IncorrectCipher"),
    "无效的暗号"
  ),

  /**
   * 服务器内部错误
   */
  SERVER_INTERNAL_ERROR(
    JsfExceptionDataUtils.ofId(0, 0, 5),
    JsfExceptionDataUtils.ofCode("jsf", "core", "ServerInternalError"),
    "服务器内部错误"
  ),

  /**
   * 错误的参数
   */
  BAD_PARAMETER(
    JsfExceptionDataUtils.ofId(0, 0, 6),
    JsfExceptionDataUtils.ofCode("jsf", "core", "BadParameter"),
    "错误的请求"
  ),

  //
  ;


  /**
   *
   */
  private long id;

  /**
   *
   */
  private String code;

  /**
   *
   */
  private String format;

  /**
   *
   */
  JsfExceptions(long id, String code, String format) {
    this.id = id;
    this.code = code;
    this.format = format;
  }

  /**
   *
   */
  @Override
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
  @Override
  public String getCode() {
    return code;
  }

  /**
   *
   */
  public void setCode(String code) {
    this.code = code;
  }

  /**
   *
   */
  @Override
  public String getFormat() {
    return format;
  }

  /**
   *
   */
  public void setFormat(String format) {
    this.format = format;
  }
}
