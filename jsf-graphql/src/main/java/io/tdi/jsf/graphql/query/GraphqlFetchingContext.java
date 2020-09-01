package io.tdi.jsf.graphql.query;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import io.tdi.jsf.core.cipher.UserIdentificationNumber;
import org.springframework.core.convert.ConversionService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created at 2020-08-05 16:58:37
 *
 * @author kidal
 * @since 0.1.0
 */
public class GraphqlFetchingContext {
  /**
   * 用户通行证
   */
  @Nullable
  private final UserIdentificationNumber uin;

  /**
   * 客户端IP地址
   */
  @NotNull
  private final String clientIp;

  /**
   * 客户端发送的X系列参数
   */
  private final ImmutableMap<String, String> xVariables;

  /**
   * 饼干
   */
  private final List<GraphqlCookie> cookies = Lists.newArrayList();

  /**
   * 警告
   */
  private final ConcurrentMap<GraphqlFetchingWarningType, CopyOnWriteArrayList<GraphqlFetchingWarning>> warnings = Maps.newConcurrentMap();

  /**
   * 其他参数
   */
  private final Map<String, Object> variables = Maps.newHashMap();

  /**
   * 转换服务
   */
  @NotNull
  private final ConversionService conversionService;

  /**
   *
   */
  public GraphqlFetchingContext(
    @Nullable UserIdentificationNumber uin,
    @NotNull String clientIp,
    @NotNull Map<String, String> xVariables,
    @NotNull ConversionService conversionService
  ) {
    this.uin = uin;
    this.clientIp = clientIp;
    this.xVariables = ImmutableMap.copyOf(xVariables);
    this.conversionService = conversionService;
  }

  /**
   * 添加饼干
   */
  public GraphqlFetchingContext addCookie(@NotNull GraphqlCookie cookie) {
    cookies.add(cookie);
    return this;
  }

  /**
   * 添加警告
   */
  public GraphqlFetchingContext addWarning(@NotNull GraphqlFetchingWarningType type,
                                           @NotNull String typeName,
                                           @NotNull String fieldName,
                                           @NotNull String message) {
    CopyOnWriteArrayList<GraphqlFetchingWarning> list = warnings.get(type);
    if (list == null) {
      list = new CopyOnWriteArrayList<>();
      CopyOnWriteArrayList<GraphqlFetchingWarning> prev = warnings.putIfAbsent(type, list);
      list = prev != null ? prev : list;
    }
    list.add(new GraphqlFetchingWarning(typeName, fieldName, message));
    return this;
  }

  /**
   *
   */
  @Nullable
  public UserIdentificationNumber getUin() {
    return uin;
  }

  /**
   *
   */
  @NotNull
  public String getClientIp() {
    return clientIp;
  }

  /**
   *
   */
  public ImmutableMap<String, String> getxVariables() {
    return xVariables;
  }

  /**
   *
   */
  public List<GraphqlCookie> getCookies() {
    return cookies;
  }

  /**
   *
   */
  public ConcurrentMap<GraphqlFetchingWarningType, CopyOnWriteArrayList<GraphqlFetchingWarning>> getWarnings() {
    return warnings;
  }

  /**
   *
   */
  public Map<String, Object> getVariables() {
    return variables;
  }

  /**
   *
   */
  @NotNull
  public ConversionService getConversionService() {
    return conversionService;
  }
}
