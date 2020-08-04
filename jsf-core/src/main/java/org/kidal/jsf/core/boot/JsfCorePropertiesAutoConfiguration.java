package org.kidal.jsf.core.boot;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.kidal.jsf.core.converter.StringToDateConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created at 2020-08-04 22:56:26
 *
 * @author kidal
 * @since 0.1.0
 */
@Configuration
@EnableConfigurationProperties(JsfCoreProperties.class)
public class JsfCorePropertiesAutoConfiguration {
  /**
   * 参数
   */
  private final JsfCoreProperties properties;

  /**
   *
   */
  public JsfCorePropertiesAutoConfiguration(JsfCoreProperties properties) {
    this.properties = properties;
  }

  /**
   * ConversionService
   */
  @Primary
  @Bean(JsfCoreProperties.B_CONVERSION_SERVICE)
  public ConversionServiceFactoryBean conversionServiceFactoryBean() {
    final ConversionServiceFactoryBean conversionServiceFactoryBean = new ConversionServiceFactoryBean();

    conversionServiceFactoryBean.setConverters(
      Sets.newHashSet(
        new StringToDateConverter()
      )
    );

    return conversionServiceFactoryBean;
  }

  /**
   * ThreadPoolTaskExecutor
   */
  @Primary
  @Bean(JsfCoreProperties.B_THREAD_POOL_TASK_EXECUTOR)
  @ConditionalOnProperty(value = JsfCoreProperties.P_CONCURRENT_ENABLED, havingValue = "true", matchIfMissing = true)
  public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
    //
    JsfCoreProperties.Concurrent props = properties.getConcurrent();

    // 通过阻塞因子计算线程数
    final double blockFactor = props.getBlockageFactor();
    final double processors = Runtime.getRuntime().availableProcessors();
    final int threadCount = (int) (processors / (1.0 - blockFactor));

    // 默认参数
    final int corePoolSize = Math.max(props.getCorePoolSize(), 0);
    final int keepAliveSeconds = props.getKeepAliveSeconds() > 0 ? props.getKeepAliveSeconds() : 60;
    final int maxPoolSize = props.getMaxPoolSize() > 0 ? props.getMaxPoolSize() : threadCount;
    final int queueCapacity = Math.max(props.getQueueCapacity(), 0);

    // new
    ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
    threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
    threadPoolTaskExecutor.setKeepAliveSeconds(keepAliveSeconds);
    threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
    threadPoolTaskExecutor.setQueueCapacity(queueCapacity);
    threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

    // log
//    Lifecycle.log("Commons", "Concurrent -> ThreadPoolTaskExecutor(corePoolSize={0}, keepAliveSeconds={1}, maxPoolSize={2}, queueCapacity={3})",
//      corePoolSize, keepAliveSeconds, maxPoolSize, queueCapacity);

    // done
    return threadPoolTaskExecutor;
  }

  /**
   * ListeningExecutorService
   */
  @Lazy
  @Bean(JsfCoreProperties.B_LISTENING_EXECUTOR_SERVICE)
  @ConditionalOnProperty(value = JsfCoreProperties.P_CONCURRENT_ENABLED, havingValue = "true", matchIfMissing = true)
  public ListeningExecutorService listeningExecutorService(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
    return MoreExecutors.listeningDecorator(threadPoolTaskExecutor.getThreadPoolExecutor());
  }
}
