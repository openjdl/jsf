package io.tdi.jsf.settings.boot;

import io.tdi.jsf.core.utils.SpringUtils;
import io.tdi.jsf.settings.SettingsService;
import io.tdi.jsf.settings.SettingsServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.scheduling.TaskScheduler;

/**
 * Created at 2020-09-10 16:42:47
 *
 * @author kidal
 * @since 0.3
 */
@Configuration
@EnableConfigurationProperties(JsfSettingsProperties.class)
@ConditionalOnProperty(value = JsfSettingsProperties.P_ENABLED, havingValue = "true", matchIfMissing = true)
public class JsfSettingsPropertiesAutoConfiguration {
  private final JsfSettingsProperties properties;

  /**
   *
   */
  public JsfSettingsPropertiesAutoConfiguration(JsfSettingsProperties properties) {
    this.properties = properties;
  }

  /**
   *
   */
  @Bean(JsfSettingsProperties.B_SETTINGS_SERVICE)
  public SettingsService settingsService(ConversionService conversionService,
                                         TaskScheduler taskScheduler,
                                         SpringUtils springUtils) {
    return new SettingsServiceImpl(properties, conversionService, taskScheduler, springUtils);
  }
}
