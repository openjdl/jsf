package io.tdi.jsf.core.utils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Created at 2020-08-04 17:30:00
 *
 * @author kidal
 * @since 0.1.0
 */
public class SecurityUtils {
  /**
   *
   */
  public static String createNonce() {
    return RandomUtils.nextString(16);
  }

  /**
   *
   */
  public static String createMd5Signature(String key, long timestamp, String nonce, Object... params) {
    String signatureSource = createSignatureSource(key, timestamp, nonce, params);
    String sign;
    sign = CryptoUtils.toMd5String(signatureSource.getBytes(StandardCharsets.UTF_8)).toLowerCase();
    return sign;
  }

  /**
   *
   */
  public static String createShaSignature(String key, long timestamp, String nonce, Object... params) {
    String signatureSource = createSignatureSource(key, timestamp, nonce, params);
    String sign;
    sign = CryptoUtils.toShaString(signatureSource.getBytes(StandardCharsets.UTF_8)).toLowerCase();
    return sign;
  }

  /**
   *
   */
  public static String createSignatureSource(String key, long timestamp, String nonce, Object... params) {
    StringBuilder signSource = new StringBuilder();
    if (params.length == 0) {
      signSource
        .append(timestamp)
        .append('&')
        .append(nonce)
        .append('&')
        .append(key);
    } else {
      signSource
        .append(StringUtils.join(Arrays.stream(params)
          .map(Object::toString)
          .toArray(String[]::new), '&'))
        .append('&')
        .append(timestamp)
        .append('&')
        .append(nonce)
        .append('&')
        .append(key);
    }
    return signSource.toString();
  }
}
