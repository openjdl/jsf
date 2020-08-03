package org.kidal.jsf.core.utils;

import java.util.Arrays;

/**
 * @author kidal
 */
public enum CompressionType {
  /**
   * 无压缩
   */
  NONE(0),

  /**
   * Zip压缩
   */
  ZIP(1),

  /**
   * Snappy压缩
   */
  SNAPPY(2),

  //
  ;

  /**
   * 通过算法类型研发查询
   */
  public static CompressionType valueOfAlgorithmType(final int algorithmType) {
    return Arrays.stream(values())
      .filter(it -> it.algorithmType == algorithmType)
      .findFirst()
      .orElse(null);
  }

  /**
   * 算法类型掩码
   */
  private final byte algorithmType;

  /**
   *
   */
  CompressionType(int id) {
    this.algorithmType = (byte) id;
  }

  /**
   *
   */
  public byte getAlgorithmType() {
    return algorithmType;
  }
}
