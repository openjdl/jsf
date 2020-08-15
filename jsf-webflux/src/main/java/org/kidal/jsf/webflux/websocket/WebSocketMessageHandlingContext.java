package org.kidal.jsf.webflux.websocket;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kidal.jsf.core.exception.JsfException;
import org.kidal.jsf.core.exception.JsfExceptions;
import org.kidal.jsf.core.pagination.PageArgs;
import org.kidal.jsf.core.pagination.PageSortArg;
import org.kidal.jsf.core.sugar.BeanAccessor;
import org.kidal.jsf.core.sugar.BeanPropertyAccessor;
import org.kidal.jsf.core.sugar.EmptyBeanAccessor;
import org.kidal.jsf.core.sugar.GenericBeanAccessor;
import org.kidal.jsf.core.utils.StringUtils;
import org.springframework.core.convert.ConversionService;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

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
  private final SessionManager sessionManager;

  /**
   *
   */
  @NotNull
  private final Session session;

  /**
   *
   */
  @NotNull
  private final Payload payload;

  /**
   *
   */
  @NotNull
  private final BeanAccessor parameters;

  /**
   *
   */
  public WebSocketMessageHandlingContext(@NotNull SessionManager sessionManager,
                                         @NotNull Session session,
                                         @NotNull Payload payload) {
    this.sessionManager = sessionManager;
    this.session = session;
    this.payload = payload;

    if (payload.getData() != null) {
      parameters = new GenericBeanAccessor(payload.getData(), () -> new JsfException(JsfExceptions.BAD_REQUEST));
    } else {
      parameters = new EmptyBeanAccessor(() -> new JsfException(JsfExceptions.BAD_REQUEST));
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
    PageSortArg[] pageSortArgs = sorts.stream()
      .map(it -> it.split(" "))
      .filter(it -> it.length == 2 && StringUtils.isNoneBlank(it[0], it[1]))
      .map(pair -> new PageSortArg(pair[0], "desc".equals(pair[1].toLowerCase())))
      .toArray(PageSortArg[]::new);

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
  public SessionManager getSessionManager() {
    return sessionManager;
  }

  @NotNull
  public Session getSession() {
    return session;
  }

  @NotNull
  public Payload getPayload() {
    return payload;
  }

  @NotNull
  public BeanAccessor getParameters() {
    return parameters;
  }
}
