package io.tdi.jsf.core.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created at 2020-08-05 14:21:32
 *
 * @author kidal
 * @since 0.1.0
 */
public class DoubleSerializer extends StdSerializer<Double> {
  private static final long serialVersionUID = -733245757544033360L;

  /**
   *
   */
  private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat();

  /**
   *
   */
  public DoubleSerializer() {
    super(Double.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void serialize(Double value, JsonGenerator generator, SerializerProvider provider) throws IOException {
    if (Double.isNaN(value) || Double.isInfinite(value)) {
      generator.writeNumber(0);
    } else {
      generator.writeRawValue(DECIMAL_FORMAT.format(value).replace(",", ""));
    }
  }
}
