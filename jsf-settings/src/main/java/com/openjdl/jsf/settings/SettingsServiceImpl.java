package com.openjdl.jsf.settings;

import com.openjdl.jsf.core.JsfService;
import com.openjdl.jsf.core.utils.ReflectionUtils;
import com.openjdl.jsf.core.utils.SpringUtils;
import com.openjdl.jsf.core.utils.StringUtils;
import com.openjdl.jsf.settings.annotation.*;
import com.openjdl.jsf.settings.boot.JsfSettingsProperties;
import com.openjdl.jsf.settings.definition.SettingsDefinition;
import com.openjdl.jsf.settings.storage.InMemorySettingsStorageFactory;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.*;

/**
 * Created at 2020-09-10 16:15:12
 *
 * @author kidal
 * @since 0.3
 */
public class SettingsServiceImpl implements SettingsService, JsfService {
  private static final Logger LOG = LoggerFactory.getLogger(SettingsServiceImpl.class);

  @NotNull
  private final JsfSettingsProperties properties;

  @NotNull
  private final ConversionService conversionService;

  @NotNull
  private final TaskScheduler taskScheduler;

  @NotNull
  private final SpringUtils springUtils;

  private SettingsStorageValueSource valueSource;

  private SettingsStorageFactory storageFactory;

  @Nullable
  private SettingsDefaultsResolver defaultsResolver;

  @SuppressWarnings("rawtypes")
  private final Map<Class, SettingsDefinition> typeDefinitionMap = new HashMap<>();

  private final Map<String, SettingsDefinition> idDefinitionMap = new HashMap<>();

  @SuppressWarnings("rawtypes")
  private final Map<Class, SettingsStorage> typeStorageMap = new HashMap<>();

  /**
   *
   */
  public SettingsServiceImpl(@NotNull JsfSettingsProperties properties,
                             @NotNull ConversionService conversionService,
                             @NotNull TaskScheduler taskScheduler,
                             @NotNull SpringUtils springUtils) {
    this.registerSelf();

    this.properties = properties;
    this.conversionService = conversionService;
    this.taskScheduler = taskScheduler;
    this.springUtils = springUtils;
  }

  /**
   *
   */
  @NotNull
  @Override
  public String getJsfServiceName() {
    return "SettingsService";
  }

  /**
   *
   */
  @Override
  public void onJsfServiceInitializeStage() throws Exception {
    // 搜索数据源
    valueSource = springUtils.getApplicationContext().getBean(SettingsStorageValueSource.class);

    // 仓库工厂
    storageFactory = springUtils.beanOrNull(SettingsStorageFactory.class);
    if (storageFactory == null) {
      storageFactory = new InMemorySettingsStorageFactory();
    }

    // 默认值解析器
    defaultsResolver = springUtils.beanOrNull(SettingsDefaultsResolver.class);

    // 扫描全部设置类
    Set<Class<?>> types = ReflectionUtils.loadClassesByAnnotation(Settings.class, properties.getPackagesToScan().split(","));

    // 创建定义
    SettingsDefinition[] definitions = types
      .stream()
      .map(type -> SettingsDefinition.parse(type, Objects.requireNonNull(AnnotationUtils.findAnnotation(type, Settings.class))))
      .toArray(SettingsDefinition[]::new);

    // 添加定义
    addDefinitions(definitions);
  }

  /**
   *
   */
  @Override
  public void onJsfServiceInjectStage() {
    // 初始化数据源
    valueSource.initialize();

    // 预加载全部仓储
    typeDefinitionMap.values().forEach(this::createStorage);

    // 注入
    springUtils.getAllBeans(true).forEach(this::initializeBean);

    // 通知观察者
    typeStorageMap.values().forEach(o -> o.notifyObservers(SettingsObserveTags.INITIALIZED));
  }

  /**
   *
   */
  @Override
  public void onJsfServiceStartStage() {
    // 启动自动刷新任务
    if (StringUtils.isNotBlank(properties.getAutoRefreshCron())) {
      taskScheduler.schedule(this::refreshAll, new CronTrigger(properties.getAutoRefreshCron()));
    }
  }

