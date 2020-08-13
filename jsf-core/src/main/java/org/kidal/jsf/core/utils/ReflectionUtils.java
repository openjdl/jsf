package org.kidal.jsf.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created at 2020-08-04 17:30:00
 *
 * @author kidal
 * @since 0.1.0
 */
public class ReflectionUtils extends org.springframework.util.ReflectionUtils {
  private static final Logger LOG = LoggerFactory.getLogger(ReflectionUtils.class);

  /**
   *
   */
  public static List<Field> getFields(Class<?> type, boolean local) {
    List<Field> fields = new ArrayList<>();

    if (local) {
      doWithLocalFields(type, fields::add);
    } else {
      doWithFields(type, fields::add, null);
    }

    return fields;
  }

  /**
   *
   */
  public static <A extends Annotation> Field[] getFieldsArray(Class<?> type, Class<A> annotationType, boolean local) {
    return getFields(type, false).stream()
      .filter(field -> field.isAnnotationPresent(annotationType))
      .toArray(Field[]::new);
  }

  /**
   *
   */
  public static <A extends Annotation> List<Field> getFields(Class<?> type, Class<A> annotationType, boolean local) {
    List<Field> list = new ArrayList<>();
    Field[] fieldsArray = getFieldsArray(type, annotationType, local);
    Collections.addAll(list, fieldsArray);
    return list;
  }

  /**
   *
   */
  public static <A extends Annotation> Field getField(Class<?> type, Class<A> annotationType, boolean local, boolean requireUnique) {
    Field[] fields = getFieldsArray(type, annotationType, local);

    if (fields.length == 1) {
      return fields[0];
    } else if (fields.length > 1 && requireUnique) {
      throw new IllegalStateException("Field not unique");
    }

    return null;
  }

  /**
   *
   */
  public static List<Method> getMethods(Class<?> type, boolean local) {
    List<Method> methods = new ArrayList<>();

    if (local) {
      doWithLocalMethods(type, methods::add);
    } else {
      doWithMethods(type, methods::add, null);
    }

    return methods;
  }

  /**
   *
   */
  public static <A extends Annotation> Method[] getMethodsArray(Class<?> type, Class<A> annotationType, boolean local) {
    return getMethods(type, false).stream()
      .filter(method -> method.isAnnotationPresent(annotationType))
      .toArray(Method[]::new);
  }

  /**
   *
   */
  public static <A extends Annotation> List<Method> getMethods(Class<?> type, Class<A> annotationType, boolean local) {
    List<Method> list = new ArrayList<>();
    Method[] methodsArray = getMethodsArray(type, annotationType, local);
    Collections.addAll(list, methodsArray);
    return list;
  }

  /**
   *
   */
  public static <A extends Annotation> Method getMethod(Class<?> type, Class<A> annotationType, boolean local, boolean requireUnique) {
    Method[] methods = getMethodsArray(type, annotationType, local);

    if (methods.length == 1) {
      return methods[0];
    } else if (methods.length > 1 && requireUnique) {
      throw new IllegalStateException("Method not unique");
    }

    return null;
  }

  /**
   *
   */
  public static Set<Class<?>> loadClasses(String... packageNames) throws IOException {
    Set<Class<?>> classes = new LinkedHashSet<>();

    for (String packageName : packageNames) {
      String packageDirectoryName = packageName.replace('.', '/');

      Enumeration<URL> urlEnumeration = Thread.currentThread()
        .getContextClassLoader()
        .getResources(packageDirectoryName);

      while (urlEnumeration.hasMoreElements()) {
        URL url = urlEnumeration.nextElement();

        if ("file".equals(url.getProtocol())) {
          classes.addAll(loadClassesFromDirectory(packageName, new File(url.getPath())));
        } else if ("jar".equals(url.getProtocol())) {
          classes.addAll(loadClassesFromJar(packageName, ((JarURLConnection) url.openConnection()).getJarFile()));
        }
      }
    }

    return classes;
  }

  /**
   *
   */
  public static Set<Class<?>> loadClassesFromDirectory(String packageName, File directory) {
    Stack<File> stack = new Stack<>();
    List<File> classFiles = new ArrayList<>();
    FileFilter fileFilter = pathname -> {
      if (pathname.isDirectory()) {
        stack.push(pathname);
        return false;
      }
      return pathname.getName().matches(".*\\.class$");
    };

    stack.push(directory);

    while (!stack.isEmpty()) {
      File current = stack.pop();
      File[] files = current.listFiles(fileFilter);

      if (files != null) {
        Collections.addAll(classFiles, files);
      }
    }

    Pattern pattern = Pattern.compile("(" + packageName.replace('.', '/') + ".*)\\.class");
    Set<Class<?>> classes = new HashSet<>();

    for (File file : classFiles) {
      Matcher matcher = pattern.matcher(file.getAbsolutePath().replace(File.separatorChar, '/'));
      Class<?> objectClass = loadClass(matcher);

      if (objectClass != null) {
        classes.add(objectClass);
      }
    }

    return classes;
  }

