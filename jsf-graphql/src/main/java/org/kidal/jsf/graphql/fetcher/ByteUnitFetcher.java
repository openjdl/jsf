package org.kidal.jsf.graphql.fetcher;

import graphql.Scalars;
import graphql.schema.*;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kidal.jsf.graphql.query.GraphqlFetchingEnvironment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * Created at 2020-08-05 17:40:48
 *
 * @author kidal
 * @since 0.1.0
 */
public class ByteUnitFetcher extends BaseGraphqlDataFetcher<Object> {
  static {
    UnitFetcherFactoryStaticRegistry.register(new Factory());
  }

  /**
   * 原始fetcher
   */
  @Nullable
  private final DataFetcher<?> fetcher;

  /**
   *
   */
  @Nullable
  private final String fieldName;

  /**
   *
   */
  public ByteUnitFetcher(@Nullable DataFetcher<?> fetcher, @Nullable String fieldName) {
    this.fetcher = fetcher;
    this.fieldName = fieldName;
  }

  /**
   *
   */
  @Override
  @Nullable
  public Object fetch(@NotNull GraphqlFetchingEnvironment env) throws Exception {
    Object valueObject = null;
    if (fieldName != null) {
      Object sourceObject = env.getEnvironment().getSource();
      if (sourceObject instanceof Map) {
        //noinspection rawtypes
        valueObject = ((Map) sourceObject).get(fieldName);
      }
    } else if (fetcher != null) {
      valueObject = fetcher.get(env.getEnvironment());
    } else {
      Object sourceObject = env.getEnvironment().getSource();
      if (sourceObject instanceof Map) {
        //noinspection rawtypes
        valueObject = ((Map) sourceObject).get(env.getEnvironment().getFieldDefinition().getName());
      }
    }
    if (valueObject == null) {
      return null;
    }

    // unit
    String unit = env.getEnvironment().getArgument("unit");
    if (unit == null) {
      return valueObject;
    }
    Integer precision = env.getEnvironment().getArgument("precision");

    // transform value
    double value = (double) Long.parseLong(valueObject.toString());

    // format
    double result;
    switch (unit) {
      case "KILOBYTE":
        result = value / 1024L;
        break;
      case "MEGABYTE":
        result = value / (1024L * 1024L);
        break;
      case "GIGABYTE":
        result = value / (1024L * 1024L * 1024L);
        break;
      case "BYTE":
      default:
        result = value;
        break;
    }

    // precision
    if (precision == null) {
      return (long) result;
    } else {
      return BigDecimal.valueOf(result).setScale(precision, RoundingMode.DOWN).doubleValue();
    }
  }

  /**
   *
   */
  @Nullable
  public DataFetcher<?> getFetcher() {
    return fetcher;
  }

  /**
   *
   */
  @Nullable
  public String getFieldName() {
    return fieldName;
  }

  /**
   *
   */
  public static class Factory extends BaseUnitFetcherFactory {
    /**
     *
     */
    @NotNull
    @Override
    public DataFetcher<?> withUnitFetcher(@NotNull DataFetcher<?> fetcher) {
      return new ByteUnitFetcher(fetcher, null);
    }
  }

  /**
   *
   */
  public static class Directive implements SchemaDirectiveWiring {
    /**
     *
     */
    @Override
    public GraphQLFieldDefinition onField(@NotNull SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment) {
      GraphQLFieldDefinition field = environment.getElement();
      GraphQLFieldsContainer parentType = environment.getFieldsContainer();

      DataFetcher<?> originalFetcher = environment.getCodeRegistry().getDataFetcher(parentType, field);
      ByteUnitFetcher fetcher = new ByteUnitFetcher(originalFetcher, field.getName());

      // 使用新fetcher
      FieldCoordinates coordinates = FieldCoordinates.coordinates(parentType, field);
      environment.getCodeRegistry().dataFetcher(coordinates, fetcher);

      // 在参数列表末尾添加unit、precision字段
      return field.transform(it -> it
        .argument(GraphQLArgument.newArgument().name("unit").type(Scalars.GraphQLString))
        .argument(GraphQLArgument.newArgument().name("precision").type(Scalars.GraphQLInt))
      );
    }
  }
}