  /**
   *
   */
  @SuppressWarnings("rawtypes")
  private void initializeBean(@NotNull Object bean) {
    // 注入属性
    ReflectionUtils.doWithFields(
      bean.getClass(),
      field -> {
        if (field.isAnnotationPresent(SettingsInjectStorage.class)) {
          injectStorage(bean, field, Objects.requireNonNull(AnnotationUtils.findAnnotation(field, SettingsInjectStorage.class)));
        } else if (field.isAnnotationPresent(SettingsInjectValue.class)) {
          injectValue(bean, field, Objects.requireNonNull(AnnotationUtils.findAnnotation(field, SettingsInjectValue.class)));
        }
      });

    // 添加观察者
    ReflectionUtils.doWithMethods(
      bean.getClass(),
      method -> {
        SettingsStorageObserver annotation = AnnotationUtils.findAnnotation(method, SettingsStorageObserver.class);
        if (annotation == null) {
          return;
        }

        Set<SettingsStorage> storageSet = new HashSet<>();
        if (annotation.ids().length == 0 && annotation.types().length == 0) {
          storageSet.addAll(getAllStorage());
        } else {
          for (String id : annotation.ids()) {
            SettingsStorage storage = getStorage(id);
            if (storage == null) {
              throw new IllegalStateException(MessageFormat.format(
                "Incorrect SettingsStorageObserver(name={0}) at {1}{2}: storage not found",
                id, bean.getClass().getName(), method.getName()));
            }
            storageSet.add(storage);
          }

          for (Class type : annotation.types()) {
            SettingsStorage storage = getStorage(type);
            if (storage == null) {
              throw new IllegalStateException(MessageFormat.format(
                "Incorrect SettingsStorageObserver(classes={0}) at {1}{2}: storage not found",
                type, bean.getClass().getName(), method.getName()));
            }
            storageSet.add(storage);
          }
        }

        if (storageSet.size() > 0) {
          SettingsStorageObserverProxy proxy = new SettingsStorageObserverProxy(bean, method, annotation.keys(), annotation.order());
          String[] tags = annotation.tags();

          for (String tag : tags) {
            storageSet.forEach(storage -> storage.addObserver(tag, proxy));
          }
        }
      });
  }

  /**
   *
   */
  @SuppressWarnings("rawtypes")
  private void injectStorage(@NotNull Object bean, @NotNull Field field, @NotNull SettingsInjectStorage annotation) {
    // 检查字段类型
    if (!field.getType().equals(SettingsStorage.class)) {
      throw new IllegalStateException(String.format(
        "Can not inject %s.%s: Incorrect field type.",
        bean.getClass().getName(), field.getName()));
    }

    // 获取泛型类型
    Type genericType = field.getGenericType();
    if (!(genericType instanceof ParameterizedType)) {
      throw new IllegalStateException(String.format(
        "Can not inject %s.%s: Incorrect generic type.",
        bean.getClass().getName(), field.getName()));
    }

    // 获取实际的泛型类型
    Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
    if (actualTypeArguments.length != 2 || !(actualTypeArguments[1] instanceof Class)) {
      throw new IllegalStateException(String.format(
        "Can not inject %s.%s: Incorrect generic type.",
        bean.getClass().getName(), field.getName()));
    }

    // 获取注入器
    Class<? extends InjectSettingsStorageProvider> providerType =
      annotation.providerType() == InjectSettingsStorageProvider.class
        ? DefaultInjectSettingsStorageProvider.class
        : annotation.providerType();
    InjectSettingsStorageProvider provider = createProvider(providerType);
    provider.init(annotation.storageId(), annotation.required());

    // 获取仓储
    SettingsStorage storage = null;
    if (provider.getStorageId() != null) {
      storage = (Objects.equals(provider.getStorageId(), ""))
        ? getStorage((Class) actualTypeArguments[1])
        : getStorage(provider.getStorageId());
    }

    // 检查必要
    if (storage == null) {
      if (provider.isRequired()) {
        throw new IllegalStateException(String.format(
          "Inject configuration storage %s failed: storage not found.", provider));
      }

      // 直接返回，否则会修改到默认值
      return;
    }

    // 注入
    ReflectionUtils.makeAccessible(field);
    ReflectionUtils.setField(field, bean, storage);

    // log
    LOG.debug("Injected storage {} -> {}#{}", provider.getStorageId(), field.getType().getName(), field.getName());
  }

