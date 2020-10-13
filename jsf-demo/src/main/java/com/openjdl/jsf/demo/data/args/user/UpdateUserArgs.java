package com.openjdl.jsf.demo.data.args.user;

/**
 * Created at 2020-10-12 15:11:20
 *
 * @author kidal
 * @since 0.4
 */
public class UpdateUserArgs {
  private int id;
  private String username;

  //--------------------------------------------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------------------------------------------

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
