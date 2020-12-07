package com.openjdl.jsf.webflux.modbus.dtu.payload.response;

/**
 * Created at 2020-12-07 21:10:21
 *
 * @author kidal
 * @since 0.5
 */
public class ModbusDtuRegisterResponse implements ModbusDtuResponse {
  /**
   *
   */
  private final String data;

  /**
   *
   */
  public ModbusDtuRegisterResponse(String data) {
    this.data = data;
  }

  /**
   *
   */
  @Override
  public String toString() {
    return "ModbusDtuRegisterResponse{" +
      "data='" + data + '\'' +
      '}';
  }

  //--------------------------------------------------------------------------------------------------------------
  // Getters & Setters
  //--------------------------------------------------------------------------------------------------------------
  //region

  public String getData() {
    return data;
  }

  //endregion
}
