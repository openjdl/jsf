package com.openjdl.jsf.core.exception;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created at 2020-09-12 15:04:34
 *
 * @author kidal
 * @since 0.3.1
 */
public class JsfExceptionDataContractStaticRegistry {
  private static final Map<Long, JsfExceptionDataContract> idMap = new HashMap<>();
  private static final Map<String, JsfExceptionDataContract> codeMap = new HashMap<>();

  public static synchronized void register(@NotNull JsfExceptionDataContract data) {
    idMap.put(data.getId(), data);
    codeMap.put(data.getCode(), data);
  }

  @NotNull
  public static synchronized Collection<JsfExceptionDataContract> values() {
    return Collections.unmodifiableCollection(idMap.values());
  }

  public static synchronized boolean has(long id) {
    return idMap.containsKey(id);
  }

  public static synchronized boolean has(@NotNull String code) {
    return codeMap.containsKey(code);
  }

  @Nullable
  public static synchronized JsfExceptionDataContract get(long id) {
    return idMap.get(id);
  }

  @Nullable
  public static synchronized JsfExceptionDataContract get(@NotNull String code) {
    return codeMap.get(code);
  }
}