  /**
   *
   */
  @SuppressWarnings("rawtypes")
  private void injectValue(@NotNull Object bean, @NotNull Field field, @NotNull SettingsInjectValue annotation) {
    // 获取注入器
    Class<? extends InjectSettingsValueProvider> providerType =
      annotation.providerType() == InjectSettingsValueProvider.class
        ? DefaultInjectSettingsValueProvider.class
        : annotation.providerType();
    InjectSettingsValueProvider provider = createProvider(providerType);
    provider.init(annotation.storageId(), annotation.key(), annotation.required(), annotation.applyDefaultsResolver());

    // 获取仓储
    SettingsStorage storage = null;
    if (provider.getStorageId() != null) {
      storage = (Objects.equals(provider.getStorageId(), "")) ?
        getStorage(field.getType()) :
        getStorage(provider.getStorageId());
    }

    // 检查必须
    if (storage == null) {
      if (provider.isRequired()) {
        throw new IllegalStateException(String.format(
          "Inject configuration value %s %s <- %s %s failed: value not found",
          field.getType().getName(), field.getName(), provider.getStorageId(), provider.getKey()));
      }
      return;
    }

    // 获取主键字段
    Optional<Field> keyOptional = Arrays.stream(field.getType().getDeclaredFields())
      .filter(f -> f.isAnnotationPresent(SettingsKey.class))
      .findFirst();
    if (!keyOptional.isPresent()) {
      throw new IllegalStateException(String.format(
        "Inject configuration value %s %s <- %s %s failed: key not found",
        field.getType().getName(), field.getName(), storage.getDefinition().getId(), provider.getKey()));
    }
    Class keyType = keyOptional.get().getType();

    // 处理主键
    Pair<Object, Object> keyValue = injectValueProcessKeyValue(keyType, bean, field, provider, storage);
    Object key = keyValue.getLeft();
    Object value = keyValue.getRight();

    // inject
    ReflectionUtils.makeAccessible(field);
    if (value != null) {
      ReflectionUtils.setField(field, bean, value);
    }

    // add observer
    storage.addObserver(SettingsObserveTags.PROPERTY_CHANGED, new SettingsValueObserverProxy(bean, field, provider, key));

    // log
    LOG.debug("Injected storage value {}#{} -> {}#{}", storage.getDefinition().getId(), key, field.getType().getName(), field.getName());
  }

  /**
   *
   */
  @NotNull
  @SuppressWarnings({"rawtypes", "unchecked"})
  private Pair<Object, Object> injectValueProcessKeyValue(Class keyType,
                                                          Object bean,
                                                          Field field,
                                                          InjectSettingsValueProvider provider,
                                                          SettingsStorage storage) {
    Object originalKey, key, value;

    if (String.class.equals(keyType)) {
      originalKey = key = provider.getKey();
      if (((String) key).length() > 0) {
        value = storage.get(key);
        if (value == null) {
          value = storage.get(key = key + "." + field.getName());
        }
      } else {
        key = field.getName();
        value = storage.get(key);
        if (value == null) {
          value = storage.get(key = ((String) key).replace('_', '.'));
        }
      }
    } else {
      originalKey = key = conversionService.convert(provider.getKey(), keyType);

      value = storage.get(Objects.requireNonNull(key));
    }

    // try apply defaults
    if (value == null && provider.isApplyDefaultsResolver()) {
      if (defaultsResolver != null) {
        value = defaultsResolver.resolveSettingsDefaults(storage.getDefinition(), originalKey);
      }
    }

    // check required
    if (provider.isRequired() && value == null) {
      throw new IllegalStateException(MessageFormat.format(
        "Inject configuration value {0} {1} => {2} failed: value not found",
        field.getType(), field.getName(), key));
    }

    // done
    return Pair.of(key, value);
  }

