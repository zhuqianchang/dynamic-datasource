package indi.zqc.dynamic.datasource.service.impl;

import indi.zqc.dynamic.datasource.annotation.DataSource;
import indi.zqc.dynamic.datasource.mapper.UserDao;
import indi.zqc.dynamic.datasource.model.User;
import indi.zqc.dynamic.datasource.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Zhu.Qianchang
 * @date 2019/11/8.
 */
@Transactional
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    /**
     * 注解方式访问数据源db1
     */
    @DataSource("db1")
    @Override
    public User getUser(int id) {
        return userDao.getUser(id);
    }

    /**
     * 注解方式访问数据源db2
     */
    @DataSource("db1")
    @Override
    public void insertUser(User user) {
        userDao.insertUser(user);
    }

    /**
     * 注解方式访问数据源db2
     */
    @DataSource("db2")
    @Override
    public void updateUser(User user) {
        userDao.updateUser(user);
    }
}
