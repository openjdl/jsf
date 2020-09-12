package com.openjdl.jsf.settings.accessor;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Comparator;

/**
 * Created at 2020-09-10 15:55:45
 *
 * @author kidal
 * @since 0.3
 */
public class MethodIndexGetter extends MethodGetter implements IndexGetter {
  private final String indexName;
  private final boolean unique;
  private final Comparator<?> comparator;

  /**
   *
   */
  public MethodIndexGetter(@NotNull Method method, String indexName, boolean unique, Comparator<?> comparator) {
    super(method);

    this.indexName = indexName;
    this.unique = unique;
    this.comparator = comparator;
  }

  /**
   *
   */
  @NotNull
  @Override
  public String getIndexName() {
    return indexName;
  }

  /**
   *
   */
  @Override
  public boolean isUnique() {
    return unique;
  }

  /**
   *
   */
  @Override
  public Comparator<?> getComparator() {
    return comparator;
  }
}
