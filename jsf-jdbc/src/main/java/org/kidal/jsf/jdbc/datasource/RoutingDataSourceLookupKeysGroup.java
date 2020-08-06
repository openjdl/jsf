package org.kidal.jsf.jdbc.datasource;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created at 2020-08-06 17:26:09
 *
 * @author kidal
 * @since 0.1.0
 */
public class RoutingDataSourceLookupKeysGroup {
  /**
   *
   */
  private final List<String> masterKeys = Lists.newArrayList();

  /**
   *
   */
  private final List<String> slaveKeys = Lists.newArrayList();

  /**
   *
   */
  public RoutingDataSourceLookupKeysGroup() {

  }

  /**
   *
   */
  public List<String> getMasterKeys() {
    return masterKeys;
  }

  /**
   *
   */
  public List<String> getSlaveKeys() {
    return slaveKeys;
  }
}
