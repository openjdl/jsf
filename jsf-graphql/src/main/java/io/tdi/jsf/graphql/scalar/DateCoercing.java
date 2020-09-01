package io.tdi.jsf.graphql.scalar;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;
import io.tdi.jsf.core.utils.DateUtils;

import java.util.Date;

/**
 * Created at 2020-08-06 13:27:21
 *
 * @author kidal
 * @since 0.1.0
 */
public class DateCoercing implements Coercing<Date, String> {
  /**
   * Date
   */
  public static final GraphQLScalarType GraphQLDate = GraphQLScalarType.newScalar()
    .name("Date")
    .description("Built-in java.util.Date")
    .coercing(new DateCoercing())
    .build();

  /**
   *
   */
  @Override
  public Date parseValue(Object input) {
    if (input == null) {
      return null;
    } else if (input instanceof String) {
      return DateUtils.uncertainToDateSafely((String) input);
    } else {
      return null;
    }
  }

  /**
   *
   */
  @Override
  public Date parseLiteral(Object input) {
    if (input == null) {
      return null;
    } else if (input.getClass() == StringValue.class) {
      return DateUtils.uncertainToDateSafely(((StringValue) input).getValue());
    } else {
      return null;
    }
  }

  /**
   *
   */
  @Override
  public String serialize(Object dataFetcherResult) {
    if (dataFetcherResult == null) {
      return null;
    } else if (dataFetcherResult.getClass() == Long.class) {
      return dataFetcherResult.toString();
    } else if (dataFetcherResult.getClass() == String.class) {
      return (String) dataFetcherResult;
    } else {
      return DateUtils.iso8601ToStringSafely(dataFetcherResult);
    }
  }
}
