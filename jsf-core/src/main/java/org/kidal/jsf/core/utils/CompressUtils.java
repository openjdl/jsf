package org.kidal.jsf.core.utils;

import org.jetbrains.annotations.NotNull;
import org.xerial.snappy.Snappy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

/**
 * Created at 2020-08-04 17:30:00
 *
 * @author kidal
 * @since 0.1.0
 */
public class CompressUtils {
  /**
   * 缓存大小
   */
  private static final int BUF_SIZE = 8192;

  /**
   * 压缩数据
   */
  @NotNull
  public static byte[] compress(@NotNull CompressionType type,
                                @NotNull byte[] data, int offset, int length) throws IOException {
    switch (type) {
      case NONE:
        if (offset == 0 && length == data.length) {
          return data;
        }
        byte[] buf = new byte[length];
        System.arraycopy(data, offset, buf, 0, length);
        return buf;

      case ZIP:
        return zipCompress(data, offset, length);

      case SNAPPY:
        return snappyUncompress(data, offset, length);

      default:
        throw new IllegalStateException("Unknown compression type `" + type + "`");
    }
  }

  /**
   * 解压数据
   */
  @NotNull
  public static byte[] uncompress(@NotNull CompressionType type,
                                  @NotNull byte[] data, int offset, int length) throws IOException {
    switch (type) {
      case NONE:
        if (offset == 0 && length == data.length) {
          return data;
        }
        byte[] buf = new byte[length];
        System.arraycopy(data, offset, buf, 0, length);
        return buf;

      case ZIP:
        return zipUncompress(data, offset, length);

      case SNAPPY:
        return snappyUncompress(data, offset, length);

      default:
        throw new IllegalStateException("Unknown compression type `" + type + "`");
    }
  }

  /**
   * 压缩数据并添加算法类型掩码
   */
  @NotNull
  public byte[] compressWithAlgorithmType(@NotNull CompressionType type,
                                          @NotNull byte[] data, int offset, int length) throws IOException {
    byte[] bytes = compress(type, data, offset, length);
    byte[] markedBytes = new byte[length + 1];

    markedBytes[0] = type.getAlgorithmType();
    System.arraycopy(bytes, 0, markedBytes, 1, length);
    return markedBytes;
  }

  /**
   * 通过算法类型掩码解压数据
   */
  @NotNull
  public byte[] uncompressWithAlgorithmType(@NotNull byte[] data, int offset, int length) throws IOException {
    return uncompress(CompressionType.valueOfAlgorithmType(data[0]), data, offset + 1, length - 1);
  }

  /**
   * Zip压缩
   */
  @NotNull
  public static byte[] zipCompress(@NotNull byte[] data, int offset, int length) throws IOException {
    try (ByteArrayInputStream in = new ByteArrayInputStream(data, offset, length)) {
      try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
        try (DeflaterOutputStream deflater = new DeflaterOutputStream(out)) {
          writeTo(in, deflater);
          deflater.finish();
          return out.toByteArray();
        }
      }
    }
  }

  /**
   * Zip 解压
   */
  @NotNull
  public static byte[] zipUncompress(@NotNull byte[] data, int offset, int length) throws IOException {
    try (ByteArrayInputStream in = new ByteArrayInputStream(data, offset, length)) {
      try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
        try (InflaterOutputStream inflater = new InflaterOutputStream(out)) {
          writeTo(in, inflater);
          inflater.finish();
          return out.toByteArray();
        }
      }
    }
  }

  /**
   * Snappy压缩
   */
  @NotNull
  public static byte[] snappyCompress(@NotNull byte[] data, int offset, int length) throws IOException {
    if (offset == 0 && length == data.length) {
      return Snappy.compress(data);
    } else {
      byte[] buf = new byte[length];
      System.arraycopy(data, offset, buf, 0, length);
      return Snappy.compress(buf);
    }
  }

  /**
   * Snappy解压
   */
  @NotNull
  public static byte[] snappyUncompress(@NotNull byte[] data, int offset, int length) throws IOException {
    if (offset == 0 && length == data.length) {
      return Snappy.uncompress(data);
    } else {
      byte[] buf = new byte[length];
      System.arraycopy(data, offset, buf, 0, length);
      return Snappy.uncompress(buf);
    }
  }

  /**
   *
   */
  private static void writeTo(@NotNull ByteArrayInputStream in, @NotNull OutputStream out) throws IOException {
    byte[] buffer = new byte[BUF_SIZE];
    int count;
    while ((count = in.read(buffer, 0, BUF_SIZE)) != -1) {
      out.write(buffer, 0, count);
    }
  }
}
