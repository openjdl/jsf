package io.tdi.jsf.settings;

import io.tdi.jsf.core.observe.TaggedObservable;
import io.tdi.jsf.settings.definition.SettingsDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * Created at 2020-09-10 14:55:24
 *
 * @author kidal
 * @since 0.3
 */
public interface SettingsStorage<K, V> extends TaggedObservable {
  /**
   *
   */
  @NotNull
  SettingsDefinition getDefinition();

  /**
   *
   */
  @NotNull
  SettingsMetadata getMetadata();

  /**
   *
   */
  void refresh();

  /**
   *
   */
  @Nullable
  V get(@NotNull K key);

  /**
   *
   */
  boolean containsKey(@NotNull K key);

  /**
   *
   */
  @NotNull
  Collection<V> values();

  /**
   *
   */
  @Nullable
  V getUniqueIndexedValue(@NotNull String indexName, @NotNull Object indexKey);

  /**
   *
   */
  @NotNull
  List<V> getIndexedValues(@NotNull String indexName, @NotNull Object indexKey);
}
