package com.openjdl.jsf.graphql;

import com.openjdl.jsf.core.JsfService;
import com.openjdl.jsf.graphql.query.GraphqlQueryArgs;
import com.openjdl.jsf.graphql.query.GraphqlQueryResults;
import org.jetbrains.annotations.NotNull;

/**
 * Created at 2020-08-05 21:12:06
 *
 * @author kidal
 * @since 0.1.0
 */
public interface GraphqlService extends JsfService {
  /**
   * 运行GraphQL查询.
   *
   * @param args 查询参数
   * @return 查询结果
   */
  @NotNull
  GraphqlQueryResults query(@NotNull GraphqlQueryArgs args);
}
