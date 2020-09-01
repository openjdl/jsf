package io.tdi.jsf.core.cache;

import com.google.common.base.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * Created at 2020-08-18 20:48:50
 *
 * @author kidal
 * @since 0.1.0
 */
public class CacheComputedValueKey {
  /**
   * 计算值名称
   */
  @NotNull
  private final String name;

  /**
   * 计算值键
   */
  @NotNull
  private final Object key;

  /**
   *
   */
  public CacheComputedValueKey(@NotNull String name, @NotNull Object key) {
    this.name = name;
    this.key = key;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CacheComputedValueKey that = (CacheComputedValueKey) o;
    return Objects.equal(name, that.name) && Objects.equal(key, that.key);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(name, key);
  }

  @Override
  public String toString() {
    return "CacheComputedValueKey{" +
      "name='" + name + '\'' +
      ", key=" + key +
      '}';
  }

  @NotNull
  public String getName() {
    return name;
  }

  @NotNull
  public Object getKey() {
    return key;
  }
}
