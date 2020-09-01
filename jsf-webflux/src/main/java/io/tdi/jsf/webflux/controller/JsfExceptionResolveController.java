package io.tdi.jsf.webflux.controller;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import io.tdi.jsf.core.exception.JsfExceptionDataContract;
import io.tdi.jsf.core.exception.JsfExceptionDataResolver;
import io.tdi.jsf.core.exception.JsfExceptions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

/**
 * Created at 2020-08-05 14:34:26
 *
 * @author kidal
 * @since 0.1.0
 */
public class JsfExceptionResolveController extends JsfBaseController {
  /**
   * 异常数据解析器
   */
  @Autowired(required = false)
  protected JsfExceptionDataResolver jsfExceptionDataResolver;

  /**
   * 解析异常数据
   */
  @Nullable
  protected JsfExceptionDataContract resolveJsfExceptionData(long id, @NotNull String code) {
    if (jsfExceptionDataResolver != null) {
      return jsfExceptionDataResolver.resolveJsfExceptionData(id, code);
    } else {
      return Arrays.stream(JsfExceptions.values())
        .filter(it -> it.getId() == id || it.getCode().equals(code))
        .findFirst()
        .orElse(null);
    }
  }

  /**
   * 解析错误消息格式
   */
  @NotNull
  protected String resolveJsfExceptionMessageFormat(long id, @NotNull String code, @NotNull String defaultMessage) {
    JsfExceptionDataContract data = null;
    try {
      data = resolveJsfExceptionData(id, code);
    } catch (Exception e) {
      LOG.warn("Resolve error info failed", e);
    }

    if (data != null) {
      return data.getFormat();
    }

    return defaultMessage;
  }

  /**
   * 解析错误消息格式
   */
  @NotNull
  protected String resolveJsfExceptionMessageFormat(@NotNull JsfExceptionDataContract data) {
    return resolveJsfExceptionMessageFormat(data.getId(), data.getCode(), data.getFormat());
  }
}
