package org.kidal.jsf.core.utils;

/**
 * @author kidal
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
