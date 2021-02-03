package com.openjdl.jsf.jdbc.mybatis.boot;

import com.openjdl.jsf.core.utils.StringUtils;
import com.openjdl.jsf.jdbc.boot.JsfJdbcProperties;
import com.openjdl.jsf.jdbc.boot.JsfJdbcPropertiesAutoConfiguration;
import com.openjdl.jsf.jdbc.mybatis.JdbcMybatisService;
import com.openjdl.jsf.jdbc.mybatis.JdbcMybatisServiceImpl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.jetbrains.annotations.NotNull;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ResourceUtils;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created at 2020-08-06 21:45:07
 *
 * @author kidal
 * @since 0.1.0
 */

@Configuration
@EnableConfigurationProperties(JsfJdbcMybatisProperties.class)
@ConditionalOnProperty(value = JsfJdbcMybatisProperties.P_ENABLED, havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter(JsfJdbcPropertiesAutoConfiguration.class)
public class JsfJdbcMybatisPropertiesAutoConfiguration {
  /**
   *
   */
  @NotNull
  private final JsfJdbcMybatisProperties properties;

  /**
   *
   */
  public JsfJdbcMybatisPropertiesAutoConfiguration(@NotNull JsfJdbcMybatisProperties properties) {
    this.properties = properties;
  }

  /**
   *
   */
  @Primary
  @Bean(JsfJdbcMybatisProperties.B_JDBC_MYBATIS_SERVICE)
  public JdbcMybatisService jdbcMybatisService() {
    return new JdbcMybatisServiceImpl();
  }


  /**
   *
   */
  @Primary
  @Bean(JsfJdbcMybatisProperties.B_SQL_SESSION_FACTORY)
  public SqlSessionFactory sqlSessionFactory(
    @Qualifier(JsfJdbcProperties.B_ROUTING_DATASOURCE)
      DataSource dataSource
  ) throws Exception {
    JsfJdbcMybatisProperties.SessionFactoryProperties sessionFactory = properties.getSessionFactory();

    //
    SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
    sqlSessionFactoryBean.setDataSource(dataSource);

    //
    if (!sessionFactory.getMapperLocations().isEmpty()) {
      List<Resource> resources = new ArrayList<>();
      PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

      for (String location : sessionFactory.getMapperLocations()) {
        Collections.addAll(resources, resolver.getResources(location));
      }

      sqlSessionFactoryBean.setMapperLocations(resources.toArray(new Resource[0]));
    }

    //
    if (StringUtils.isNotBlank(sessionFactory.getConfigLocation())) {
      URL url = ResourceUtils.getURL(sessionFactory.getConfigLocation());
      byte[] buf = new byte[1024 * 8];
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      InputStream inputStream = url.openStream();
      int read;
      while ((read = inputStream.read(buf)) != -1) {
        byteArrayOutputStream.write(buf, 0, read);
      }
      sqlSessionFactoryBean.setConfigLocation(new ByteArrayResource(byteArrayOutputStream.toByteArray()));
    }

    return sqlSessionFactoryBean.getObject();
  }
}
