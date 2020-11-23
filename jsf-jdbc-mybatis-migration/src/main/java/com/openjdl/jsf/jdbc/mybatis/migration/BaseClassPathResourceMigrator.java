package com.openjdl.jsf.jdbc.mybatis.migration;

import com.openjdl.jsf.core.utils.IOUtils;
import com.openjdl.jsf.core.utils.StringUtils;
import com.openjdl.jsf.jdbc.mybatis.migration.data.mapper.MigratorMapper;
import com.openjdl.jsf.jdbc.mybatis.utils.SqlAdapter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created at 2020-10-13 15:35:19
 *
 * @author kidal
 * @since 0.4
 */
public abstract class BaseClassPathResourceMigrator implements BaseMigrator {
  @Nullable
  private final String group;

  @NotNull
  private final String location;

  @NotNull
  private final String multiSqlSeparator;

  /**
   *
   */
  protected BaseClassPathResourceMigrator() {
    this(null, null);
  }

  /**
   *
   */
  protected BaseClassPathResourceMigrator(@Nullable String group, @Nullable String location) {
    this(group, location, null);
  }

  /**
   *
   */
  protected BaseClassPathResourceMigrator(@Nullable String group, @Nullable String location, @Nullable String multiSqlSeparator) {
    this.group = group;

    if (StringUtils.isBlank(location)) {
      this.location = String.format("migration/%s.sql", getClass().getSimpleName());
    } else {
      this.location = location;
    }

    if (StringUtils.isBlank(multiSqlSeparator)) {
      this.multiSqlSeparator = "\n\n";
    } else {
      this.multiSqlSeparator = multiSqlSeparator;
    }
  }

  /**
   *
   */
  @NotNull
  protected List<String> processScripts(@NotNull List<String> scripts) {
    return scripts;
  }

  /**
   *
   */
  @NotNull
  protected String loadSql(@NotNull String location) {
    try (InputStream is = new ClassPathResource(location).getInputStream()) {
      return IOUtils.readAllText(is);
    } catch (IOException e) {
      ExceptionUtils.rethrow(e);
      return "";
    }
  }

  /**
   *
   */
  @Nullable
  @Override
  public String getDataSourceGroupName() {
    return group;
  }

  /**
   *
   */
  @Override
  public void up(@NotNull MigratorMapper migratorMapper) {
    String originalSql = loadSql(this.location);
    String fixedSql = originalSql.replaceAll("\r\n", "\n");
    String[] sqlArr = fixedSql.split(this.multiSqlSeparator);

    for (String sql : sqlArr) {
      if (StringUtils.isNotBlank(sql)) {
        migratorMapper.update(SqlAdapter.of(sql));
      }
    }
  }
}
