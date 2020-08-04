package org.kidal.jsf.core.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.kidal.jsf.core.utils.DateUtils;

import java.io.IOException;
import java.util.Date;


/**
 * Created at 2020-08-04 17:30:00
 *
 * @author kidal
 * @since 0.1.0
 */
public class DateJsonDeserializer extends JsonDeserializer<Date> {
  @Override
  public Date deserialize(JsonParser p, DeserializationContext context) throws IOException, JsonProcessingException {
    String value = p.getValueAsString();
    if (value == null) {
      return null;
    }
    return DateUtils.toDate(value, DateUtils.PATTERN_DATE);
  }
}
