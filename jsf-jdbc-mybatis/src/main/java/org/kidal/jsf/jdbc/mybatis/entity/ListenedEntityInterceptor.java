package org.kidal.jsf.jdbc.mybatis.entity;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  private static final Logger LOG = LoggerFactory.getLogger(ListenedEntityInterceptor.class);

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
      Object arg = invocation.getArgs()[1];
      if (arg instanceof ListenedEntity) {
        ((ListenedEntity) arg).onBeforeUpdate();
      } else if (arg instanceof MapperMethod.ParamMap<?>) {
        // 列表
        @SuppressWarnings("unchecked")
        MapperMethod.ParamMap<Object> map = (MapperMethod.ParamMap<Object>) arg;
        if (map.containsKey("param1")) {
          Object param1 = map.get("param1");
          if (param1 instanceof Iterable) {
            //noinspection unchecked
            ((Iterable<ListenedEntity>) param1).forEach(ListenedEntity::onBeforeUpdate);
          } else {
            LOG.warn("ListenedEntity is not affected on: {}", ms.getId());
          }
        } else {
          LOG.warn("ListenedEntity is not affected on: {}", ms.getId());
        }
      } else {
        LOG.warn("ListenedEntity is not affected on: {}", ms.getId());
      }
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
