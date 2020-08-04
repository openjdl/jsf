package org.kidal.jsf.core.exception;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created at 2020-08-04 18:27:07
 *
 * @author kidal
 * @since 0.1.0
 */
public class JsfExceptionDataUtils {
  /**
   * 创建错误ID
   *
   * @param microserviceId 微服务实例ID
   * @param moduleId       模块ID
   * @param serialId       序号ID
   * @return 错误代号
   */
  public static long ofId(int microserviceId, int moduleId, int serialId) {
    return (long) microserviceId * 1000000 + moduleId * 1000 + serialId;
  }

  /**
   * 创建错误代号
   *
   * @param microserviceName 微服务实例ID
   * @param moduleName       模块ID
   * @param serialName       序号
   * @return 错误代号
   */
  @NotNull
  public static String ofCode(@NotNull String microserviceName, @NotNull String moduleName, String serialName) {
    return String.format("%s:%s:%s", microserviceName, moduleName, serialName);
  }

  /**
   * 创建ID字典
   */
  @NotNull
  public static <T extends JsfExceptionDataContract> Map<Long, T> createIdMap(@NotNull T[] values) {
    final Map<Long, T> map = new HashMap<>(values.length);
    Arrays.stream(values)
      .forEach(e -> {
        if (map.containsKey(e.getId())) {
          throw new IllegalStateException("Duplicate error code " + e.getCode());
        }
        map.put(e.getId(), e);
      });
    return map;
  }

  /**
   * 创建错误代号字典
   */
  @NotNull
  public static <T extends JsfExceptionDataContract> Map<String, T> createCodeMap(@NotNull T[] values) {
    final Map<String, T> map = new HashMap<>(values.length);
    Arrays.stream(values)
      .forEach(e -> {
        if (map.containsKey(e.getCode())) {
          throw new IllegalStateException("Duplicate error code " + e.getCode());
        }
        map.put(e.getCode(), e);
      });
    return map;
  }
}
