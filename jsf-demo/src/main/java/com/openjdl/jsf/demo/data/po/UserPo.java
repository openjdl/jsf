package com.openjdl.jsf.demo.data.po;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * Created at 2020-10-11 20:52:35
 *
 * @author kidal
 * @since 0.4
 */
public class UserPo {
  private int id;

  @NotNull
  private String username = "";

  @NotNull
  private String password = "";

  @NotNull
  private Date createdAt = new Date(System.currentTimeMillis());

  @NotNull
  private Date updatedAt = new Date(System.currentTimeMillis());

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
  public String getUsername() {
    return username;
  }

  public void setUsername(@NotNull String username) {
    this.username = username;
  }

  @NotNull
  public String getPassword() {
    return password;
  }

  public void setPassword(@NotNull String password) {
    this.password = password;
  }

  @NotNull
  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(@NotNull Date createdAt) {
    this.createdAt = createdAt;
  }

  @NotNull
  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(@NotNull Date updatedAt) {
    this.updatedAt = updatedAt;
  }
}
