package org.kidal.jsf.graphql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

/**
 * Created at 2020-08-05 22:11:35
 *
 * @author kidal
 * @since 0.1.0
 */
public class GraphqlDelegatedDataFetcher extends BaseGraphqlDataFetcher<Object> {
  /**
   * 豆子
   */
  @NotNull
  private final Object bean;

  /**
   * 方法
   */
  @NotNull
  private final Method method;

  /**
   *
   */
  public GraphqlDelegatedDataFetcher(@NotNull Object bean, @NotNull Method method) {
    this.bean = bean;
    this.method = method;
  }

  /**
   *
   */
  @Nullable
  @Override
  public Object fetch(@NotNull GraphqlFetchingEnvironment env) throws Exception {
    return method.invoke(bean, env);
  }

  /**
   *
   */
  @NotNull
  public Object getBean() {
    return bean;
  }

  /**
   *
   */
  @NotNull
  public Method getMethod() {
    return method;
  }
}
