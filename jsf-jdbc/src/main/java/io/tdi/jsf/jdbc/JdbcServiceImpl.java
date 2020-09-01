package io.tdi.jsf.jdbc;

import org.jetbrains.annotations.NotNull;

/**
 * Created at 2020-08-06 17:17:32
 *
 * @author kidal
 * @since 0.1.0
 */
public class JdbcServiceImpl implements JdbcService {
  /**
   *
   */
  public JdbcServiceImpl() {
    registerSelf();
  }

  /**
   *
   */
  @NotNull
  @Override
  public String getJsfServiceName() {
    return "JsfJdbcService";
  }
}
