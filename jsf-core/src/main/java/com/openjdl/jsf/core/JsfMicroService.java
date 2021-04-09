package com.openjdl.jsf.core;

import com.openjdl.jsf.core.crypto.BouncyCastleCryptoProvider;
import com.openjdl.jsf.core.exception.JsfException;
import com.openjdl.jsf.core.utils.IpUtils;
import com.openjdl.jsf.core.utils.ProcessUtils;
import com.openjdl.jsf.core.utils.YamlUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
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
  public static JsfMicroServiceMetadata metadata;

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
  static final CopyOnWriteArrayList<JsfService> SERVICES = new CopyOnWriteArrayList<>();

  /**
   * 监听
   */
  static final CopyOnWriteArrayList<JsfMicroServiceListener> LISTENERS = new CopyOnWriteArrayList<>();

  /**
   *
   */
  public static void run(@NotNull Class<?> entryClass, @NotNull String[] args) throws Exception {
    if (args.length < 1) {
      throw new IllegalArgumentException("args.length < 1");
    }

    final String group = args[0];
    final String[] fixedArgs = ArrayUtils.subarray(args, 1, args.length);

    run(group, WebApplicationType.REACTIVE, entryClass, fixedArgs);
  }

  /**
   * 启动
   */
  public static void run(
    @NotNull String group,
    @NotNull WebApplicationType webApplicationType,
    @NotNull Class<?> entryClass,
    String[] args
  ) throws Exception {
    // 将进程号写入磁盘
    ProcessUtils.writeProcessId(".pid");

    // 准备启动
    prepareRun(group);

    // springboot
    try {
      new SpringApplicationBuilder(entryClass).web(webApplicationType).run(args);
    } catch (JsfException e) {
      LOG.error(e.formatMessage(), e);
    }
  }

  /**
   * 准备启动
   */
  public static void prepareRun(@NotNull String group) throws Exception {
    // 安装缺少的加密解密库
    BouncyCastleCryptoProvider.install();

    // 本机IP
    final String lanIp = IpUtils.resolveLanIp();

    // 读取默认配置
    metadata = new JsfMicroServiceMetadata();
    metadata.setGroup(group);
    metadata.getInstance().setLanIp(lanIp);
    metadata.getInstance().setWanIp(lanIp);
  }

  /**
   * 准备启动
   */
  public static void prepareTest() throws Exception {
    prepareRun("test");
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
  public static synchronized void register(JsfService service) {
    if (launched) {
      throw new IllegalStateException(
        MessageFormat.format("Can't register lifecycle service {0}: lifecycle system is launched",
          service.getJsfServiceName()));
    }
    boolean hasOne = SERVICES.contains(service) ||
      SERVICES.stream()
        .anyMatch(it -> it.getJsfServiceName().equalsIgnoreCase(service.getJsfServiceName()));
    if (hasOne) {
      throw new IllegalStateException(
        MessageFormat.format("Can't register lifecycle service {0}: already registered",
          service.getJsfServiceName()));
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
   *
   */
  static void doStage(@NotNull String service, @NotNull String stage, @NotNull DoStageCallable callable) throws Exception {
    long startTime = System.nanoTime();
    callable.call();
    long elapsedTime = System.nanoTime() - startTime;

    log(service, stage + "({0}ms)", TimeUnit.NANOSECONDS.toMillis(elapsedTime));
  }

  /**
   *
   */
  interface DoStageCallable {
    void call() throws Exception;
  }
}
