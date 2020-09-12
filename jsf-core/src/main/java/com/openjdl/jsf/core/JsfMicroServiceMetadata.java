package com.openjdl.jsf.core;

import org.jetbrains.annotations.NotNull;

/**
 * Created at 2020-08-04 23:05:41
 *
 * @author kidal
 * @since 0.1.0
 */
public class JsfMicroServiceMetadata {
  /**
   *
   */
  private String group;

  /**
   *
   */
  private String name;

  /**
   *
   */
  private Instance instance = new Instance();

  /**
   *
   */
  public void merge(@NotNull JsfMicroServiceMetadata o) {
    group = mergeProp(group, o.group);

    instance = mergeProp(instance, o.instance);
    instance.uuid = mergeProp(instance.uuid, o.instance.uuid);
    instance.wanIp = mergeProp(instance.wanIp, o.instance.wanIp);
    instance.lanIp = mergeProp(instance.lanIp, o.instance.lanIp);
    instance.port = mergeProp(instance.port, o.instance.port);
  }

  /**
   *
   */
  private <T> T mergeProp(T a, T b) {
    return a != null ? a : b;
  }

  /**
   *
   */
  public static class Instance {
    /**
     *
     */
    private String uuid;

    /**
     *
     */
    private String wanIp;

    /**
     *
     */
    private String lanIp;

    /**
     *
     */
    private Integer port;

    /**
     *
     */
    public String getUuid() {
      return uuid;
    }

    /**
     *
     */
    public void setUuid(String uuid) {
      this.uuid = uuid;
    }

    /**
     *
     */
    public String getWanIp() {
      return wanIp;
    }

    /**
     *
     */
    public void setWanIp(String wanIp) {
      this.wanIp = wanIp;
    }

    /**
     *
     */
    public String getLanIp() {
      return lanIp;
    }

    /**
     *
     */
    public void setLanIp(String lanIp) {
      this.lanIp = lanIp;
    }

    /**
     *
     */
    public Integer getPort() {
      return port;
    }

    /**
     *
     */
    public void setPort(Integer port) {
      this.port = port;
    }
  }

  /**
   *
   */
  public String getGroup() {
    return group;
  }

  /**
   *
   */
  public void setGroup(String group) {
    this.group = group;
  }

  /**
   *
   */
  public String getName() {
    return name;
  }

  /**
   *
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   *
   */
  public Instance getInstance() {
    return instance;
  }

  /**
   *
   */
  public void setInstance(Instance instance) {
    this.instance = instance;
  }
}
