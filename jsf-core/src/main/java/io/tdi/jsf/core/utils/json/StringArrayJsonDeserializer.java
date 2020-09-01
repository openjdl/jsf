package io.tdi.jsf.core.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * Created at 2020-08-04 17:30:00
 *
 * @author kidal
 * @since 0.1.0
 */
public class StringArrayJsonDeserializer extends JsonDeserializer<String> {
  @Override
  public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    ObjectCodec codec = p.getCodec();
    JsonNode node = codec.readTree(p);
    if (node.isNull()) {
      return "[]";
    } else if (node.isArray() && !node.elements().hasNext()) {
      return "[]";
    }
    return codec.readTree(p).asToken().asString();
  }
}
