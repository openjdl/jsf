package org.kidal.jsf.graphql.fetcher;

import graphql.schema.*;
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
  public static final EmptyMap INSTANCE = new EmptyMap();

  /**
   *
   */
  public static class Directive implements SchemaDirectiveWiring {
    /**
     *
     */
    public static final String NAME = "map";

    /**
     *
     */
    @Override
    public GraphQLFieldDefinition onField(@NotNull SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment) {
      GraphQLFieldDefinition field = environment.getElement();
      GraphQLFieldsContainer parentType = environment.getFieldsContainer();

      DataFetcher<?> originalFetcher = environment.getCodeRegistry().getDataFetcher(parentType, field);

      // 使用新fetcher
      FieldCoordinates coordinates = FieldCoordinates.coordinates(parentType, field);
      environment.getCodeRegistry().dataFetcher(coordinates, INSTANCE);

      return field;
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
