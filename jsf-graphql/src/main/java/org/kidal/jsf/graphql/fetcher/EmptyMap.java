package org.kidal.jsf.graphql.fetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

/**
 * Created at 2020-08-05 17:33:13
 *
 * @author kidal
 * @since 0.1.0
 */
public class EmptyMap implements DataFetcher<Map<?, ?>> {
  /**
   *
   */
  public static final EmptyMap singleton = new EmptyMap();

  /**
   *
   */
  public static class Directive implements SchemaDirectiveWiring {
    /**
     *
     */
    @Override
    public GraphQLFieldDefinition onField(@NotNull SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment) {
      return environment.getElement().transform(it -> it.dataFetcher(singleton));
    }
  }

  /**
   *
   */
  @Override
  public Map<?, ?> get(DataFetchingEnvironment environment) throws Exception {
    return Collections.emptyMap();
  }
}
