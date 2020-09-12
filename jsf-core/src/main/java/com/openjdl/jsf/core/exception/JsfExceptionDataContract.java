package com.openjdl.jsf.core.exception;

/**
 * Created at 2020-08-04 18:11:17
 *
 * @author kidal
 * @since 0.1.0
 */
public interface JsfExceptionDataContract {
  /**
   * 获取错误ID
   *
   * @return 错误ID
   */
  long getId();

  /**
   * 获取错误代码
   *
   * @return 错误代码
   */
  String getCode();

  /**
   * 获取格式化格式
   *
   * @return 格式化格式
   */
  String getFormat();

  /**
   * 获取错误相信描述
   *
   * @since 0.3
   */
  default String getDescription() {
    return null;
  }
}
