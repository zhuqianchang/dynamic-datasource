package indi.zqc.dynamic.datasource.service;

import indi.zqc.dynamic.datasource.model.User;

/**
 * @author Zhu.Qianchang
 * @date 2019/11/8.
 */
public interface UserService {

    User getUser(int id);

    void insertUser(User user);

    void updateUser(User user);
}
