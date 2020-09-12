package com.openjdl.jsf.graphql.annotation;

import com.openjdl.jsf.graphql.fetcher.BaseUnitFetcherFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created at 2020-08-05 16:50:05
 *
 * @author kidal
 * @since 0.1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface GraphqlFetcher {
  /**
   * 获取类型名/字段名
   *
   * @return GraphQL中的类型名/字段名
   */
  String value() default "";

  /**
   * 单位工厂
   */
  Class<BaseUnitFetcherFactory> unitFactory() default BaseUnitFetcherFactory.class;
}
