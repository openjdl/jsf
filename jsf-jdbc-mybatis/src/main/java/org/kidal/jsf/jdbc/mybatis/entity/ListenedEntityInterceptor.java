package org.kidal.jsf.jdbc.mybatis.entity;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created at 2020-08-06 21:33:44
 *
 * @author kidal
 * @since 0.1.0
 */
@Intercepts({
  @Signature(
    method = "update", type = Executor.class,
    args = {MappedStatement.class, Object.class}
  ),
  @Signature(
    method = "query", type = Executor.class,
    args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
  )
})
public class ListenedEntityInterceptor implements Interceptor {
  /**
   *
   */
  @Override
  public Object intercept(@NotNull Invocation invocation) throws Throwable {
    switch (invocation.getMethod().getName()) {
      case "query":
        return onQuery(invocation);
      case "update":
        return onUpdate(invocation);
      default:
        return invocation.proceed();
    }
  }

  /**
   *
   */
  @Nullable
  @SuppressWarnings("rawtypes")
  private Object onQuery(@NotNull Invocation invocation) throws Throwable {
    MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
    Object parameter = invocation.getArgs()[1];
    Object results = invocation.proceed();

    if (results == null) {
      return null;
    }

    if (ms.getResultMaps() == null) {
      return results;
    }

    if (
      ms
        .getResultMaps()
        .stream()
        .noneMatch(rm -> rm.getType() != null && ListenedEntity.class.isAssignableFrom(rm.getType()))
    ) {
      return results;
    }

    if (results instanceof ListenedEntity) {
      ((ListenedEntity) results).onAfterSelect();
    } else if (results.getClass().isArray()) {
      for (Object listener : (Object[]) results) {
        ((ListenedEntity) listener).onAfterSelect();
      }
    } else if (List.class.isAssignableFrom(results.getClass())) {
      for (Object listener : (List) results) {
        ((ListenedEntity) listener).onAfterSelect();
      }
    } else if (Map.class.isAssignableFrom(results.getClass())) {
      for (Object listener : ((Map) results).values()) {
        ((ListenedEntity) listener).onAfterSelect();
      }
    }

    return results;
  }

  /**
   *
   */
  private Object onUpdate(@NotNull Invocation invocation) throws Throwable {
    MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
    if (
      ms.getParameterMap() != null
        && ms.getParameterMap().getType() != null
        && ListenedEntity.class.isAssignableFrom(ms.getParameterMap().getType())
    ) {
      ((ListenedEntity) invocation.getArgs()[1]).onBeforeUpdate();
    }
    return invocation.proceed();
  }

  /**
   *
   */
  @Override
  public Object plugin(Object target) {
    return Plugin.wrap(target, this);
  }

  /**
   *
   */
  @Override
  public void setProperties(Properties properties) {

  }
}
