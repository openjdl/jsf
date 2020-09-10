package io.tdi.jsf.settings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Created at 2020-09-10 12:29:39
 *
 * @author kidal
 * @since 0.3
 */
@SuppressWarnings("rawtypes")
public interface SettingsService {
  /**
   *
   */
  void refresh(String... ids);

  /**
   *
   */
  void refresh(Class... types);

  /**
   *
   */
  void refresh(SettingsStorage... targets);

  /**
   *
   */
  void refreshAll();

  /**
   *
   */
  @Nullable
  SettingsStorage getStorage(@NotNull String id);

  /**
   *
   */
  @Nullable
  SettingsStorage getStorage(@NotNull Class type);

  /**
   *
   */
  @Nullable
  SettingsStorage loadStorage(@NotNull String id);

  /**
   *
   */
  @Nullable
  SettingsStorage loadStorage(@NotNull Class type);

  /**
   *
   */
  @NotNull
  Collection<SettingsStorage> getAllStorage();
}
