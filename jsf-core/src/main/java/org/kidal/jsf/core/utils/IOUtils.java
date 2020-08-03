package org.kidal.jsf.core.utils;

import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author kidal
 */
public class IOUtils {
  /**
   *
   */
  public static void close(Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (IOException ignored) {

      }
    }
  }

  /**
   *
   */
  public static byte[] serializeObject(Object in) throws IOException {
    ObjectOutputStream oos = null;
    ByteArrayOutputStream bos = null;
    byte[] data;

    try {
      bos = new ByteArrayOutputStream();
      oos = new ObjectOutputStream(bos);

      oos.writeObject(in);
      oos.flush();

      data = bos.toByteArray();
    } finally {
      close(oos);
      close(bos);
    }

    return data;
  }

  /**
   *
   */
  public static Object deserializeObject(byte[] data) throws IOException, ClassNotFoundException {
    ByteArrayInputStream bis = null;
    ObjectInputStream ois = null;
    Object object;

    try {
      bis = new ByteArrayInputStream(data);
      ois = new ObjectInputStream(bis);

      object = ois.readObject();
    } finally {
      close(ois);
      close(bis);
    }

    return object;
  }

  /**
   *
   */
  public static byte[] readAllBytes(File file) throws IOException {
    final long length = file.length();
    final byte[] data = new byte[(int) length];

    try (FileInputStream fileInputStream = new FileInputStream(file)) {
      int bytesRead = fileInputStream.read(data);
      if (bytesRead != length) {
        throw new IOException(bytesRead + " bytes read, need " + length + " bytes");
      }
    }

    return data;
  }

  /**
   *
   */
  public static byte[] readAllBytes(InputStream in) throws IOException {
    final int length = 8192;
    final byte[] buf = new byte[length];
    int bytesRead;

    ByteArrayOutputStream out = new ByteArrayOutputStream();

    while ((bytesRead = in.read(buf, 0, length)) != -1) {
      out.write(buf, 0, bytesRead);
    }

    return out.toByteArray();
  }

  /**
   *
   */
  public static byte[] readAllBytes(String location) throws IOException {
    return readAllBytes(ResourceUtils.getFile(location));
  }

  /**
   *
   */
  public static String readAllText(BufferedReader reader) throws IOException {
    final String lineSeparator = System.getProperty("line.separator");
    final StringBuilder builder = new StringBuilder();

    String line = reader.readLine();
    if (line != null) {
      builder.append(line);
    } else {
      return builder.toString();
    }
    while ((line = reader.readLine()) != null) {
      builder.append(lineSeparator).append(line);
    }
    return builder.toString();
  }

  /**
   *
   */
  public static String readAllText(File file, Charset charset) throws IOException {
    return new String(readAllBytes(file), charset);
  }

  /**
   *
   */
  public static String readAllText(File file) throws IOException {
    return readAllText(file, StandardCharsets.UTF_8);
  }

  /**
   *
   */
  public static String readAllText(String location, Charset charset) throws IOException {
    return new String(readAllBytes(location), charset);
  }

  /**
   *
   */
  public static String readAllText(String location) throws IOException {
    return readAllText(location, StandardCharsets.UTF_8);
  }

  /**
   *
   */
  public static String readAllText(InputStream in, Charset charset) throws IOException {
    final int length = 4096;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte[] data = new byte[length];
    int count;

    while ((count = in.read(data, 0, length)) != -1) {
      out.write(data, 0, count);
    }

    return new String(out.toByteArray(), charset);
  }

  /**
   *
   */
  public static String readAllText(InputStream in) throws IOException {
    return readAllText(in, StandardCharsets.UTF_8);
  }
}
