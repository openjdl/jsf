package org.kidal.jsf.core.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.*;
import org.jetbrains.annotations.NotNull;
import org.kidal.jsf.core.utils.json.DoubleSerializer;
import org.kidal.jsf.core.utils.json.FloatSerializer;
import org.kidal.jsf.core.utils.json.Iso8601JsonSerializer;
import org.kidal.jsf.core.utils.json.UncertainDateJsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.util.*;

/**
 * Created at 2020-08-04 17:30:00
 *
 * @author kidal
 * @since 0.1.0
 */
public class JsonUtils {
  private static final Logger LOG = LoggerFactory.getLogger(JsonUtils.class);
  private static final TypeFactory FACTORY = TypeFactory.defaultInstance();

  public static final Converter DEFAULT;
  public static final Converter TYPING_AS_PROPERTY;

  static {
    MapperHolder defaultMapperHolder = new MapperHolder();
    DEFAULT = new Converter(defaultMapperHolder);

    MapperHolder typingAsPropertyMapperHolder = new MapperHolder();
    typingAsPropertyMapperHolder.enableDefaultTypingAsProperty();
    TYPING_AS_PROPERTY = new Converter(typingAsPropertyMapperHolder);
  }

  public static String toString(Object object) {
    return DEFAULT.toString(object);
  }

  public static String toPrettyString(Object object) {
    return DEFAULT.toPrettyString(object);
  }

  public static <T> T toObject(String content, Class<T> objectClass) {
    return DEFAULT.toObject(content, objectClass);
  }

  public static <T> T toObject(InputStream stream, Class<T> type) {
    return DEFAULT.toObject(stream, type);
  }

  public static <T> T toObject(Reader reader, Class<T> type) {
    return DEFAULT.toObject(reader, type);
  }

  public static <T> T[] toArray(String content, Class<T> elementClass) {
    return DEFAULT.toArray(content, elementClass);
  }

  public static <E, C extends Collection<E>> C toCollection(String content, Class<C> collectionClass, Class<E> elementClass) {
    return DEFAULT.toCollection(content, collectionClass, elementClass);
  }

  public static <E> ArrayList<E> toArrayList(String content, Class<E> elementClass) {
    return DEFAULT.toArrayList(content, elementClass);
  }

  public static <E> HashSet<E> toHashSet(String content, Class<E> elementClass) {
    return DEFAULT.toHashSet(content, elementClass);
  }

  public static <K, V, M extends Map<K, V>> M toMap(String content, Class<M> mapClass, Class<K> keyClass, Class<V> valueClass) {
    return DEFAULT.toMap(content, mapClass, keyClass, valueClass);
  }

  public static <K, V> Map<K, V> toMap(String content, Class<K> keyClass, Class<V> valueClass) {
    return DEFAULT.toMap(content, keyClass, valueClass);
  }

  public static <V> Map<String, V> toMap(String content, Class<V> valueClass) {
    return DEFAULT.toMap(content, valueClass);
  }

  public static Map<String, Object> toMap(String content) {
    return DEFAULT.toMap(content);
  }

  public static class MapperHolder {
    private final ObjectMapper mapper = new ObjectMapper();

    MapperHolder() {

      Version version = new Version(1, 0, 0, "RELEASE", "org.kidal", "jsf-core");
      SimpleModule module = new SimpleModule("org.kidal.jsf.core", version)
        .addSerializer(float.class, new FloatSerializer())
        .addSerializer(Float.class, new FloatSerializer())
        .addSerializer(double.class, new DoubleSerializer())
        .addSerializer(Double.class, new DoubleSerializer());
//        .addSerializer(Date.class, new Iso8601JsonSerializer())
//        .addDeserializer(Date.class, new UncertainDateJsonDeserializer());

      mapper.registerModule(module);
      mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
    }

    void enableDefaultTypingAsProperty() {
      mapper.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT, null);
    }

