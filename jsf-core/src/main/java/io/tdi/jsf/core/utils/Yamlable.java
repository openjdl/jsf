package io.tdi.jsf.core.utils;

/**
 * Created at 2020-08-04 17:30:00
 *
 * @author kidal
 * @since 0.1.0
 */
public interface Yamlable {
  /**
   * 将对象转换为YAML字符串
   *
   * @return 该对象的YAML字符串
   */
  default String toYaml() {
    return YamlUtils.toString(this);
  }
}
