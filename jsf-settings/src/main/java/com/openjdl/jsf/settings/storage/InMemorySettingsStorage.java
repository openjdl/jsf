package com.openjdl.jsf.settings.storage;

import com.openjdl.jsf.core.observe.CopyOnWriteTaggedObservable;
import com.openjdl.jsf.settings.*;
import com.openjdl.jsf.settings.accessor.Getter;
import com.openjdl.jsf.settings.accessor.GetterUtils;
import com.openjdl.jsf.settings.accessor.IndexGetter;
import com.openjdl.jsf.settings.definition.SettingsDefinition;
import com.openjdl.jsf.settings.definition.SettingsInjectBeanDefinition;
import com.openjdl.jsf.settings.event.SettingsStoragePropertiesChangedEventArgs;
import com.openjdl.jsf.settings.exception.SettingsException;
import com.openjdl.jsf.settings.exception.SettingsValidationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Created at 2020-09-10 16:11:46
 *
 * @author kidal
 * @since 0.3
 */
public class InMemorySettingsStorage<K, V> extends CopyOnWriteTaggedObservable implements SettingsStorage<K, V> {
  private static final Logger logger = LoggerFactory.getLogger(InMemorySettingsStorage.class);

  @NotNull
  private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
  @NotNull
  private final ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();
  @NotNull
  private final ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();

  @NotNull
  private final SettingsServiceImpl service;
  @NotNull
  private final SettingsDefinition definition;
  @NotNull
  private final Getter keyGetter;
  @NotNull
  private final Map<String, IndexGetter> indexGetterMap;

  private SettingsMetadata metadata = null;
  private Map<K, V> values = new HashMap<>();
  private Map<String, Map<Object, List<V>>> indexValues = new HashMap<>();
  private Map<String, Map<Object, V>> uniqueValues = new HashMap<>();

  /**
   *
   */
  public InMemorySettingsStorage(@NotNull SettingsServiceImpl service,
                                 @NotNull SettingsDefinition definition) {
    this.service = service;
    this.definition = definition;
    this.keyGetter = GetterUtils.createKeyGetter(definition.getType());
    this.indexGetterMap = GetterUtils.createIndexGetters(definition.getType());

    // 注入静态豆子
    definition
      .getInjectBeanDefinitions()
      .stream()
      .filter(SettingsInjectBeanDefinition::isStatic)
      .forEach(beanDefinition -> {
        Field field = beanDefinition.getField();
        Object bean = beanDefinition.getBean(service.getSpringUtils().getApplicationContext());
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, null, bean);
      });

