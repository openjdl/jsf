package com.openjdl.jsf.webflux.modbus.dtu;

/**
 * Created at 2020-12-14 11:14:02
 *
 * @author kidal
 * @since 0.5
 */
public enum ModbusDtuFc {
  UNKNOWN(0x00), // 0
  READ_COIL(0x01), // 1
  READ_DISCRETE_INPUT(0x02), // 2
  READ_HOLDING_REGISTERS(0x03), // 3
  READ_INPUT_REGISTERS(0x04), // 4
  WRITE_SINGLE_COIL(0x05), // 5
  WRITE_SINGLE_HOLDING_REGISTER(0x06), // 6
  WRITE_MULTIPLE_COILS(0x0F), // 15
  WRITE_MULTIPLE_HOLDING_REGISTERS(0x10), // 16
  ;

  private final short code;

  ModbusDtuFc(int code) {
    this.code = (short) code;
  }

  public short getCode() {
    return code;
  }
}
