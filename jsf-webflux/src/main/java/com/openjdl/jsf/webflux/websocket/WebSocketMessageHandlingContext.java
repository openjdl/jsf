package com.openjdl.jsf.webflux.websocket;

import com.openjdl.jsf.core.exception.JsfException;
import com.openjdl.jsf.core.exception.JsfExceptions;
import com.openjdl.jsf.core.pagination.PageArgs;
import com.openjdl.jsf.core.pagination.PageSortArg;
import com.openjdl.jsf.core.sugar.BeanAccessor;
import com.openjdl.jsf.core.sugar.BeanPropertyAccessor;
import com.openjdl.jsf.core.sugar.EmptyBeanAccessor;
import com.openjdl.jsf.core.sugar.GenericBeanAccessor;
import com.openjdl.jsf.core.utils.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.convert.ConversionService;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created at 2020-08-12 22:26:43
 *
 * @author kidal
 * @since 0.1.0
 */
public class WebSocketMessageHandlingContext implements BeanAccessor {
  /**
   *
   */
  @NotNull
  private final WebSocketSessionManager sessionManager;

  /**
   *
   */
  @NotNull
  private final WebSocketSession session;

  /**
   *
   */
  @NotNull
  private final WebSocketPayload payload;

  /**
   *
   */
  @NotNull
  private final BeanAccessor parameters;

  /**
   *
   */
  public WebSocketMessageHandlingContext(@NotNull WebSocketSessionManager sessionManager,
                                         @NotNull WebSocketSession session,
                                         @NotNull WebSocketPayload payload) {
    this.sessionManager = sessionManager;
    this.session = session;
    this.payload = payload;

    if (payload.getData() != null) {
      parameters = new GenericBeanAccessor(payload.getData(), sessionManager.getConversionService(), () -> new JsfException(JsfExceptions.BAD_REQUEST));
    } else {
      parameters = new EmptyBeanAccessor(sessionManager.getConversionService(), () -> new JsfException(JsfExceptions.BAD_REQUEST));
    }
  }

  /**
   * 获取分页参数
   *
   * @return 分页参数
   */
  @NotNull
  public PageArgs getPageArgs() {
    // 页码
    int page = parameters.getInteger("page").orElse(PageArgs.DEFAULT_PAGE);

    // 每页个数
    int limit = parameters.getInteger("limit").orElse(PageArgs.DEFAULT_LIMIT);

    // 排序
    List<String> sorts = parameters.getList("sorts", String.class).orElse(Collections.emptyList());
    List<PageSortArg> pageSortArgs = sorts.stream()
      .map(it -> it.split(" "))
      .filter(it -> it.length == 2 && StringUtils.isNoneBlank(it[0], it[1]))
      .map(pair -> new PageSortArg(pair[0], "desc".equals(pair[1].toLowerCase())))
      .collect(Collectors.toList());

    // done
    return PageArgs.of(page, limit, pageSortArgs);
  }

  //--------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------

  @NotNull
  @Override
  public BeanPropertyAccessor getPropertyAccessor() {
    return getParameters().getPropertyAccessor();
  }

  /**
   *
   */
  @Nullable
  @Override
  public ConversionService getConversionService() {
    return getParameters().getConversionService();
  }

  @NotNull
  @Override
  public Supplier<RuntimeException> getExceptionSupplier() {
    return getParameters().getExceptionSupplier();
  }

  @NotNull
  public WebSocketSessionManager getSessionManager() {
    return sessionManager;
  }

  @NotNull
  public WebSocketSession getSession() {
    return session;
  }

  @NotNull
  public WebSocketPayload getPayload() {
    return payload;
  }

  @NotNull
  public BeanAccessor getParameters() {
    return parameters;
  }
}
