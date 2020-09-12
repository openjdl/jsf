package com.openjdl.jsf.graphql.query;

import org.jetbrains.annotations.NotNull;
import org.springframework.lang.Nullable;

import java.time.Duration;

/**
 * Created at 2020-08-05 17:13:19
 *
 * @author kidal
 * @since 0.1.0
 */
public class GraphqlCookie {
  /**
   *
   */
  @NotNull
  private final String name;

  /**
   *
   */
  @NotNull
  private final String value;

  /**
   *
   */
  @NotNull
  private final Duration maxAge;

  /**
   *
   */
  @Nullable
  private final String domain;

  /**
   *
   */
  @Nullable
  private final String path;

  /**
   *
   */
  private final boolean secure;

  /**
   *
   */
  private final boolean httpOnly;

  /**
   *
   */
  @Nullable
  private final String sameSite;

  /**
   *
   */
  public GraphqlCookie(@NotNull String name,
                       @NotNull String value,
                       @NotNull Duration maxAge,
                       @Nullable String domain,
                       @Nullable String path,
                       boolean secure,
                       boolean httpOnly,
                       @Nullable String sameSite) {
    this.name = name;
    this.value = value;
    this.maxAge = maxAge;
    this.domain = domain;
    this.path = path;
    this.secure = secure;
    this.httpOnly = httpOnly;
    this.sameSite = sameSite;
  }

  /**
   *
   */
  @NotNull
  public String getName() {
    return name;
  }

  /**
   *
   */
  @NotNull
  public String getValue() {
    return value;
  }

  /**
   *
   */
  @NotNull
  public Duration getMaxAge() {
    return maxAge;
  }

  /**
   *
   */
  @Nullable
  public String getDomain() {
    return domain;
  }

  /**
   *
   */
  @Nullable
  public String getPath() {
    return path;
  }

  /**
   *
   */
  public boolean isSecure() {
    return secure;
  }

  /**
   *
   */
  public boolean isHttpOnly() {
    return httpOnly;
  }

  /**
   *
   */
  @Nullable
  public String getSameSite() {
    return sameSite;
  }
}
