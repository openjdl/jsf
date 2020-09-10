package io.tdi.jsf.settings.accessor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Comparator;

/**
 * Created at 2020-09-10 15:30:44
 *
 * @author kidal
 * @since 3.0
 */
public class FieldIndexGetter extends FieldGetter implements IndexGetter {
  @NotNull
  private final String indexName;

  private final boolean unique;

  @Nullable
  private final Comparator<?> comparator;

  /**
   *
   */
  public FieldIndexGetter(Field field, @NotNull String indexName, boolean unique, @Nullable Comparator<?> comparator) {
    super(field);

    this.indexName = indexName;
    this.unique = unique;
    this.comparator = comparator;
  }

  /**
   *
   */
  @Override
  @NotNull
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
  @Nullable
  public Comparator<?> getComparator() {
    return comparator;
  }
}
