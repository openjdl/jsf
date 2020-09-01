package io.tdi.jsf.core.utils;

import java.nio.charset.StandardCharsets;

/**
 * Created at 2020-08-04 17:30:00
 *
 * @author kidal
 * @since 0.1.0
 */
public class UrlUtils {
  public static final String HTTP_PREFIX = "http://";
  public static final String HTTPS_PREFIX = "https://";
  public static final String HTTPX_PREFIX = "//";

  @SuppressWarnings("SpellCheckingInspection")
  private static final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_.!~*'()";

  /**
   *
   */
  public static String encodeUriComponent(String input) {
    if (StringUtils.isEmpty(input)) {
      return input;
    }
    int l = input.length();
    StringBuilder o = new StringBuilder(l * 3);
    for (int i = 0; i < l; i++) {
      String e = input.substring(i, i + 1);
      if (!ALLOWED_CHARS.contains(e)) {
        byte[] b = e.getBytes(StandardCharsets.UTF_8);
        o.append(getHex(b));
        continue;
      }
      o.append(e);
    }
    return o.toString();
  }

  /**
   *
   */
  private static String getHex(byte[] buf) {
    StringBuilder o = new StringBuilder(buf.length * 3);
    for (byte aBuf : buf) {
      int n = aBuf & 0xff;
      o.append("%");
      if (n < 0x10) {
        o.append("0");
      }
      o.append(Long.toString(n, 16).toUpperCase());
    }
    return o.toString();
  }

  /**
   *
   */
  public static String normalize(String url) {
    if (url == null || url.isEmpty()) {
      return url;
    }
    if (url.startsWith(HTTPX_PREFIX)) {
      return url;
    } else if (url.startsWith(HTTP_PREFIX)) {
      return url.substring(5);
    } else if (url.startsWith(HTTPS_PREFIX)) {
      return url.substring(6);
    } else {
      return "//" + url;
    }
  }
}
