package io.tdi.jsf.jdbc.mybatis.utils;

import io.tdi.jsf.core.utils.StringUtils;

/**
 * Created at 2020-08-06 21:39:03
 *
 * @author kidal
 * @since 0.1.0
 */
public class SqlAdapter {
  public static SqlAdapter of(String sql) {
    return new SqlAdapter(sql);
  }

  public static SqlAdapter of(String... sql) {
    return new SqlAdapter(StringUtils.join(sql, "\n"));
  }

  private String sql;

  public SqlAdapter() {

  }

  public SqlAdapter(String sql) {
    this.sql = sql;
  }

  public String getSql() {
    return sql;
  }

  public void setSql(String sql) {
    this.sql = sql;
  }
}
