package com.openjdl.jsf.demo.data.mapper;

import com.openjdl.jsf.demo.data.args.user.SearchUserArgs;
import com.openjdl.jsf.demo.data.args.user.UpdateUserArgs;
import com.openjdl.jsf.demo.data.po.UserPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created at 2020-10-12 10:37:41
 *
 * @author kidal
 * @since 0.4
 */
public interface UserMapper {
  List<UserPo> getUsers(SearchUserArgs args);

  int countUser(SearchUserArgs args);

  void createUser(UserPo userPo);

  boolean updateUser(UpdateUserArgs args);

  boolean deleteUser(@Param("id") int id);
}
