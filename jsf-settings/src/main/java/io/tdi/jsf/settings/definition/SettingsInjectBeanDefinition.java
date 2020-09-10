package io.tdi.jsf.settings.definition;

import io.tdi.jsf.core.utils.ReflectionUtils;
import io.tdi.jsf.core.utils.StringUtils;
import io.tdi.jsf.settings.annotation.SettingsInjectBean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created at 2020-09-10 14:24:53
 *
 * @author kidal
 * @since 0.3
 */
public class SettingsInjectBeanDefinition {
  @NotNull
  private final Field field;
  @Nullable
  private final String beanName;

  /**
   *
   */
  public SettingsInjectBeanDefinition(@NotNull Field field) {
    ReflectionUtils.makeAccessible(field);

    SettingsInjectBean settingsInjectBean = AnnotationUtils.findAnnotation(field, SettingsInjectBean.class);

    this.field = field;
    this.beanName = settingsInjectBean != null ? settingsInjectBean.beanName() : null;
  }

  /**
   *
   */
  @Override
  public String toString() {
    return "SettingsInjectBeanDefinition{" +
      "field=" + field +
      ", beanName='" + beanName + '\'' +
      '}';
  }

  /**
   *
   */
  public Object getBean(@NotNull ApplicationContext applicationContext) {
    if (StringUtils.isBlank(beanName)) {
      return applicationContext.getBean(field.getType());
    } else {
      return applicationContext.getBean(beanName);
    }
  }

  /**
   *
   */
  public boolean isStatic() {
    return Modifier.isStatic(field.getModifiers());
  }

  /**
   *
   */
  @NotNull
  public Field getField() {
    return field;
  }

  /**
   *
   */
  @Nullable
  public String getBeanName() {
    return beanName;
  }
}
