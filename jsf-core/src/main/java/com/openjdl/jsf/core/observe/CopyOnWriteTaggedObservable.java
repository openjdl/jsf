package com.openjdl.jsf.core.observe;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;

import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created at 2020-09-10 14:57:21
 *
 * @author kidal
 * @since 0.3
 */
public class CopyOnWriteTaggedObservable implements TaggedObservable {
  /**
   * observers.
   */
  private final ConcurrentMap<String, CopyOnWriteArrayList<TaggedObserver>> observersMap;

  /**
   *
   */
  public CopyOnWriteTaggedObservable() {
    this.observersMap = new ConcurrentHashMap<>();
  }

  /**
   *
   */
  protected CopyOnWriteArrayList<TaggedObserver> getOrCreateObserverList(String tag) {
    CopyOnWriteArrayList<TaggedObserver> list = observersMap.get(tag);

    if (list == null) {
      CopyOnWriteArrayList<TaggedObserver> prev;

      list = new CopyOnWriteArrayList<>();
      prev = observersMap.putIfAbsent(tag, list);
      list = prev != null ? prev : list;
    }

    return list;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addObserver(@NotNull String tag, @NotNull TaggedObserver observer) {
    CopyOnWriteArrayList<TaggedObserver> list = getOrCreateObserverList(tag);
    list.addIfAbsent(observer);
    list.sort(Comparator.comparingInt(o -> o instanceof Ordered ? ((Ordered) o).getOrder() : 0));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteObserver(@NotNull String tag, @NotNull TaggedObserver observer) {
    getOrCreateObserverList(tag).remove(observer);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void notifyObservers(@NotNull String tag) {
    notifyObservers(tag, null);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void notifyObservers(@NotNull String tag, Object arg) {
    getOrCreateObserverList(tag).forEach(o -> o.onObservableChanged(this, tag, arg));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteObservers(@NotNull String tag) {
    getOrCreateObserverList(tag).clear();
  }
}
