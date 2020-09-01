package io.tdi.jsf.core.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created at 2020-08-05 14:22:11
 *
 * @author kidal
 * @since 0.1.0
 */
public class FloatSerializer extends StdSerializer<Float> {
  private static final long serialVersionUID = 7756202071506068464L;

  /**
   *
   */
  private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat();

  /**
   *
   */
  public FloatSerializer() {
    super(Float.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void serialize(Float value, JsonGenerator generator, SerializerProvider provider) throws IOException {
    if (Float.isNaN(value) || Float.isInfinite(value)) {
      generator.writeNumber(0);
    } else {
      generator.writeRawValue(DECIMAL_FORMAT.format(value).replace(",", ""));
    }
  }
}
