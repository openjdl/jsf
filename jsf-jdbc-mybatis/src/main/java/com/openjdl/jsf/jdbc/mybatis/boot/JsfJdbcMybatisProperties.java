package com.openjdl.jsf.jdbc.mybatis.boot;

import com.google.common.collect.Lists;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Created at 2020-08-06 21:41:02
 *
 * @author kidal
 * @since 0.1.0
 */
@ConfigurationProperties(JsfJdbcMybatisProperties.B_PATH)
public class JsfJdbcMybatisProperties {
  public static final String P_PATH = "jsf.jdbc.mybatis";
  public static final String B_PATH = "jsf-jdbc-mybatis";

  public static final String P_ENABLED = P_PATH + ".enabled";
  public static final String B_JDBC_MYBATIS_SERVICE = B_PATH + "-JdbcMybatisService";
  public static final String B_SQL_SESSION_FACTORY = B_PATH + "-SqlSessionFactory";

  //--------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------

  private boolean enabled;
  private SessionFactoryProperties sessionFactory = new SessionFactoryProperties();

  //--------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------

  public static class SessionFactoryProperties {
    private List<String> mapperLocations = Lists.newArrayList(
      "classpath*:**/*Mapper.xml"
    );
    private String configLocation;

    public List<String> getMapperLocations() {
      return mapperLocations;
    }

    public void setMapperLocations(List<String> mapperLocations) {
      this.mapperLocations = mapperLocations;
    }

    public String getConfigLocation() {
      return configLocation;
    }

    public void setConfigLocation(String configLocation) {
      this.configLocation = configLocation;
    }
  }

  //--------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public SessionFactoryProperties getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactoryProperties sessionFactory) {
    this.sessionFactory = sessionFactory;
  }
}
