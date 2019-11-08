package indi.zqc.dynamic.datasource.mapper;

import indi.zqc.dynamic.datasource.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Zhu.Qianchang
 * @date 2019/11/8.
 */
@Mapper
public interface UserDao {

    User getUser(@Param("id") int id);

    void insertUser(User user);

    void updateUser(User user);
}
