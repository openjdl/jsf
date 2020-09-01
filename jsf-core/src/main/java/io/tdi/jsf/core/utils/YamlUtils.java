package io.tdi.jsf.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.io.Reader;

/**
 * Created at 2020-08-04 17:30:00
 *
 * @author kidal
 * @since 0.1.0
 */
public class YamlUtils {
  private static final Logger LOG = LoggerFactory.getLogger(YamlUtils.class);
  public static final Yaml YAML;

  static {
    DumperOptions dumperOptions = new DumperOptions();
    dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

    YAML = new Yaml(dumperOptions);
  }

  /**
   *
   */
  public static String toString(Object obj) {
    try {
      return YAML.dump(obj);
    } catch (Exception e) {
      LOG.warn("Convert {} to string failed", obj, e);
      throw new IllegalArgumentException(e);
    }
  }

  /**
   *
   */
  public static <T> T toObject(String content, Class<T> type) {
    try {
      return YAML.loadAs(content, type);
    } catch (Exception e) {
      LOG.warn("Convert {} to object {} failed", content, type.getName(), e);
      throw new IllegalArgumentException(e);
    }
  }

  /**
   *
   */
  public static <T> T toObject(InputStream stream, Class<T> type) {
    try {
      return YAML.loadAs(stream, type);
    } catch (Exception e) {
      LOG.warn("Convert input stream to object {} failed", type.getName(), e);
      throw new IllegalArgumentException(e);
    }
  }

  /**
   *
   */
  public static <T> T toObject(Reader reader, Class<T> type) {
    try {
      return YAML.loadAs(reader, type);
    } catch (Exception e) {
      LOG.warn("Convert reader to object {} failed", type.getName(), e);
      throw new IllegalArgumentException(e);
    }
  }
}
