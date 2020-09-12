package com.openjdl.jsf.graphql.fetcher;

import graphql.schema.DataFetcher;
import org.jetbrains.annotations.NotNull;

/**
 * Created at 2020-08-05 17:38:57
 *
 * @author kidal
 * @since 0.1.0
 */
public abstract class BaseUnitFetcherFactory {
  /**
   * 使用带单位的Fetcher来代理原始fetcher
   *
   * @param fetcher 原始fetcher
   * @return 代理后的fetcher
   */
  @NotNull
  public abstract DataFetcher<?> withUnitFetcher(@NotNull DataFetcher<?> fetcher);
}
