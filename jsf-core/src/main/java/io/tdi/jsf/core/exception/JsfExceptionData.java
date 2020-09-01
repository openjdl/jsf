package io.tdi.jsf.core.exception;

/**
 * Created at 2020-08-04 18:18:58
 *
 * @author kidal
 * @since 0.1.0
 */
public class JsfExceptionData implements JsfExceptionDataContract {
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
  public JsfExceptionData() {

  }

  /**
   *
   */
  public JsfExceptionData(long id, String code, String format) {
    this.id = id;
    this.code = code;
    this.format = format;
  }

  /**
   *
   */
  @Override
  public String toString() {
    return "ExceptionData{" +
      "id=" + id +
      ", code='" + code + '\'' +
      ", format='" + format + '\'' +
      '}';
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
