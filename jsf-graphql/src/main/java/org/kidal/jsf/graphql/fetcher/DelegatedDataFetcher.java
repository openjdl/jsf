package org.kidal.jsf.graphql.fetcher;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kidal.jsf.core.pagination.PageArgs;
import org.kidal.jsf.core.unify.UnifiedApiContext;
import org.kidal.jsf.core.utils.ReflectionUtils;
import org.kidal.jsf.graphql.annotation.GraphqlParameters;
import org.kidal.jsf.graphql.query.GraphqlFetchingEnvironment;
import org.springframework.core.convert.ConversionService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created at 2020-08-05 22:11:35
 *
 * @author kidal
 * @since 0.1.0
 */
public class DelegatedDataFetcher extends BaseGraphqlDataFetcher<Object> {
  /**
   * 类型转换服务
   */
  @NotNull
  private final ConversionService conversionService;

  /**
   * 类型名
   */
  @NotNull
  private final String typeName;

  /**
   * 字段名
   */
  private final String fieldName;

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
  public DelegatedDataFetcher(@NotNull ConversionService conversionService,
                              @NotNull String typeName,
                              @NotNull String fieldName,
                              @NotNull Object bean,
                              @NotNull Method method) {
    this.conversionService = conversionService;
    this.typeName = typeName;
    this.fieldName = fieldName;
    this.bean = bean;
    this.method = method;
  }

  /**
   *
   */
  @Nullable
  @Override
  public Object fetch(@NotNull GraphqlFetchingEnvironment env) throws Exception {
    // 参数
    Object[] parameters = new Object[method.getParameterCount()];
    for (int i = 0; i < method.getParameterCount(); i++) {
      Class<?> type = method.getParameterTypes()[i];
      if (type == GraphqlFetchingEnvironment.class) {
        parameters[i] = env;
      } else if (type == PageArgs.class) {
        parameters[i] = env.getPageArgs();
      } else if (type == UnifiedApiContext.class) {
        parameters[i] = new UnifiedApiContext(env, env.getParameters());
      } else {
        Annotation[] annotations = method.getParameterAnnotations()[i];
        if (annotations.length > 0 && annotations[0] instanceof GraphqlParameters) {
          parameters[i] = ReflectionUtils.mapToPojo(env.getEnvironment().getArguments(), type, conversionService);
        }
      }
    }

    return method.invoke(bean, parameters);
  }

  //--------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------

  @NotNull
  public String getTypeName() {
    return typeName;
  }

  public String getFieldName() {
    return fieldName;
  }

  @NotNull
  public Object getBean() {
    return bean;
  }

  @NotNull
  public Method getMethod() {
    return method;
  }
}