  /**
   *
   */
  @NotNull
  private <T> T createProvider(@NotNull Class<T> providerType) {
    try {
      return providerType.getDeclaredConstructor().newInstance();
    } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
      throw new IllegalStateException("Instantiate provider failed", e);
    }
  }

  /**
   *
   */
  private void addDefinitions(@NotNull SettingsDefinition... definitions) {
    for (SettingsDefinition definition : definitions) {
      Class<?> type = definition.getType();
      String id = definition.getId();

      if (typeDefinitionMap.put(type, definition) != null) {
        throw new IllegalStateException(String.format(
          "Definition type %s already exist", type.getName()));
      }
      if (idDefinitionMap.put(id, definition) != null) {
        throw new IllegalStateException(String.format(
          "Definition id %s already exist", id));
      }
    }
  }

  /**
   *
   */
  @SuppressWarnings("unchecked")
  private <K, V> SettingsStorage<K, V> createStorage(@NotNull SettingsDefinition definition) {
    if (typeStorageMap.containsKey(definition.getType())) {
      return (SettingsStorage<K, V>) typeStorageMap.get(definition.getType());
    }

    // 注入静态字段
    definition
      .getInjectBeanDefinitions()
      .forEach(item -> {
        if (item.isStatic()) {
          Field field = item.getField();
          Object bean = item.getBean(springUtils.getApplicationContext());
          try {
            field.set(null, bean);
          } catch (IllegalAccessException e) {
            throw new IllegalStateException(String.format(
              "Inject bean to configuration %s field %s failed",
              definition.getType().getName(), field.getName()), e);
          }
        }
      });

    // create storage.
    SettingsStorage<K, V> storage = getStorageFactory().createStorage(this, definition);

    // cache
    typeStorageMap.put(definition.getType(), storage);

    // log
    LOG.debug("Loaded storage `{}`", definition.toString());

    // done
    return storage;
  }

  /**
   *
   */
  @SuppressWarnings("rawtypes")
  @Override
  public void refresh(String... ids) {
    SettingsStorage[] targets = Arrays.stream(ids)
      .map(this::getStorage)
      .filter(Objects::nonNull)
      .toArray(SettingsStorage[]::new);
    refresh(targets);
  }

  /**
   *
   */
  @SuppressWarnings("rawtypes")
  @Override
  public void refresh(Class... types) {
    SettingsStorage[] targets = Arrays.stream(types)
      .map(this::getStorage)
      .filter(Objects::nonNull)
      .toArray(SettingsStorage[]::new);
    refresh(targets);
  }

  /**
   *
   */
  @SuppressWarnings("rawtypes")
  @Override
  public void refresh(SettingsStorage... targets) {
    // notify observers.
    Arrays.stream(targets).forEach(
      target -> target.notifyObservers(SettingsObserveTags.BEFORE_REFRESH_ALL));

    // refresh targets.
    Arrays.stream(targets).forEach(SettingsStorage::refresh);

    // notify observers.
    Arrays.stream(targets).forEach(
      target -> target.notifyObservers(SettingsObserveTags.AFTER_REFRESH_ALL));
  }

  /**
   *
   */
  @SuppressWarnings("rawtypes")
  @Override
  public void refreshAll() {
    SettingsStorage[] targets = typeStorageMap.values().toArray(new SettingsStorage[0]);
    refresh(targets);
  }

  /**
   *
   */
  @SuppressWarnings({"rawtypes"})
  @Override
  public SettingsStorage getStorage(@NotNull String id) {
    return idDefinitionMap.containsKey(id)
      ? getStorage(idDefinitionMap.get(id).getType())
      : null;
  }

  /**
   *
   */
  @SuppressWarnings({"rawtypes"})
  @Override
  public SettingsStorage getStorage(@NotNull Class type) {
    return typeStorageMap.get(type);
  }

  /**
   *
   */
  @SuppressWarnings({"rawtypes"})
  @Override
  public SettingsStorage loadStorage(@NotNull String id) {
    return idDefinitionMap.containsKey(id)
      ? loadStorage(idDefinitionMap.get(id).getType())
      : null;
  }

  /**
   *
   */
  @SuppressWarnings({"rawtypes"})
  @Override
  public SettingsStorage loadStorage(@NotNull Class type) {
    if (typeStorageMap.containsKey(type)) {
      return typeStorageMap.get(type);
    }

    if (typeDefinitionMap.containsKey(type)) {
      return createStorage(typeDefinitionMap.get(type));
    }

    return null;
  }

  /**
   *
   */
  @SuppressWarnings({"rawtypes"})
  @NotNull
  @Override
  public Collection<SettingsStorage> getAllStorage() {
    return Collections.unmodifiableCollection(typeStorageMap.values());
  }

  /**
   *
   */
  @NotNull
  public ConversionService getConversionService() {
    return conversionService;
  }

  /**
   *
   */
  @NotNull
  public TaskScheduler getTaskScheduler() {
    return taskScheduler;
  }

  /**
   *
   */
  @NotNull
  public SpringUtils getSpringUtils() {
    return springUtils;
  }

  /**
   *
   */
  @NotNull
  public SettingsStorageValueSource getValueSource() {
    return Objects.requireNonNull(valueSource, "未初始化完成");
  }

  /**
   *
   */
  @NotNull
  public SettingsStorageFactory getStorageFactory() {
    return Objects.requireNonNull(storageFactory, "未初始化完成");
  }
}
