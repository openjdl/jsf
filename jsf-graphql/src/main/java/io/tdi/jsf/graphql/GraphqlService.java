package io.tdi.jsf.graphql;

import org.jetbrains.annotations.NotNull;
import io.tdi.jsf.core.JsfService;
import io.tdi.jsf.graphql.query.GraphqlQueryArgs;
import io.tdi.jsf.graphql.query.GraphqlQueryResults;

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
