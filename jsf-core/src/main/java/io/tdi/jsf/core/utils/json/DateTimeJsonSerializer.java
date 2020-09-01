package io.tdi.jsf.core.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.tdi.jsf.core.utils.DateUtils;

import java.io.IOException;
import java.util.Date;

/**
 * Created at 2020-08-04 17:30:00
 *
 * @author kidal
 * @since 0.1.0
 */
public class DateTimeJsonSerializer extends JsonSerializer<Date> {
  @Override
  public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
    if (value == null) {
      gen.writeNull();
    } else {
      String string = DateUtils.toString(value, DateUtils.PATTERN_DATE_TIME);
      gen.writeString(string);
    }
  }
}
