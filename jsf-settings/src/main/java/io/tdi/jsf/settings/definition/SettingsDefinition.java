package io.tdi.jsf.settings.definition;

import io.tdi.jsf.core.utils.StringUtils;
import io.tdi.jsf.settings.SettingsValidating;
import io.tdi.jsf.settings.annotation.SettingsInjectBean;
import io.tdi.jsf.settings.annotation.Settings;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.ReflectionUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created at 2020-09-10 14:24:15
 *
 * @author kidal
 * @since 0.3
 */
public class SettingsDefinition {
  /**
   *
   */
  @NotNull
  public static SettingsDefinition parse(@NotNull Class<?> settingsClass, @NotNull Settings settings) {
    return new SettingsDefinition(settingsClass, settings.id());
  }

  @NotNull
  private final Class<?> type;

  @NotNull
  private final String id;

  @NotNull
  private final Set<SettingsInjectBeanDefinition> injectBeanDefinitions;

  /**
   *
   */
  public SettingsDefinition(@NotNull Class<?> type, @NotNull String id) {
    this.type = type;
    this.id = resolveId(type, id);
    this.injectBeanDefinitions = resolveInjectBeanDefinitions(type);
  }

  /**
   *
   */
  @Override
  public String toString() {
    return "SettingsDefinition{" +
      "settingsClass=" + type +
      ", id='" + id + '\'' +
      ", injectBeanDefinitions=" + injectBeanDefinitions +
      '}';
  }

  /**
   *
   */
  public boolean canValidate() {
    return SettingsValidating.class.isAssignableFrom(type);
  }

  /**
   *
   */
  @NotNull
  private String resolveId(@NotNull Class<?> settingsClass, @NotNull String id) {
    return StringUtils.isBlank(id)
      ? StringUtils.uncapitalize(settingsClass.getSimpleName())
      : id;
  }

  /**
   *
   */
  @NotNull
  private Set<SettingsInjectBeanDefinition> resolveInjectBeanDefinitions(@NotNull Class<?> settingsClass) {
    Set<SettingsInjectBeanDefinition> injectBeanDefinitions = new HashSet<>();
    ReflectionUtils.doWithFields(
      settingsClass,
      field -> injectBeanDefinitions.add(new SettingsInjectBeanDefinition(field)),
      field -> field.isAnnotationPresent(SettingsInjectBean.class)
    );
    return Collections.unmodifiableSet(injectBeanDefinitions);
  }

  /**
   *
   */
  @NotNull
  public Class<?> getType() {
    return type;
  }

  /**
   *
   */
  @NotNull
  public String getId() {
    return id;
  }

  /**
   *
   */
  @NotNull
  public Set<SettingsInjectBeanDefinition> getInjectBeanDefinitions() {
    return injectBeanDefinitions;
  }
}
