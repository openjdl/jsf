package com.openjdl.jsf.demo.service.user;

import com.openjdl.jsf.core.pagination.Page;
import com.openjdl.jsf.demo.data.args.user.SearchUserArgs;
import com.openjdl.jsf.demo.data.args.user.UpdateUserArgs;
import com.openjdl.jsf.demo.data.dao.UserDao;
import com.openjdl.jsf.demo.data.po.UserPo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;

/**
 * Created at 2020-10-12 15:45:22
 *
 * @author kidal
 * @since 0.4
 */
@Service
public class UserService {
  private final UserDao dao;

  public UserService(UserDao dao) {
    this.dao = dao;
  }

  @Nullable
  public UserPo getUser(int id) {
    SearchUserArgs args = new SearchUserArgs();
    args.withPageOne();
    args.setId(Collections.singletonList(id));
    return dao.getUsers(args).stream().findFirst().orElse(null);
  }

  @NotNull
  public Page<UserPo> getUserPage(@NotNull SearchUserArgs args) {
    return args.page(() -> dao.countUser(args), () -> dao.getUsers(args));
  }

  @NotNull
  public UserPo createUser(@NotNull UserPo userPo) {
    return dao.createUser(userPo);
  }

  @NotNull
  public UserPo updateUser(@NotNull UpdateUserArgs args) {
    dao.updateUser(args);
    return Objects.requireNonNull(getUser(args.getId()));
  }
}
