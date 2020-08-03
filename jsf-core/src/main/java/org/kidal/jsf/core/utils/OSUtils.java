package org.kidal.jsf.core.utils;

/**
 * @author kidal
 */
@SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
public class OSUtils {
  /**
   *
   */
  public enum Type {
    /**
     * Any
     */
    Any("any"),
    /**
     * Linux
     */
    Linux("Linux"),
    Mac_OS("Mac OS"),
    Mac_OS_X("Mac OS X"),
    Windows("Windows"),
    OS2("OS/2"),
    Solaris("Solaris"),
    SunOS("SunOS"),
    MPEiX("MPE/iX"),
    HP_UX("HP-UX"),
    AIX("AIX"),
    OS390("OS/390"),
    FreeBSD("FreeBSD"),
    Irix("Irix"),
    Digital_Unix("Digital Unix"),
    NetWare_411("NetWare"),
    OSF1("OSF1"),
    OpenVMS("OpenVMS"),
    Others("Others");

    /**
     *
     */
    private final String description;

    /**
     *
     */
    Type(String description) {
      this.description = description;
    }

    /**
     *
     */
    @Override
    public String toString() {
      return description;
    }
  }

  private static final String NAME = System.getProperty("os.name").toLowerCase();
  private static final Type TYPE;

  //
  static {
    if (isAix()) {
      TYPE = Type.AIX;
    } else if (isDigitalUnix()) {
      TYPE = Type.Digital_Unix;
    } else if (isFreeBSD()) {
      TYPE = Type.FreeBSD;
    } else if (isHPUX()) {
      TYPE = Type.HP_UX;
    } else if (isIrix()) {
      TYPE = Type.Irix;
    } else if (isLinux()) {
      TYPE = Type.Linux;
    } else if (isMacOS()) {
      TYPE = Type.Mac_OS;
    } else if (isMacOSX()) {
      TYPE = Type.Mac_OS_X;
    } else if (isMPEiX()) {
      TYPE = Type.MPEiX;
    } else if (isNetWare()) {
      TYPE = Type.NetWare_411;
    } else if (isOpenVMS()) {
      TYPE = Type.OpenVMS;
    } else if (isOS2()) {
      TYPE = Type.OS2;
    } else if (isOS390()) {
      TYPE = Type.OS390;
    } else if (isOSF1()) {
      TYPE = Type.OSF1;
    } else if (isSolaris()) {
      TYPE = Type.Solaris;
    } else if (isSunOS()) {
      TYPE = Type.SunOS;
    } else if (isWindows()) {
      TYPE = Type.Windows;
    } else {
      TYPE = Type.Others;
    }
  }

  /**
   *
   */
  public static boolean isLinux() {
    return NAME.contains("linux");
  }

  /**
   *
   */
  public static boolean isMacOS() {
    return NAME.contains("mac") && NAME.indexOf("os") > 0 && !NAME.contains("x");
  }

  /**
   *
   */
  public static boolean isMacOSX() {
    return NAME.contains("mac") && NAME.indexOf("os") > 0 && NAME.indexOf("x") > 0;
  }

  /**
   *
   */
  public static boolean isWindows() {
    return NAME.contains("windows");
  }

  /**
   *
   */
  public static boolean isOS2() {
    return NAME.contains("os/2");
  }

  /**
   *
   */
  public static boolean isSolaris() {
    return NAME.contains("solaris");
  }

  /**
   *
   */
  public static boolean isSunOS() {
    return NAME.contains("sunos");
  }

  /**
   *
   */
  public static boolean isMPEiX() {
    return NAME.contains("mpe/ix");
  }

  /**
   *
   */
  public static boolean isHPUX() {
    return NAME.contains("hp-ux");
  }

  /**
   *
   */
  public static boolean isAix() {
    return NAME.contains("aix");
  }

  /**
   *
   */
  public static boolean isOS390() {
    return NAME.contains("os/390");
  }

  /**
   *
   */
  public static boolean isFreeBSD() {
    return NAME.contains("freebsd");
  }

  /**
   *
   */
  public static boolean isIrix() {
    return NAME.contains("irix");
  }

  /**
   *
   */
  public static boolean isDigitalUnix() {
    return NAME.contains("digital") && NAME.indexOf("unix") > 0;
  }

  /**
   *
   */
  public static boolean isNetWare() {
    return NAME.contains("netware");
  }

  /**
   *
   */
  public static boolean isOSF1() {
    return NAME.contains("osf1");
  }

  /**
   *
   */
  public static boolean isOpenVMS() {
    return NAME.contains("openvms");
  }
}
