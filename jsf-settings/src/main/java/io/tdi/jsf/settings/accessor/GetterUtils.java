package io.tdi.jsf.settings.accessor;

import io.tdi.jsf.core.utils.ReflectionUtils;
import io.tdi.jsf.settings.annotation.SettingsIndex;
import io.tdi.jsf.settings.annotation.SettingsKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created at 2020-09-10 15:26:57
 *
 * @author kidal
 * @since 0.3
 */
public class GetterUtils {
  /**
   *
   */
  @NotNull
  public static Getter createKeyGetter(@NotNull Class<?> type) {
    Method[] methods = ReflectionUtils.getMethodsArray(type, SettingsKey.class, false);
    if (methods.length > 0) {
      return new MethodGetter(methods[0]);
    }

    Field[] fields = ReflectionUtils.getFieldsArray(type, SettingsKey.class, false);
    if (fields.length > 0) {
      return new FieldGetter(fields[0]);
    }

    throw new IllegalStateException("无法找到Key字段，请确认是否正确注解SettingsKey: type=" + type.getName());
  }

  /**
   *
   */
  @NotNull
  public static Map<String, IndexGetter> createIndexGetters(@NotNull Class<?> type) {
    Map<String, IndexGetter> getterMap = new HashMap<>();

    Method[] methods = ReflectionUtils.getMethodsArray(type, SettingsIndex.class, false);
    for (Method method : methods) {
      SettingsIndex settingsIndex = Objects.requireNonNull(AnnotationUtils.findAnnotation(method, SettingsIndex.class));
      MethodIndexGetter getter = new MethodIndexGetter(
        method,
        settingsIndex.id().equals("") ? method.getName() : settingsIndex.id(),
        settingsIndex.unique(),
        createIndexComparator(settingsIndex.comparatorType())
      );
      getterMap.put(getter.getIndexName(), getter);
    }

    Field[] fields = ReflectionUtils.getFieldsArray(type, SettingsKey.class, false);
    for (Field field : fields) {
      SettingsIndex settingsIndex = Objects.requireNonNull(AnnotationUtils.findAnnotation(field, SettingsIndex.class));
      FieldIndexGetter getter = new FieldIndexGetter(
        field,
        settingsIndex.id().equals("") ? field.getName() : settingsIndex.id(),
        settingsIndex.unique(),
        createIndexComparator(settingsIndex.comparatorType())
      );
      getterMap.putIfAbsent(getter.getIndexName(), getter);
    }

    return getterMap;
  }

  /**
   * Create index comparator.
   */
  @SuppressWarnings("rawtypes")
  public static Comparator<?> createIndexComparator(Class<? extends Comparator> comparatorType) {
    if (comparatorType != null && comparatorType != Comparator.class) {
      try {
        return comparatorType.getDeclaredConstructor().newInstance();
      } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
        throw new IllegalStateException("实例化对比器 " + comparatorType.getName() + " 失败", e);
      }
    }
    return null;
  }

  /**
   *
   */
  private GetterUtils() {
    throw new IllegalStateException();
  }
}
