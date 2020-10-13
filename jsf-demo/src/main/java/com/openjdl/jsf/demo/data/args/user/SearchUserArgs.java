package com.openjdl.jsf.demo.data.args.user;

import com.google.common.collect.ImmutableSortedSet;
import com.openjdl.jsf.jdbc.mybatis.utils.BaseWhere;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created at 2020-10-12 10:38:41
 *
 * @author kidal
 * @since 0.4
 */
public class SearchUserArgs extends BaseWhere {
  public static final ImmutableSortedSet<String> SORTABLE = ImmutableSortedSet.of(
    "id",
    "username",
    "createdAt"
  );

  //--------------------------------------------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------------------------------------------

  private List<Integer> id;
  private List<String> username;

  //--------------------------------------------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------------------------------------------

  public SearchUserArgs() {
    setDefaultOrder("id", true);
  }

  @Override
  public void normalize() {
    id = normalize(id);
    username = normalize(username);
  }

  @Override
  public boolean canSort(@NotNull String field) {
    return SORTABLE.contains(field);
  }

  //--------------------------------------------------------------------------------------------------------------
  //
  //--------------------------------------------------------------------------------------------------------------

  public List<Integer> getId() {
    return id;
  }

  public void setId(List<Integer> id) {
    this.id = id;
  }

  public List<String> getUsername() {
    return username;
  }

  public void setUsername(List<String> username) {
    this.username = username;
  }
}
