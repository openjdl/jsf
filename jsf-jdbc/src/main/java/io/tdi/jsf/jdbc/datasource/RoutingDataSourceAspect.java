package io.tdi.jsf.jdbc.datasource;

import io.tdi.jsf.jdbc.annotation.DataSourceMapping;
import io.tdi.jsf.jdbc.annotation.MasterDataSourceMapping;
import io.tdi.jsf.jdbc.annotation.SlaveDataSourceMapping;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.jetbrains.annotations.NotNull;
import io.tdi.jsf.core.utils.StringUtils;
import org.springframework.core.Ordered;

/**
 * Created at 2020-08-06 17:28:59
 *
 * @author kidal
 * @since 0.1.0
 */
@Aspect
public class RoutingDataSourceAspect implements Ordered {
  /**
   *
   */
  @NotNull
  private final RoutingDataSource routingDataSource;

  /**
   *
   */
  private final int order;

  /**
   *
   */
  public RoutingDataSourceAspect(@NotNull RoutingDataSource routingDataSource, int order) {
    this.routingDataSource = routingDataSource;
    this.order = order;
  }

  /**
   *
   */
  @Around("@annotation(dataSourceMapping)")
  public Object doDataSourceMapping(ProceedingJoinPoint pjp, DataSourceMapping dataSourceMapping) throws Throwable {
    return run(pjp, dataSourceMapping.value(), dataSourceMapping.readOnly());
  }

  /**
   *
   */
  @Around("@annotation(masterDataSourceMapping)")
  public Object doMasterDataSourceMapping(ProceedingJoinPoint pjp, MasterDataSourceMapping masterDataSourceMapping) throws Throwable {
    return run(pjp, masterDataSourceMapping.value(), false);
  }

  /**
   *
   */
  @Around("@annotation(slaveDataSourceMapping)")
  public Object doSlaveDataSourceMapping(ProceedingJoinPoint pjp, SlaveDataSourceMapping slaveDataSourceMapping) throws Throwable {
    return run(pjp, slaveDataSourceMapping.value(), true);
  }

  /**
   *
   */
  private Object run(ProceedingJoinPoint pjp, String groupName, boolean readOnly) throws Throwable {
    if (StringUtils.isEmpty(groupName)) {
      routingDataSource.push(readOnly);
      try {
        return pjp.proceed();
      } finally {
        routingDataSource.pop();
      }
    } else {
      routingDataSource.push(groupName, readOnly);
      try {
        return pjp.proceed();
      } finally {
        routingDataSource.pop(groupName);
      }
    }
  }

  //--------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------

  @NotNull
  public RoutingDataSource getRoutingDataSource() {
    return routingDataSource;
  }

  @Override
  public int getOrder() {
    return order;
  }
}
