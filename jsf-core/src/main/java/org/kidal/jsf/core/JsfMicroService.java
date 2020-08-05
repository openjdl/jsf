package org.kidal.jsf.core;

import org.jetbrains.annotations.NotNull;
import org.kidal.jsf.core.crypto.BouncyCastleCryptoProvider;
import org.kidal.jsf.core.exception.JsfException;
import org.kidal.jsf.core.utils.IpUtils;
import org.kidal.jsf.core.utils.JsonUtils;
import org.kidal.jsf.core.utils.ProcessUtils;
import org.kidal.jsf.core.utils.YamlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created at 2020-08-04 23:12:51
 *
 * @author kidal
 * @since 0.1.0
 */
public class JsfMicroService {
  /**
   * 日志
   */
  public static final Logger LOG = LoggerFactory.getLogger("jsf-micro-service");

  /**
   * 元数据
   */
  static JsfMicroServiceMetadata metadata;

  /**
   * 已经启动
   */
  static volatile boolean launched = false;

  /**
   * 运行中
   */
  static volatile boolean running = false;

  /**
   * 服务
   */
  static final CopyOnWriteArrayList<JsfMicroServiceModule> SERVICES = new CopyOnWriteArrayList<>();

  /**
   * 监听
   */
  static final CopyOnWriteArrayList<JsfMicroServiceListener> LISTENERS = new CopyOnWriteArrayList<>();

  /**
   * 启动
   */
  public static void bootstrap(
    @NotNull String group,
    @NotNull String name,
    int port,
    @NotNull WebApplicationType webApplicationType,
    @NotNull Class<?> entryClass,
    String... args
  ) throws Exception {
    // 安装缺少的加密解密库
    BouncyCastleCryptoProvider.install();

    // 将进程号写入磁盘
    ProcessUtils.writeProcessId(".pid");

    // 本机IP
    final String lanIp = IpUtils.resolveLanIp();

    // 读取默认配置
    final JsfMicroServiceMetadata metadata = new JsfMicroServiceMetadata();
    metadata.setGroup(group);
    metadata.setName(name);
    metadata.getInstance().setLanIp(lanIp);
    metadata.getInstance().setWanIp(lanIp);
    metadata.getInstance().setPort(port);
    metadata.getInstance().setUuid(String.format("%s:%d", lanIp, metadata.getInstance().getPort()));

    // log
    LOG.info("Launching\n{}", JsonUtils.toPrettyString(metadata));

    // springboot
    try {
      new SpringApplicationBuilder(entryClass).web(webApplicationType).run(args);
    } catch (JsfException e) {
      LOG.error(e.formatMessage(), e);
    }
  }

  /**
   *
   */
  private static JsfMicroServiceMetadata loadMetadata(String filename, String name) {
    JsfMicroServiceMetadata metadata = null;
    File file = new File(filename);
    if (file.exists()) {
      try (FileInputStream in = new FileInputStream(file)) {
        // buffer
        byte[] bytes = new byte[(int) file.length()];
        //noinspection ResultOfMethodCallIgnored
        in.read(bytes);
        // convert
        metadata = YamlUtils.toObject(new String(bytes, StandardCharsets.UTF_8), JsfMicroServiceMetadata.class);
        // set
        metadata.setName(name);
      } catch (Exception e) {
        throw new IllegalStateException("Failed to read micro service metadata: " + filename, e);
      }
    }
    return metadata;
  }

  /**
   *
   */
  public static synchronized void register(JsfMicroServiceModule service) {
    if (launched) {
      throw new IllegalStateException(
        MessageFormat.format("Can't register lifecycle service {0}: lifecycle system is launched",
          service.getJsfMicroServiceModuleName()));
    }
    boolean hasOne = SERVICES.contains(service) ||
      SERVICES.stream()
        .anyMatch(it -> it.getJsfMicroServiceModuleName().equalsIgnoreCase(service.getJsfMicroServiceModuleName()));
    if (hasOne) {
      throw new IllegalStateException(
        MessageFormat.format("Can't register lifecycle service {0}: already registered",
          service.getJsfMicroServiceModuleName()));
    }
    SERVICES.add(service);
  }

  /**
   *
   */
  public static synchronized void listen(JsfMicroServiceListener listener) {
    LISTENERS.add(listener);
  }

  /**
   *
   */
  public static synchronized void deaf(JsfMicroServiceListener listener) {
    LISTENERS.remove(listener);
  }

  /**
   * 日志
   */
  public static void log(String service, String format, Object... args) {
    if (args.length > 0) {
      LOG.info("Service        {} -> {}", service, MessageFormat.format(format, args));
    } else {
      LOG.info("Service        {} -> {}", service, format);
    }
  }

  /**
   * 错误
   */
  public static void error(String service, Exception e, String format, Object... args) {
    if (e != null) {
      LOG.info("Service        {} -> {}", service, MessageFormat.format(format, args), e);
    } else {
      LOG.info("Service        {} -> {}", service, MessageFormat.format(format, args));
    }
  }

  /**
   * Log service initializing message.
   */
  static void logInitializing(String service, long nanoTime) {
    log(service, "Initializing({0}ms)", TimeUnit.NANOSECONDS.toMillis(nanoTime));
  }

  /**
   * Log service running message.
   */
  static void logRunning(String service, long nanoTime) {
    log(service, "Running({0}ms)", TimeUnit.NANOSECONDS.toMillis(nanoTime));
  }

  /**
   * Log service closed message.
   */
  static void logClosed(String service, long nanoTime) {
    log(service, "Closed({0}ms)", TimeUnit.NANOSECONDS.toMillis(nanoTime));
  }
}
