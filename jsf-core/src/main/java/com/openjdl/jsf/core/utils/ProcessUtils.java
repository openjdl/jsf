package com.openjdl.jsf.core.utils;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created at 2020-08-04 23:24:54
 *
 * @author kidal
 * @since 0.1.0
 */
public class ProcessUtils {
  /**
   * 获取进程ID
   */
  public static String getProcessId() {
    String name = ManagementFactory.getRuntimeMXBean().getName();
    return name.split("@")[0];
  }

  /**
   * 将进程ID写入磁盘
   */
  public static boolean writeProcessId(String path) throws IOException {
    final String processId = getProcessId();
    if (processId != null) {
      Files.write(Paths.get(path), processId.getBytes());
      return true;
    } else {
      return false;
    }
  }
}
