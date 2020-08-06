package org.kidal.jsf.jdbc.boot;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created at 2020-08-06 17:40:33
 *
 * @author kidal
 * @since 0.1.0
 */
@ConfigurationProperties(JsfJdbcProperties.P_PATH)
public class JsfJdbcProperties {
  public static final String P_PATH = "jsf.jdbc";
  public static final String B_PATH = "jsf-jdbc";

  public static final String P_ENABLED = P_PATH + ".enabled";
  public static final String P_ASPECT_ENABLED = P_PATH + ".aspect.enabled";
  public static final String P_TRANSACTION_ENABLED = P_PATH + ".transaction.enabled";

  public static final String B_JDBC_SERVICE = "-JdbcService";
  public static final String B_DATASOURCE_PREFIX = B_PATH + "-Datasource";
  public static final String B_ROUTING_DATASOURCE = B_PATH + "-RoutingDatasource";
  public static final String B_ROUTING_DATASOURCE_ASPECT = B_PATH + "-RoutingDatasourceAspect";
  public static final String B_TRANSACTION_MANAGER = B_PATH + "-TransactionManager";

  /**
   * 创建数据源豆子名
   */
  public static String makeDataSourceBeanName(String name) {
    return String.format("%s-%s",
      JsfJdbcProperties.B_DATASOURCE_PREFIX,
      name.replace("-", "_").replace(".", "_").replace(" ", "_").toLowerCase()
    );
  }

  //--------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------

  private boolean enabled;
  private Aspect aspect = new Aspect();
  private Transaction transaction = new Transaction();
  private DataSourceProperties defaultDataSource = new DataSourceProperties(true);
  private List<DataSourceProperties> dataSources = new ArrayList<>();
  private RoutingDataSourceProperties routingDataSource = new RoutingDataSourceProperties();

  //--------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------

  public static class Aspect {
    private boolean enabled;
    private int order;

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public int getOrder() {
      return order;
    }

    public void setOrder(int order) {
      this.order = order;
    }
  }

  public static class Transaction {
    private boolean enabled;

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }
  }

  public static class DataSourceProperties {
    private String name = "default";
    private String dataSourceClassName = "org.apache.commons.dbcp2.BasicDataSource";
    private Map<String, Object> propertyValues = new LinkedHashMap<>();

    public DataSourceProperties() {

    }

    public DataSourceProperties(boolean withDefaults) {
      if (withDefaults) {
        this.propertyValues.put("username", "root");
        this.propertyValues.put("password", "root");
        this.propertyValues.put("url", "jdbc:mysql://localhost:3306/mcg_aio?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false");
        this.propertyValues.put("driverClassName", "com.mysql.cj.jdbc.Driver");
        this.propertyValues.put("defaultAutoCommit", true);
        this.propertyValues.put("defaultReadOnly", false);
        this.propertyValues.put("initialSize", 0);
        this.propertyValues.put("maxTotal", 2);
        this.propertyValues.put("maxIdle", 2);
        this.propertyValues.put("minIdle", 0);
        this.propertyValues.put("maxWaitMillis", 60000);
        this.propertyValues.put("validationQuery", "SELECT 1");
        this.propertyValues.put("testOnBorrow", true);
        this.propertyValues.put("connectionInitSqls", "SET NAMES utf8mb4");
      }
    }

    public DataSourceProperties complete(DataSourceProperties other) {
      DataSourceProperties merged = new DataSourceProperties();
      merged.setName(peekNotNull(getName(), other.getName()));
      merged.setDataSourceClassName(peekNotNull(getDataSourceClassName(), other.getDataSourceClassName()));
      merged.getPropertyValues().putAll(other.getPropertyValues());
      merged.getPropertyValues().putAll(getPropertyValues());
      return merged;
    }

    private <T> T peekNotNull(T a, T b) {
      return (a != null) ? a : b;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getDataSourceClassName() {
      return dataSourceClassName;
    }

    public void setDataSourceClassName(String dataSourceClassName) {
      this.dataSourceClassName = dataSourceClassName;
    }

    public Map<String, Object> getPropertyValues() {
      return propertyValues;
    }

    public void setPropertyValues(Map<String, Object> propertyValues) {
      this.propertyValues = propertyValues;
    }
  }

  public static class RoutingDataSourceProperties {
    private String defaultGroupName = "default";
    private boolean defaultReadOnly = true;
    private List<GroupProperties> groups = new ArrayList<>();

    public String getDefaultGroupName() {
      return defaultGroupName;
    }

    public void setDefaultGroupName(String defaultGroupName) {
      this.defaultGroupName = defaultGroupName;
    }

    public boolean isDefaultReadOnly() {
      return defaultReadOnly;
    }

    public void setDefaultReadOnly(boolean defaultReadOnly) {
      this.defaultReadOnly = defaultReadOnly;
    }

    public List<GroupProperties> getGroups() {
      return groups;
    }

    public void setGroups(List<GroupProperties> groups) {
      this.groups = groups;
    }

    public static class GroupProperties {
      private String name;
      private List<String> masters = new ArrayList<>();
      private List<String> slaves = new ArrayList<>();

      public String getName() {
        return name;
      }

      public void setName(String name) {
        this.name = name;
      }

      public List<String> getMasters() {
        return masters;
      }

      public void setMasters(List<String> masters) {
        this.masters = masters;
      }

      public List<String> getSlaves() {
        return slaves;
      }

      public void setSlaves(List<String> slaves) {
        this.slaves = slaves;
      }
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

  public Aspect getAspect() {
    return aspect;
  }

  public void setAspect(Aspect aspect) {
    this.aspect = aspect;
  }

  public Transaction getTransaction() {
    return transaction;
  }

  public void setTransaction(Transaction transaction) {
    this.transaction = transaction;
  }

  public DataSourceProperties getDefaultDataSource() {
    return defaultDataSource;
  }

  public void setDefaultDataSource(DataSourceProperties defaultDataSource) {
    this.defaultDataSource = defaultDataSource;
  }

  public List<DataSourceProperties> getDataSources() {
    return dataSources;
  }

  public void setDataSources(List<DataSourceProperties> dataSources) {
    this.dataSources = dataSources;
  }

  public RoutingDataSourceProperties getRoutingDataSource() {
    return routingDataSource;
  }

  public void setRoutingDataSource(RoutingDataSourceProperties routingDataSource) {
    this.routingDataSource = routingDataSource;
  }
}
