package io.tdi.jsf.core.utils;

import org.jetbrains.annotations.NotNull;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created at 2020-08-04 23:32:59
 *
 * @author kidal
 * @since 0.1.0
 */
public class IpUtils {
  /**
   * 解析本地IP
   *
   * @return 本地IP
   */
  @NotNull
  public static String resolveLanIp() {
    try {
      Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
      InetAddress inetAddress;
      while (allNetInterfaces.hasMoreElements()) {
        NetworkInterface netInterface = allNetInterfaces.nextElement();
        Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
        while (addresses.hasMoreElements()) {
          // 获取地址
          inetAddress = addresses.nextElement();

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
//              inetAddress.isLoopbackAddress() ||
            inetAddress.isLinkLocalAddress() ||
            inetAddress.isSiteLocalAddress() ||
            inetAddress.isMCGlobal() ||
            inetAddress.isMCNodeLocal() ||
            inetAddress.isMCLinkLocal() ||
            inetAddress.isMCSiteLocal() ||
            inetAddress.isMCOrgLocal();

          // 是否是特殊放行IP
          boolean isSpecial = inetAddress.getHostAddress().startsWith("192.168.")
            || inetAddress.getHostAddress().startsWith("172.16.")
            || inetAddress.getHostAddress().startsWith("10.")
            || "127.0.0.1".equals(inetAddress.getHostAddress());

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
