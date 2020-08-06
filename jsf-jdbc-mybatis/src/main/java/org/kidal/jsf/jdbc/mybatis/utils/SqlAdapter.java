package org.kidal.jsf.jdbc.mybatis.utils;

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
