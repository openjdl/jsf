package com.openjdl.jsf.core.utils;

/**
 * Created at 2020-08-04 17:30:00
 *
 * @author kidal
 * @since 0.1.0
 */
public interface Jsonable {
  /**
   * 将对象转换为JSON字符串
   *
   * @return 该对象的JSON字符串
   */
  default String toJson() {
    return JsonUtils.toString(this);
  }

  /**
   * 将对象转换为JSON字符串（格式化）
   *
   * @return 该对象的JSON字符串（格式化）
   */
  default String toPrettyJson() {
    return JsonUtils.toPrettyString(this);
  }
}
