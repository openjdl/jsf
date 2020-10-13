package com.openjdl.jsf.demo.data.dao;

import com.openjdl.jsf.demo.data.args.user.SearchUserArgs;
import com.openjdl.jsf.demo.data.args.user.UpdateUserArgs;
import com.openjdl.jsf.demo.data.mapper.UserMapper;
import com.openjdl.jsf.demo.data.po.UserPo;
import com.openjdl.jsf.jdbc.annotation.MasterDataSourceMapping;
import com.openjdl.jsf.jdbc.annotation.SlaveDataSourceMapping;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created at 2020-10-12 15:41:07
 *
 * @author kidal
 * @since 0.4
 */
@Component
public class UserDao {
  private final UserMapper mapper;

  public UserDao(UserMapper mapper) {
    this.mapper = mapper;
  }

  @SlaveDataSourceMapping
  public List<UserPo> getUsers(@NotNull SearchUserArgs args) {
    args.normalize();

    return mapper.getUsers(args);
  }

  @SlaveDataSourceMapping
  public int countUser(@NotNull SearchUserArgs args) {
    args.normalize();

    return mapper.countUser(args);
  }

  @MasterDataSourceMapping
  public UserPo createUser(@NotNull UserPo userPo) {
    mapper.createUser(userPo);

    return userPo;
  }

  @MasterDataSourceMapping
  public boolean updateUser(@NotNull UpdateUserArgs args) {
    return mapper.updateUser(args);
  }

  @MasterDataSourceMapping
  public boolean deleteUser(int id) {
    return mapper.deleteUser(id);
  }
}
