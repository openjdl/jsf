package org.kidal.jsf.core.pagination;

import org.jetbrains.annotations.NotNull;
import org.kidal.jsf.core.utils.callback.Action1;

/**
 * 遍历分页器
 *
 * @author kidal
 */
public class ForEachPager<T> {
  /**
   * 起始分页参数
   */
  private final PageArgs beginPageArgs;

  /**
   * 数据加载器
   */
  private final Action1<PageResults<T>, PageArgs> loader;

  /**
   *
   */
  public ForEachPager(@NotNull PageArgs beginPageArgs, @NotNull Action1<PageResults<T>, PageArgs> loader) {
    this.beginPageArgs = beginPageArgs;
    this.loader = loader;
  }

  public void forEach() {

  }
}
