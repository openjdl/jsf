package org.kidal.jsf.jdbc.mybatis.entity;

/**
 * Created at 2020-08-06 21:26:44
 *
 * @author kidal
 * @since 0.1.0
 */
public interface ListenedEntity {
  /**
   * 实体刚获取
   */
  default void onAfterSelect() {

  }

  /**
   * 实体即将更新
   */
  default void onBeforeUpdate() {

  }
}
