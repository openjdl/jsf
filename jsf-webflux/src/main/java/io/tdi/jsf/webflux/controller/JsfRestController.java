package io.tdi.jsf.webflux.controller;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import io.tdi.jsf.core.exception.JsfException;
import io.tdi.jsf.core.exception.JsfExceptions;
import io.tdi.jsf.webflux.controller.editor.DatePropertyEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.Date;

/**
 * Created at 2020-08-05 14:47:32
 *
 * @author kidal
 * @since 0.1.0
 */
public class JsfRestController extends JsfExceptionResolveController {
  /**
   * 绑定数据
   */
  @InitBinder
  protected void initJsfRestController(WebDataBinder binder) {
    binder.registerCustomEditor(Date.class, new DatePropertyEditor());
  }

  /**
   * 解析JSF异常消息
   */
  protected String resolveJsfExceptionMessage(JsfException e) {
    // debug log
    if (LOG.isDebugEnabled()) {
      if (e.getCause() != null) {
        LOG.error("", e);
      }
    }

    // warn log
    if (LOG.isWarnEnabled()) {
      LOG.warn("(R)JsfException({}, {}, {})", e.getData().getId(), e.getData().getCode(), e.formatMessage(), e);
    }

    // 解析错误格式
    String format = resolveJsfExceptionMessageFormat(e.getData().getId(), e.getData().getCode(), e.formatMessage());

    // 格式化
    String message = e.formatMessage(format);

    // warn log
    if (LOG.isWarnEnabled()) {
      LOG.warn("(P)JsfException({}, {}, {})", e.getData().getId(), e.getData().getCode(), message, e);
    }

    return message;
  }

  /**
   * 构造错误信息
   */
  @NotNull
  protected Object createErrorResponseData(long id, @NotNull String code, @NotNull String message) {
    return Collections.singletonMap("error", ImmutableMap.of("id", id, "code", code, "message", message));
  }

  /**
   * 构造错误信息
   */
  @NotNull
  protected Object createErrorResponseData(Exception e) {
    // log
    if (e != null) {
      LOG.error("{}({})", e.getClass(), e.getMessage(), e);
    }

    //
    final JsfExceptions badRequest = JsfExceptions.BAD_REQUEST;
    final String format = resolveJsfExceptionMessageFormat(badRequest);
    return createErrorResponseData(badRequest.getId(), badRequest.getCode(), format);
  }

  /**
   *
   */
  @ResponseBody
  @ExceptionHandler(Exception.class)
  public Object handleException(Exception e) {
    // log
    LOG.error("Missing exception handler", e);

    //
    final JsfExceptions fail = JsfExceptions.FAIL;
    final String format = resolveJsfExceptionMessageFormat(fail);
    return createErrorResponseData(fail.getId(), fail.getCode(), format);
  }

  /**
   *
   */
  @ResponseBody
  @ExceptionHandler(RuntimeException.class)
  public Object handleRuntimeException(RuntimeException e) {
    // log
    LOG.error("Missing runtime exception handler", e);

    //
    final JsfExceptions fail = JsfExceptions.FAIL;
    final String format = resolveJsfExceptionMessageFormat(fail);
    return createErrorResponseData(fail.getId(), fail.getCode(), format);
  }

  /**
   *
   */
  @ResponseBody
  @ExceptionHandler(JsfException.class)
  public Object handleJsfException(JsfException e) {
    final String message = resolveJsfExceptionMessage(e);
    return createErrorResponseData(e.getData().getId(), e.getData().getCode(), message);
  }
}
