package com.openjdl.jsf.graphql.fetcher;

import com.openjdl.jsf.core.utils.DateUtils;
import com.openjdl.jsf.graphql.query.GraphqlFetchingEnvironment;
import graphql.Scalars;
import graphql.schema.*;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * Created at 2020-08-06 14:26:48
 *
 * @author kidal
 * @since 0.1.0
 */
public class TimeUnitFetcher extends BaseGraphqlDataFetcher<Object> {
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
  public TimeUnitFetcher(@Nullable DataFetcher<?> fetcher, @Nullable String fieldName) {
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
      case "seconds":
        result = value / DateUtils.MILLIS_PER_SECOND;
        break;
      case "minutes":
        result = value / DateUtils.MILLIS_PER_MINUTE;
        break;
      case "hours":
        result = value / DateUtils.MILLIS_PER_HOUR;
        break;
      case "days":
        result = value / DateUtils.MILLIS_PER_DAY;
        break;
      default:
        result = value;
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
      return new TimeUnitFetcher(fetcher, null);
    }
  }

  /**
   *
   */
  public static class Directive implements SchemaDirectiveWiring {
    /**
     *
     */
    public static final String NAME = "time";

    /**
     *
     */
    @Override
    public GraphQLFieldDefinition onField(@NotNull SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment) {
      GraphQLFieldDefinition field = environment.getElement();
      GraphQLFieldsContainer parentType = environment.getFieldsContainer();

      DataFetcher<?> originalFetcher = environment.getCodeRegistry().getDataFetcher(parentType, field);
      TimeUnitFetcher fetcher = new TimeUnitFetcher(originalFetcher, null);

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