  /**
   *
   */
  public static Set<Class<?>> loadClassesFromJar(String packageName, JarFile jarFile) {
    Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
    Pattern pattern = Pattern.compile("(" + packageName.replace('.', '/') + ".*)\\.class");
    Set<Class<?>> classes = new LinkedHashSet<>();

    while (jarEntryEnumeration.hasMoreElements()) {
      JarEntry jarEntry = jarEntryEnumeration.nextElement();
      String name = jarEntry.getName();
      Matcher matcher = pattern.matcher(name.replace(File.separatorChar, '/'));
      Class<?> objectClass = loadClass(matcher);

      if (objectClass != null) {
        classes.add(objectClass);
      }
    }

    return classes;
  }

  /**
   *
   */
  public static Set<Class<?>> loadClassesByAnnotation(Class<? extends Annotation> annotationType,
                                                      String... packageNames) throws IOException, ClassNotFoundException {
    String annotationClassName = annotationType.getName();
    PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
    CachingMetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(patternResolver);

    Set<Class<?>> classes = new HashSet<>();

    for (String packageName : packageNames) {
      String locationPattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
        ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(packageName)) +
        "/" + "**/*.class";
      Resource[] resources = patternResolver.getResources(locationPattern);
      for (Resource resource : resources) {
        if (!resource.isReadable()) {
          continue;
        }

        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();

        if (!annotationMetadata.hasAnnotation(annotationClassName)) {
          continue;
        }

        String className = metadataReader.getClassMetadata().getClassName();
        Class<?> loadedClass = ClassUtils.forName(className, null);
        classes.add(loadedClass);
      }
    }

    return classes;
  }

  /**
   *
   */
  private static Class<?> loadClass(Matcher matcher) {
    if (matcher.find()) {
      String className = matcher.group(1).replace('/', '.');
      try {
        return Class.forName(className);
      } catch (ClassNotFoundException e) {
        LOG.error("", e);
      }
    }

    return null;
  }

  /**
   *
   */
  public static void setPojoFields(Object pojo, Map<String, Object> in, ConversionService conversionService) {
    if (in != null && in.size() > 0) {
      // collect fields
      Map<String, Field> fields = new HashMap<>();
      Class<?> currentClass = pojo.getClass();
      do {
        Arrays.stream(currentClass.getDeclaredFields())
          .forEach(item -> fields.put(item.getName(), item));
        currentClass = currentClass.getSuperclass();
      } while (currentClass != Object.class);

      // set
      in.forEach((key, value) -> {
        Field field = fields.get(key);
        if (field != null) {
          if (Modifier.isStatic(field.getModifiers())) {
            return;
          }
          if (!field.isAccessible()) {
            field.setAccessible(true);
          }
          try {
            if (conversionService != null) {
              value = conversionService.convert(value, field.getType());
            }
            field.set(pojo, value);
          } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
          }
        }
      });
    }
  }

  /**
   *
   */
  public static void setPojoFields(Object pojo, Map<String, Object> in) {
    setPojoFields(pojo, in, null);
  }

  /**
   *
   */
  public static Object mapToPojo(Map<String, Object> in, Class<?> type, ConversionService conversionService) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
//    Object obj = type.getEnclosingConstructor().newInstance();
    Object obj = type.getConstructor().newInstance();
    setPojoFields(obj, in, conversionService);
    return obj;
  }

  /**
   *
   */
  public static Object mapToPojo(Map<String, Object> in, Class<?> type) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
    return mapToPojo(in, type, null);
  }

  /**
   *
   */
  public static Object mapToPojo(Map<String, Object> in) throws IllegalAccessException, InstantiationException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException {
    if (in == null) {
      return null;
    }
    if (!in.containsKey("__class")) {
      return null;
    }
    Class<?> type = ClassUtils.forName(in.get("__class").toString(), null);
    return mapToPojo(in, type, null);
  }

  /**
   *
   */
  public static Map<String, Object> pojoToMap(Object in, boolean removeClass) {
    if (in == null) {
      return null;
    }
    Map<String, Object> m = new LinkedHashMap<>();
    List<Field> fields = new ArrayList<>();

    Class<?> currentClass = in.getClass();
    do {
      fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
      currentClass = currentClass.getSuperclass();
    } while (currentClass != Object.class);

    fields.forEach(field -> {
      if (Modifier.isStatic(field.getModifiers())) {
        return;
      }
      if (Modifier.isTransient(field.getModifiers())) {
        return;
      }

      if (!field.isAccessible()) {
        field.setAccessible(true);
      }
      try {
        Object value = field.get(in);
        m.put(field.getName(), value);
      } catch (IllegalAccessException e) {
        LOG.error("", e);
      }
    });

    if (!removeClass) {
      m.put("__class", in.getClass().getName());
    }

    return m;
  }

  /**
   *
   */
  public static Map<String, Object> pojoToMap(Object in) {
    return pojoToMap(in, true);
  }
}
