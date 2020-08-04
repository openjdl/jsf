package org.kidal.jsf.core.exception;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created at 2020-08-04 22:47:49
 *
 * @author kidal
 * @since 0.1.0
 */
public interface JsfExceptionDataResolver {
  /**
   * 解析错误数据
   *
   * @param id   错误ID
   * @param code 错误编号
   * @return 错误数据
   */
  @Nullable
  JsfExceptionDataContract resolveJsfExceptionData(long id, @NotNull String code);
}
