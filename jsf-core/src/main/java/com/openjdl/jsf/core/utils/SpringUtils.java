package com.openjdl.jsf.core.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created at 2020-08-04 17:30:00
 *
 * @author kidal
 * @since 0.1.0
 */
public class SpringUtils implements ApplicationContextAware, EnvironmentAware {
  /**
   * 用于开发者开发的环境
   */
  public static final String PROFILE_DEV = "dev";

  /**
   * 发布预览环境
   */
  public static final String PROFILE_RC = "rc";

  /**
   * 生产环境
   */
  public static final String PROFILE_PROD = "prod";

  /**
   * 应用上下文
   */
  private GenericApplicationContext applicationContext;

  /**
   * 应用环境
   */
  private Environment environment;

  /**
   * 获取环境前缀
   */
  public String getProfilePrefix() {
    return StringUtils.join(getAllProfiles(), ".");
  }

  /**
   * 获取全部环境设置
   */
  public String[] getAllProfiles() {
    // 优先排序
    final String[] firstPriorityProfiles = new String[]{PROFILE_DEV, PROFILE_RC, PROFILE_PROD};

    // 排序并返回
    return Arrays.stream(environment.getActiveProfiles())
      .sorted((a, b) -> {
        int iFppA = ArrayUtils.indexOf(firstPriorityProfiles, a);
        boolean bFppA = iFppA != ArrayUtils.INDEX_NOT_FOUND;
        int iFppB = ArrayUtils.indexOf(firstPriorityProfiles, b);
        boolean bFppB = iFppB != ArrayUtils.INDEX_NOT_FOUND;

        if (bFppA && bFppB) {
          return iFppA - iFppB;
        } else if (bFppA) {
          return -1;
        } else if (bFppB) {
          return 1;
        } else {
          return a.compareTo(b);
        }
      })
      .toArray(String[]::new);
  }

  /**
   * 检测其中一个环境是否匹配.
   */
  public boolean hasAnyActiveProfile(String... profiles) {
    for (String profile : environment.getActiveProfiles()) {
      if (ArrayUtils.contains(profiles, profile)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 检测全部环境是否匹配.
   */
  public boolean hasAllActiveProfile(String... profiles) {
    for (String profile : profiles) {
      if (!ArrayUtils.contains(environment.getActiveProfiles(), profile)) {
        return false;
      }
    }
    return true;
  }

  /**
   *
   */
  public <T> T unwrapProxy(T bean) {
    if (AopUtils.isAopProxy(bean) && bean instanceof Advised) {
      try {
        //noinspection unchecked
        bean = (T) ((Advised) bean).getTargetSource().getTarget();
      } catch (Exception e) {
        throw new IllegalStateException("Unwrap proxy for bean " + bean.toString() + " failed", e);
      }
    }
    return bean;
  }

  /**
   * 获取全部豆子.
   *
   * @param unwrapProxy 是否去壳.
   */
  public List<Object> getAllBeans(boolean unwrapProxy) {
    return Arrays.stream(applicationContext.getBeanDefinitionNames())
      .map((beanDefinitionName) -> {
        Object bean = applicationContext.getBean(beanDefinitionName);
        bean = unwrapProxy ? unwrapProxy(bean) : bean;
        return bean;
      })
      .collect(Collectors.toList());
  }

  /**
   * 获取全部豆子.
   */
  public List<Pair<Object, Object>> getAllUnwrappedBeanPairs() {
    return Arrays.stream(applicationContext.getBeanDefinitionNames())
      .map((beanDefinitionName) -> {
        Object bean = applicationContext.getBean(beanDefinitionName);
        Object unwrappedBean = unwrapProxy(bean);
        return Pair.of(bean, unwrappedBean);
      })
      .collect(Collectors.toList());
  }

  /**
   *
   */
  public GenericApplicationContext getApplicationContext() {
    return applicationContext;
  }

  /**
   *
   */
  public <T> T bean(Class<T> requiredType) {
    return getApplicationContext().getBean(requiredType);
  }

  /**
   *
   */
  public <T> T beanOrNull(Class<T> type) {
    try {
      return bean(type);
    } catch (NoSuchBeanDefinitionException e) {
      return null;
    }
  }

  /**
   *
   */
  @Override
  public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
    if (applicationContext instanceof GenericApplicationContext) {
      this.applicationContext = (GenericApplicationContext) applicationContext;
    }
  }

  /**
   *
   */
  public Environment getEnvironment() {
    return environment;
  }

  /**
   *
   */
  @Override
  public void setEnvironment(@NotNull Environment environment) {
    this.environment = environment;
  }
}
