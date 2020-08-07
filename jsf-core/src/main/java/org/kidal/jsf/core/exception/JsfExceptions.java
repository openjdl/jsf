package org.kidal.jsf.core.exception;


import static org.kidal.jsf.core.exception.JsfExceptionDataUtils.ofCode;
import static org.kidal.jsf.core.exception.JsfExceptionDataUtils.ofId;

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
    ofId(0, 0, 0),
    ofCode("jsf", "core", "OK"),
    "成功"
  ),

  /**
   * 失败
   */
  FAIL(
    ofId(0, 0, 1),
    ofCode("jsf", "core", "Fail"),
    "失败"
  ),

  /**
   * 失败
   */
  BAD_REQUEST(
    ofId(0, 0, 2),
    ofCode("jsf", "core", "BadRequest"),
    "错误的请求"
  ),

  /**
   * 无效的通信证
   */
  INCORRECT_PASSPORT(
    ofId(0, 0, 3),
    ofCode("jsf", "core", "IncorrectPassport"),
    "无效的通信证"
  ),

  /**
   * 无效的暗号
   */
  INCORRECT_CIPHER(
    ofId(0, 0, 4),
    ofCode("jsf", "core", "IncorrectCipher"),
    "无效的暗号"
  ),

  /**
   * 服务器内部错误
   */
  SERVER_INTERNAL_ERROR(
    ofId(0, 0, 5),
    ofCode("jsf", "core", "ServerInternalError"),
    "服务器内部错误"
  )

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
