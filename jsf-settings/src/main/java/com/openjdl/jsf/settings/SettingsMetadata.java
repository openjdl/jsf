package com.openjdl.jsf.settings;

import com.openjdl.jsf.core.utils.DateUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

/**
 * Created at 2020-09-10 14:35:22
 *
 * @author kidal
 * @since 0.3
 */
public class SettingsMetadata {
  /**
   *
   */
  public static int INITIAL_VERSION = 0;

  /**
   *
   */
  @NotNull
  public static SettingsMetadata of(@NotNull String id) {
    return new SettingsMetadata(id, INITIAL_VERSION, new Date(DateUtils.LONG_BEFORE_TIME.getTime()));
  }

  @NotNull
  private String id;

  private int version;

  @NotNull
  private Date updatedAt;

  /**
   *
   */
  public SettingsMetadata(@NotNull String id, int version, @NotNull Date updatedAt) {
    this.id = id;
    this.version = version;
    this.updatedAt = updatedAt;
  }

  /**
   *
   */
  public boolean isUpToDate(@Nullable SettingsMetadata o) {
    return o != null && version >= o.version;
  }

  @NotNull
  public String getId() {
    return id;
  }

  public void setId(@NotNull String id) {
    this.id = id;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  @NotNull
  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(@NotNull Date updatedAt) {
    this.updatedAt = updatedAt;
  }
}
