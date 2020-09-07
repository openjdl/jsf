package io.tdi.jsf.graphql.webflux.controller;

import com.google.common.collect.Lists;
import graphql.ErrorType;
import graphql.ExceptionWhileDataFetching;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import io.tdi.jsf.graphql.GraphqlService;
import org.jetbrains.annotations.NotNull;
import io.tdi.jsf.core.cipher.UserIdentificationNumber;
import io.tdi.jsf.core.exception.JsfException;
import io.tdi.jsf.core.exception.JsfExceptions;
import io.tdi.jsf.core.exception.JsfResolvedException;
import io.tdi.jsf.core.utils.StringUtils;
import io.tdi.jsf.graphql.query.GraphqlFetchingWarningType;
import io.tdi.jsf.graphql.query.GraphqlQueryArgs;
import io.tdi.jsf.graphql.query.GraphqlQueryResults;
import io.tdi.jsf.graphql.utils.GraphqlUtils;
import io.tdi.jsf.webflux.controller.JsfRestController;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created at 2020-08-05 22:35:07
 *
 * @author kidal
 * @since 0.1.0
 */
@ResponseBody
public class GraphqlController extends JsfRestController {
  /**
   *
   */
  private final GraphqlService graphqlService;

  /**
   *
   */
  public GraphqlController(GraphqlService graphqlService) {
    this.graphqlService = graphqlService;
  }

  /**
   * 查询
   */
  @NotNull
  @ResponseBody
  public Mono<GraphqlControllerResponseBody> query(@NotNull @RequestBody GraphqlControllerRequestBody requestBody,
                                                   @NotNull ServerWebExchange exchange) {
    // 参数
    final String query = requestBody.getQuery();
    final Map<String, Object> variables = requestBody.getVariables() != null
      ? requestBody.getVariables()
      : Collections.emptyMap();
    if (StringUtils.isBlank(query)) {
      throw new JsfException(JsfExceptions.BAD_REQUEST);
    }

    // 开始查询时间
    final long queryStartMillis = System.currentTimeMillis();

    // 获取客户端IP
    final InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
    final String clientIp = remoteAddress == null ? "" : remoteAddress.getAddress().getHostName();

    // 解析X系列参数
    final Map<String, String> xVariables = GraphqlUtils.parseXVariables(
      exchange
        .getRequest()
        .getHeaders()
        .entrySet()
        .stream()
        .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
          final List<String> values = entry.getValue();
          return values.size() > 0 ? values.get(0) : "";
        }))
    );

    // 解析通行证
    final String uin = xVariables.get("uin");

    // 准备查询参数
    final GraphqlQueryArgs args = new GraphqlQueryArgs(
      UserIdentificationNumber.tryParse(uin),
      query,
      variables,
      clientIp,
      xVariables
    );

    // 执行查询
    final GraphqlQueryResults results = graphqlService.query(args);

    // 警告
    final List<String> warnings = prepareWarnings(results);

    // 错误
    final List<GraphqlControllerResponseBodyRelayError> errors = prepareRelayError(results);
    final Optional<GraphqlControllerResponseBodyRelayError> firstError = errors.stream().findFirst();
    final GraphqlControllerResponseBodyError error = firstError.isPresent()
      ? firstError.map(it -> new GraphqlControllerResponseBodyError(it.getId(), it.getCode(), it.getMessage(), null)).get()
      : null;

    // 饼干
    results.getCookies().forEach(cookie -> {
      final ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(cookie.getName(), cookie.getValue());
      builder.maxAge(cookie.getMaxAge());
      if (cookie.getDomain() != null) {
        builder.domain(cookie.getDomain());
      }
      if (cookie.getPath() != null) {
        builder.path(cookie.getPath());
      }
      builder.secure(cookie.isSecure());
      builder.httpOnly(cookie.isHttpOnly());
      builder.sameSite(cookie.getSameSite());
      exchange.getResponse().addCookie(builder.build());
    });

    // 时间
    final long queryEndMillis = System.currentTimeMillis();
    final long queryTookMillis = queryEndMillis - queryStartMillis;

    // done
    return Mono.just(
      new GraphqlControllerResponseBody(
        queryTookMillis, warnings, error,
        results.getExecutionResult().getData(),
        errors,
        results.getExecutionResult().getExtensions()
      )
    );
  }

  /**
   * 准备警告信息
   */
  @NotNull
  private List<String> prepareWarnings(@NotNull GraphqlQueryResults results) {
    final List<String> warnings = Lists.newArrayList();

    results.getWarnings().forEach((type, list) -> {
      if (type == GraphqlFetchingWarningType.DEPRECATED) {
        list.forEach(warning -> warnings.add(String.format("查询了被否决的字段: %s.%s，字段被否决的原因: %s。",
          warning.getTypeName(), warning.getFieldName(), warning.getMessage())));
      }
    });

    return warnings;
  }

  /**
   *
   */
  @NotNull
  private List<GraphqlControllerResponseBodyRelayError> prepareRelayError(@NotNull GraphqlQueryResults results) {
    final List<GraphQLError> errors = results.getExecutionResult().getErrors();
    if (errors == null || errors.isEmpty()) {
      return Collections.emptyList();
    }

    return errors
      .stream()
      .map(it -> {
        long id = JsfExceptions.SERVER_INTERNAL_ERROR.getId();
        String code = JsfExceptions.SERVER_INTERNAL_ERROR.getCode();
        String message = it.getMessage();
        JsfException jsfException = null;

        if (it instanceof ExceptionWhileDataFetching) {
          Throwable innerException = ((ExceptionWhileDataFetching) it).getException();

          if (innerException instanceof JsfException) {
            jsfException = (JsfException) innerException;
          } else if (
            innerException instanceof InvocationTargetException
              && (((InvocationTargetException) innerException).getTargetException()) instanceof JsfException
          ) {
            jsfException = (JsfException) ((InvocationTargetException) innerException).getTargetException();
          }
        }
        if (jsfException != null) {
          try {
            if (jsfException instanceof JsfResolvedException) {
              id = jsfException.getData().getId();
              code = jsfException.getData().getCode();
              message = jsfException.getMessage();
            } else {
              id = jsfException.getData().getId();
              code = jsfException.getData().getCode();
              message = resolveJsfExceptionMessage(jsfException);
            }
          } catch (Exception ignored) {

          }
        }

        List<SourceLocation> locations = it.getLocations() == null ? Collections.emptyList() : it.getLocations();
        ErrorType errorType = (it.getErrorType() == null ? ErrorType.ExecutionAborted : (ErrorType) it.getErrorType());
        List<Object> path = it.getPath() == null ? Collections.emptyList() : it.getPath();

        return new GraphqlControllerResponseBodyRelayError(id, code, message, locations, errorType, path);
      })
      .collect(Collectors.toList());
  }
}
