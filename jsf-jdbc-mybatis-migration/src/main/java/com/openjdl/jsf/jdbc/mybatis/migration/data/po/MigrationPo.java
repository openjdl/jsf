package com.openjdl.jsf.jdbc.mybatis.migration.data.po;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * Created at 2020-10-09 12:46:32
 *
 * @author kidal
 * @since 0.4
 */
public class MigrationPo {
  private int id;

  @NotNull
  private String migrator = "";

  @NotNull
  private Date createdAt = new Date(System.currentTimeMillis());

  //--------------------------------------------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------------------------------------------

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @NotNull
  public String getMigrator() {
    return migrator;
  }

  public void setMigrator(@NotNull String migrator) {
    this.migrator = migrator;
  }

  @NotNull
  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(@NotNull Date createdAt) {
    this.createdAt = createdAt;
  }
}
