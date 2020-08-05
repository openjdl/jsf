package org.kidal.jsf.graphql;

import org.jetbrains.annotations.NotNull;
import org.kidal.jsf.core.JsfService;

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
