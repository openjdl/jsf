package com.openjdl.jsf.jdbc.mybatis.migration.data.definition;

import com.openjdl.jsf.jdbc.mybatis.migration.BaseMigrator;
import org.jetbrains.annotations.NotNull;

/**
 * Created at 2020-10-11 19:14:10
 *
 * @author kidal
 * @since 0.4
 */
public class MigratorDefinition {
  @NotNull
  private final String name;

  @NotNull
  private final String order;

  @NotNull
  private final BaseMigrator migrator;

  /**
   *
   */
  public MigratorDefinition(@NotNull String name,
                            @NotNull String order,
                            @NotNull BaseMigrator migrator) {
    this.name = name;
    this.order = order;
    this.migrator = migrator;
  }

  //--------------------------------------------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------------------------------------------

  @NotNull
  public String getName() {
    return name;
  }

  @NotNull
  public String getOrder() {
    return order;
  }

  @NotNull
  public BaseMigrator getMigrator() {
    return migrator;
  }
}