    @NotNull
    public ObjectMapper getMapper() {
      return mapper;
    }
  }

  public static class Converter {
    private final MapperHolder mapperHolder;

    public Converter(MapperHolder mapperHolder) {
      this.mapperHolder = mapperHolder;
    }

    public String toString(Object object) {
      StringWriter stringWriter = new StringWriter();
      try {
        mapperHolder.mapper.writeValue(stringWriter, object);
      } catch (IOException e) {
        LOG.error("将对象 {} {} 转换为字符串失败", object != null ? object.getClass() : "null", object);

        throw new IllegalArgumentException(e);
      }
      return stringWriter.toString();
    }

    public String toPrettyString(Object object) {
      try {
        return mapperHolder.mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
      } catch (IOException e) {
        LOG.error("将对象 {} {} 转换为字符串失败", object != null ? object.getClass() : "null", object);

        throw new IllegalArgumentException(e);
      }
    }

    public <T> T toObject(String content, Class<T> objectClass) {
      JavaType javaType = FACTORY.constructType(objectClass);
      try {
        return mapperHolder.mapper.readValue(content, javaType);
      } catch (IOException e) {
        LOG.warn("将 {} 转换为对象 {} 失败", content, objectClass, e);

        throw new IllegalArgumentException(e);
      }
    }

    public <T> T toObject(InputStream stream, Class<T> type) {
      try {
        return mapperHolder.mapper.readValue(stream, type);
      } catch (Exception e) {
        LOG.warn("Convert input stream to object {} failed", type.getName(), e);
        throw new IllegalArgumentException(e);
      }
    }

    public <T> T toObject(Reader reader, Class<T> type) {
      try {
        return mapperHolder.mapper.readValue(reader, type);
      } catch (Exception e) {
        LOG.warn("Convert reader to object {} failed", type.getName(), e);
        throw new IllegalArgumentException(e);
      }
    }

    public <T> T[] toArray(String content, Class<T> elementClass) {
      ArrayType arrayType = ArrayType.construct(FACTORY.constructType(elementClass), TypeBindings.emptyBindings(), null, null);
      try {
        return mapperHolder.mapper.readValue(content, arrayType);
      } catch (IOException e) {
        LOG.warn("将 {} 转换为数组 {}[] 失败", content, elementClass, e);

        throw new IllegalArgumentException(e);
      }
    }

    public <E, C extends Collection<E>> C toCollection(String content, Class<C> collectionClass, Class<E> elementClass) {
      CollectionType collectionType = FACTORY.constructCollectionType(collectionClass, elementClass);
      try {
        return mapperHolder.mapper.readValue(content, collectionType);
      } catch (IOException e) {
        LOG.warn("将 {} 转换为集合 {}<{}> 失败", content, collectionClass, elementClass, e);

        throw new IllegalArgumentException(e);
      }
    }

    public <E> ArrayList<E> toArrayList(String content, Class<E> elementClass) {
      //noinspection unchecked
      return toCollection(content, ArrayList.class, elementClass);
    }

    public <E> HashSet<E> toHashSet(String content, Class<E> elementClass) {
      //noinspection unchecked
      return toCollection(content, HashSet.class, elementClass);
    }

    public <K, V, M extends Map<K, V>> M toMap(String content, Class<M> mapClass, Class<K> keyClass, Class<V> valueClass) {
      MapType mapType = FACTORY.constructMapType(mapClass, keyClass, valueClass);
      try {
        return mapperHolder.mapper.readValue(content, mapType);
      } catch (IOException e) {
        LOG.warn("将 {} 转换为字典 {}<{},{}> 失败", content, mapClass, keyClass, valueClass, e);

        throw new IllegalArgumentException(e);
      }
    }

    public <K, V> Map<K, V> toMap(String content, Class<K> keyClass, Class<V> valueClass) {
      //noinspection unchecked
      return toMap(content, HashMap.class, keyClass, valueClass);
    }

    public <V> Map<String, V> toMap(String content, Class<V> valueClass) {
      //noinspection unchecked
      return toMap(content, HashMap.class, String.class, valueClass);
    }

    public Map<String, Object> toMap(String content) {
      //noinspection unchecked
      return toMap(content, HashMap.class, String.class, Object.class);
    }

    @NotNull
    public MapperHolder getMapperHolder() {
      return mapperHolder;
    }
  }
}
