package com.openjdl.jsf.jdbc.mybatis.migration.data.mapper;

import com.openjdl.jsf.jdbc.mybatis.utils.SqlAdapter;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Created at 2020-10-11 19:15:29
 *
 * @author kidal
 * @since 0.4
 */
public interface MigratorMapper {
  /**
   * 创建
   */
  @Insert("${sql}")
  int insert(@NotNull SqlAdapter adapter);

  /**
   * 查询
   */
  @Select("${sql}")
  List<Map<String, Object>> select(SqlAdapter adapter);

  /**
   * 更新
   */
  @Update("${sql}")
  int update(@NotNull SqlAdapter adapter);

  /**
   * 删除
   */
  @Delete("${sql}")
  int delete(@NotNull SqlAdapter adapter);
}
