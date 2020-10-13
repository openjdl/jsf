package com.openjdl.jsf.demo;

import com.openjdl.jsf.core.JsfMicroService;
import com.openjdl.jsf.demo.boot.AppProperties;
import org.jetbrains.annotations.NotNull;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created at 2020-10-11 20:47:08
 *
 * @author kidal
 * @since 0.4
 */
@SpringBootApplication(
  scanBasePackages = {"com.openjdl.jsf"}
)
@MapperScan({
  "com.openjdl.jsf.jdbc.mybatis.migration.data.mapper",
  "com.openjdl.jsf.demo.data.mapper",
})
@EnableScheduling
@EnableConfigurationProperties(AppProperties.class)
public class Start {
  public static void main(@NotNull String[] args) throws Exception {
    JsfMicroService.run(Start.class, args);
  }
}
