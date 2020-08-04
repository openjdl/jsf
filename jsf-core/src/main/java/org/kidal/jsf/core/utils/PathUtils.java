package org.kidal.jsf.core.utils;

import java.io.File;

/**
 * Created at 2020-08-04 17:30:00
 *
 * @author kidal
 * @since 0.1.0
 */
public class PathUtils {
  public static final Character UNIX_LIKE_SEPARATOR = '/';
  public static final Character WINDOWS_LIKE_SEPARATOR = '\\';

  /**
   * 标准化路劲.
   *
   * @param path 需要标准化的路劲.
   * @return 标准化后的路劲.
   */
  public static String normalize(String path) {
    char lastChar = path.charAt(path.length() - 1);

    if (lastChar == WINDOWS_LIKE_SEPARATOR || lastChar == UNIX_LIKE_SEPARATOR) {
      path = path.substring(0, path.length() - 1);
    }

    return path.replace(WINDOWS_LIKE_SEPARATOR, UNIX_LIKE_SEPARATOR);
  }

  /**
   * 合并路劲.
   *
   * @param paths 路径数组.
   * @return 合并后的路径.
   */
  public static String combine(String... paths) {
    return combine(true, paths);
  }

  /**
   * 合并路劲.
   *
   * @param strict 严格，不允许无效路径
   * @param paths  路径数组.
   * @return 合并后的路径.
   */
  public static String combine(boolean strict, String... paths) {
    StringBuilder builder = new StringBuilder();
    boolean firstPathAppended = false;

    for (String path : paths) {
      if (path == null || path.length() == 0) {
        if (strict) {
          throw new IllegalArgumentException("Path is null or empty");
        } else {
          continue;
        }
      }

      if (firstPathAppended) {
        char firstChar = path.charAt(0);

        if (firstChar != '\\' && firstChar != '/') {
          builder.append('/');
        }
      } else {
        firstPathAppended = true;
      }

      builder.append(normalize(path));
    }

    return builder.toString();
  }

  /**
   *
   */
  public static boolean createMissingParentDirectories(File file) {
    File parent = file.getParentFile();
    if (parent == null) {
      return true;
    }

    //noinspection ResultOfMethodCallIgnored
    parent.mkdirs();
    return parent.exists();
  }
}