    // refresh
    this.refresh();
  }

  /**
   *
   */
  @NotNull
  @Override
  public SettingsDefinition getDefinition() {
    return definition;
  }

  /**
   *
   */
  @NotNull
  @Override
  public SettingsMetadata getMetadata() {
    return metadata;
  }

  /**
   *
   */
  @Override
  public void refresh() {
    // 准备数据
    SettingsStorageValueSource valueSource = service.getValueSource();

    // 读取元数据
    SettingsMetadata newMetadata = valueSource.loadMetadata(definition);
    if (metadata != null && metadata.isUpToDate(newMetadata)) {
      //logger.debug("Storage is up-to-date, will not refresh");
      return;
    }

    // 通知观察者
    notifyObservers(SettingsObserveTags.BEFORE_REFRESH);

    // 准备数据
    Map<K, V> newValues = new HashMap<>();
    Map<String, Map<Object, List<V>>> newIndexValues = new HashMap<>();
    Map<String, Map<Object, V>> newUniqueValues = new HashMap<>();

    // 读取全部数据
    Collection<V> targets = valueSource.loadAll(definition);

    // 准备新数据
    for (V target : targets) {
      // 注入数据
      definition
        .getInjectBeanDefinitions()
        .forEach(injectBeanDefinition -> {
          Field field = injectBeanDefinition.getField();
          Object bean = injectBeanDefinition.getBean(service.getSpringUtils().getApplicationContext());
          ReflectionUtils.setField(field, target, bean);
        });

      // 准备缓存
      buildCache(newValues, newIndexValues, newUniqueValues, target);
    }

    // 排序索引
    newIndexValues.forEach((indexName, indexValues) -> {
      IndexGetter getter = indexGetterMap.get(indexName);
      if (getter.getComparator() != null) {
        //noinspection unchecked
        indexValues.values().forEach(list -> list.sort((Comparator<V>) getter.getComparator()));
      }
    });

    // 校验数据
    if (definition.canValidate()) {
      newValues
        .values()
        .forEach(item -> {
          try {
            ((SettingsValidating) item).validate();
          } catch (RuntimeException e) {
            Object key = keyGetter.getValue(item);
            throw new SettingsValidationException(String.format(
              "校验设置值 %s:%s 失败", definition.getType().getName(), key),
              e);
          }
        });
    }

    // 计算变更
    Map<K, V> addedProperties = newValues.entrySet().stream()
      .filter(e -> !this.values.containsKey(e.getKey()))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    Map<K, V> removedProperties = this.values.entrySet().stream()
      .filter((entry) -> !newValues.containsKey(entry.getKey()))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    Map<K, V> changedProperties = this.values.entrySet().stream()
      .filter(entry -> newValues.containsKey(entry.getKey()) && !newValues.get(entry.getKey()).equals(entry.getValue()))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    // 交换缓存
    writeLock.lock();
    try {
      metadata = newMetadata;
      values = newValues;
      indexValues = newIndexValues;
      uniqueValues = newUniqueValues;
    } finally {
      writeLock.unlock();
    }

    // 通知观察者
    if (addedProperties.size() > 0 || removedProperties.size() > 0 || changedProperties.size() > 0) {
      notifyObservers(
        SettingsObserveTags.PROPERTY_CHANGED,
        new SettingsStoragePropertiesChangedEventArgs<>(addedProperties, removedProperties, changedProperties)
      );
    }
    notifyObservers(SettingsObserveTags.AFTER_REFRESH);
  }

  /**
   *
   */
  @Nullable
  @Override
  public V get(@NotNull K key) {
    readLock.lock();
    try {
      return values.get(key);
    } finally {
      readLock.unlock();
    }
  }

  /**
   *
   */
  @Override
  public boolean containsKey(@NotNull K key) {
    readLock.lock();
    try {
      return values.containsKey(key);
    } finally {
      readLock.unlock();
    }
  }

  /**
   *
   */
  @NotNull
  @Override
  public Collection<V> values() {
    readLock.lock();
    try {
      return Collections.unmodifiableCollection(values.values());
    } finally {
      readLock.unlock();
    }
  }

  /**
   *
   */
  @Nullable
  @Override
  public V getUniqueIndexedValue(@NotNull String indexName, @NotNull Object indexKey) {
    readLock.lock();
    try {
      Map<Object, V> indexValues = uniqueValues.get(indexName);
      return indexValues != null ? indexValues.get(indexKey) : null;
    } finally {
      readLock.unlock();
    }
  }

  /**
   *
   */
  @NotNull
  @Override
  public List<V> getIndexedValues(@NotNull String indexName, @NotNull Object indexKey) {
    readLock.lock();
    try {
      Map<Object, List<V>> indexValues = this.indexValues.get(indexName);
      if (indexValues == null) {
        return Collections.emptyList();
      }
      List<V> list = indexValues.get(indexKey);
      if (list == null) {
        return Collections.emptyList();
      }
      return Collections.unmodifiableList(list);
    } finally {
      readLock.unlock();
    }
  }

  /**
   *
   */
  @Override
  public void notifyObservers(@NotNull String tag, Object arg) {
    readLock.lock();
    try {
      super.notifyObservers(tag, arg);
    } finally {
      readLock.unlock();
    }
  }

  /**
   *
   */
  private void buildCache(Map<K, V> newValues,
                          Map<String, Map<Object, List<V>>> newIndexValues,
                          Map<String, Map<Object, V>> newUniqueValues,
                          V target) {
    // get key
    @SuppressWarnings("unchecked")
    K key = (K) keyGetter.getValue(target);
    if (key == null) {
      throw new SettingsException(definition.toString() + " key is null");
    }

    // store
    newValues.put(key, target);

    // store index
    indexGetterMap.values().forEach(getter -> {
      String indexName = getter.getIndexName();
      Object indexKey = getter.getValue(target);

      if (getter.isUnique()) {
        Map<Object, V> uniqueMap = loadUniqueIndex(newUniqueValues, indexName);
        if (uniqueMap.put(indexKey, target) != null) {
          throw new SettingsException(
            definition.toString() + " duplicate unique index " + indexName + "." + indexKey);
        }
      } else {
        loadIndexList(newIndexValues, indexName, indexKey).add(target);
      }
    });
  }

  /**
   *
   */
  private List<V> loadIndexList(Map<String, Map<Object, List<V>>> newIndexValues, String indexName, Object indexKey) {
    Map<Object, List<V>> indexMap = loadIndex(newIndexValues, indexName);
    if (indexMap.containsKey(indexKey)) {
      return indexMap.get(indexKey);
    }

    List<V> list = new ArrayList<>();
    indexMap.put(indexKey, list);
    return list;
  }

  /**
   *
   */
  private Map<Object, List<V>> loadIndex(@NotNull Map<String, Map<Object, List<V>>> newIndexValues, String indexName) {
    if (newIndexValues.containsKey(indexName)) {
      return newIndexValues.get(indexName);
    }

    Map<Object, List<V>> map = new HashMap<>();
    newIndexValues.put(indexName, map);
    return map;
  }

  /**
   *
   */
  private Map<Object, V> loadUniqueIndex(@NotNull Map<String, Map<Object, V>> newUniqueValues, String indexName) {
    if (newUniqueValues.containsKey(indexName)) {
      return newUniqueValues.get(indexName);
    }

    Map<Object, V> map = new HashMap<>();
    newUniqueValues.put(indexName, map);
    return map;
  }
}
