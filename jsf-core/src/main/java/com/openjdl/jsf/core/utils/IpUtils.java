package com.openjdl.jsf.core.utils;

import org.jetbrains.annotations.NotNull;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created at 2020-08-04 23:32:59
 *
 * @author kidal
 * @since 0.1.0
 */
public class IpUtils {
  public static final Set<Pattern> LAN_IP_PATTERNS = new HashSet<>();

  static {
    // A类地址范围：10.0.0.0—10.255.255.255
    LAN_IP_PATTERNS.add(Pattern.compile("^10\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])$"));
    // B类地址范围: 172.16.0.0---172.31.255.255
    LAN_IP_PATTERNS.add(Pattern.compile("^172\\.(1[6789]|2[0-9]|3[01])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])$"));
    // C类地址范围: 192.168.0.0---192.168.255.255
    LAN_IP_PATTERNS.add(Pattern.compile("^192\\.168\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])$"));
  }

  /**
   * 解析本地IP
   *
   * @return 本地IP
   */
  @NotNull
  public static String resolveLanIp() {
    try {
      Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
      while (allNetInterfaces.hasMoreElements()) {
        NetworkInterface netInterface = allNetInterfaces.nextElement();
        Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
        while (addresses.hasMoreElements()) {
          // 获取地址
          final InetAddress inetAddress = addresses.nextElement();

          // 必须是IPV4
          if (!(inetAddress instanceof Inet4Address)) {
            continue;
          }

          // TEMP: fix
          if (netInterface.getName().equals("eth0")) {
            return inetAddress.getHostAddress();
          }

          // 不能是Loopback
          if (inetAddress.isLoopbackAddress()) {
            continue;
          }

          // 是否是内网
          boolean isLocal = inetAddress.isMulticastAddress() ||
            inetAddress.isAnyLocalAddress() ||
            inetAddress.isLinkLocalAddress() ||
            inetAddress.isSiteLocalAddress() ||
            inetAddress.isMCGlobal() ||
            inetAddress.isMCNodeLocal() ||
            inetAddress.isMCLinkLocal() ||
            inetAddress.isMCSiteLocal() ||
            inetAddress.isMCOrgLocal();

          // 是否是特殊放行IP
          boolean isSpecial = inetAddress.getHostAddress().equals("127.0.0.1")
            || LAN_IP_PATTERNS.stream().anyMatch(it -> it.matcher(inetAddress.getHostAddress()).find());

          if (isSpecial || !isLocal) {
            return inetAddress.getHostAddress();
          }
        }
      }
    } catch (Exception e) {
      throw new IllegalStateException("Resolve local ip failed", e);
    }
    throw new IllegalStateException("Resolve local ip failed");
  }
}
