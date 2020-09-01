package io.tdi.jsf.jdbc.mybatis;

import org.jetbrains.annotations.NotNull;

/**
 * Created at 2020-08-06 21:46:44
 *
 * @author kidal
 * @since 0.1.0
 */
public class JdbcMybatisServiceImpl implements JdbcMybatisService {
  /**
   *
   */
  @NotNull
  @Override
  public String getJsfServiceName() {
    return "JdbcMybatisService";
  }
}
