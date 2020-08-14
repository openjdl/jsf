package org.kidal.jsf.core.boot;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created at 2020-08-04 22:51:03
 *
 * @author kidal
 * @since 0.1.0
 */
@ConfigurationProperties(JsfCoreProperties.P_PATH)
public class JsfCoreProperties {
  /**
   *
   */
  public static final String P_PATH = "jsf.core";

  /**
   *
   */
  public static final String B_PATH = "jsf-core";

  /**
   *
   */
  public static final String P_CONCURRENT_ENABLED = P_PATH + ".concurrent.enabled";

  /**
   *
   */
  public static final String B_SPRING_UTILS = B_PATH + "-SpringUtils";

  /**
   * 转换服务
   */
  public static final String B_CONVERSION_SERVICE = B_PATH + "-ConversionService";

  /**
   *
   */
  public static final String B_THREAD_POOL_TASK_EXECUTOR = B_PATH + "-ThreadPoolTaskExecutor";

  /**
   *
   */
  public static final String B_LISTENING_EXECUTOR_SERVICE = B_PATH + "-listening-executor-service";

  /**
   *
   */
  private Concurrent concurrent = new Concurrent();

  /**
   * UIN密钥
   */
  private String uinSecretKey = "b9RUqhN4O2322MxGw";

  /**
   * 并发参数
   */
  public static class Concurrent {
    /**
     * Concurrent blockage factor
     */
    private double blockageFactor = 0.9;

    /**
     * Initial threads count
     * default: 1
     */
    private int corePoolSize = -1;

    /**
     * Idle thread before destroy seconds
     * default: 60
     */
    private int keepAliveSeconds = -1;

    /**
     * Maximum threads count
     * default: Integer.MAX_VALUE
     */
    private int maxPoolSize = 2;

    /**
     * Wait queue size
     * default: Integer.MAX_VALUE
     */
    private int queueCapacity = -1;

    /**
     *
     */
    public double getBlockageFactor() {
      return blockageFactor;
    }

    /**
     *
     */
    public void setBlockageFactor(double blockageFactor) {
      this.blockageFactor = blockageFactor;
    }

    /**
     *
     */
    public int getCorePoolSize() {
      return corePoolSize;
    }

    /**
     *
     */
    public void setCorePoolSize(int corePoolSize) {
      this.corePoolSize = corePoolSize;
    }

    /**
     *
     */
    public int getKeepAliveSeconds() {
      return keepAliveSeconds;
    }

    /**
     *
     */
    public void setKeepAliveSeconds(int keepAliveSeconds) {
      this.keepAliveSeconds = keepAliveSeconds;
    }

    /**
     *
     */
    public int getMaxPoolSize() {
      return maxPoolSize;
    }

    /**
     *
     */
    public void setMaxPoolSize(int maxPoolSize) {
      this.maxPoolSize = maxPoolSize;
    }

    /**
     *
     */
    public int getQueueCapacity() {
      return queueCapacity;
    }

    /**
     *
     */
    public void setQueueCapacity(int queueCapacity) {
      this.queueCapacity = queueCapacity;
    }
  }

  /**
   *
   */
  public Concurrent getConcurrent() {
    return concurrent;
  }

  /**
   *
   */
  public void setConcurrent(Concurrent concurrent) {
    this.concurrent = concurrent;
  }

  /**
   *
   */
  public String getUinSecretKey() {
    return uinSecretKey;
  }

  /**
   *
   */
  public void setUinSecretKey(String uinSecretKey) {
    this.uinSecretKey = uinSecretKey;
  }
}
