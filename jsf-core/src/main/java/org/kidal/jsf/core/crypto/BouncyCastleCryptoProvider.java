package org.kidal.jsf.core.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Security;
import java.util.Map;

/**
 * Created at 2020-08-04 17:59:56
 *
 * @author kidal
 * @since 0.1.0
 */
public class BouncyCastleCryptoProvider {
  /**
   * 日志
   */
  private static final Logger LOG = LoggerFactory.getLogger(BouncyCastleCryptoProvider.class);

  /**
   * 安装
   */
  public static void install() {
    Security.addProvider(new BouncyCastleProvider());
    removeCryptographyRestrictions();
  }

  /**
   * 移除不需要的库.
   * Do the following, but with reflection to bypass access checks:
   * <p>
   * JceSecurity.isRestricted = false;
   * JceSecurity.defaultPolicy.perms.clear();
   * JceSecurity.defaultPolicy.add(CryptoAllPermission.INSTANCE);
   */
  private static void removeCryptographyRestrictions() {
    if (!isRestrictedCryptography()) {
      LOG.info("Cryptography restrictions removal not needed");
      return;
    }
    try {
      final Class<?> jceSecurity = Class.forName("javax.crypto.JceSecurity");
      final Class<?> cryptoPermissions = Class.forName("javax.crypto.CryptoPermissions");
      final Class<?> cryptoAllPermission = Class.forName("javax.crypto.CryptoAllPermission");

      final Field isRestrictedField = jceSecurity.getDeclaredField("isRestricted");
      isRestrictedField.setAccessible(true);
      final Field modifiersField = Field.class.getDeclaredField("modifiers");
      modifiersField.setAccessible(true);
      modifiersField.setInt(isRestrictedField, isRestrictedField.getModifiers() & ~Modifier.FINAL);
      isRestrictedField.set(null, false);

      final Field defaultPolicyField = jceSecurity.getDeclaredField("defaultPolicy");
      defaultPolicyField.setAccessible(true);
      final PermissionCollection defaultPolicy = (PermissionCollection) defaultPolicyField.get(null);

      final Field perms = cryptoPermissions.getDeclaredField("perms");
      perms.setAccessible(true);
      ((Map<?, ?>) perms.get(defaultPolicy)).clear();

      final Field instance = cryptoAllPermission.getDeclaredField("INSTANCE");
      instance.setAccessible(true);
      defaultPolicy.add((Permission) instance.get(null));

      LOG.info("Successfully removed cryptography restrictions");
    } catch (final Exception e) {
      LOG.warn("Failed to remove cryptography restrictions", e);
    }
  }

  /**
   *
   */
  private static boolean isRestrictedCryptography() {
    // This matches Oracle Java 7 and 8, but not Java 9 or OpenJDK.
    final String name = System.getProperty("java.runtime.name");
    final String ver = System.getProperty("java.version");
    return "Java(TM) SE Runtime Environment".equals(name)
      && ver != null && (ver.startsWith("1.7") || ver.startsWith("1.8"));
  }
}
