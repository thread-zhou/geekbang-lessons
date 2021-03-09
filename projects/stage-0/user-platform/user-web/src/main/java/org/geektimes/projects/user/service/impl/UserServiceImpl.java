package org.geektimes.projects.user.service.impl;

import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.repository.DatabaseUserRepository;
import org.geektimes.projects.user.repository.UserRepository;
import org.geektimes.projects.user.service.UserService;
import org.geektimes.projects.user.sql.DBConnectionManager;
import org.geektimes.web.core.ComponentContextFactory;

import java.sql.*;
import java.util.ServiceLoader;

/**
 * @ClassName: UserServiceImpl
 * @Description: 用户服务
 * @author: zhoujian
 * @date: 2021/3/1 21:15
 * @version: 1.0
 */
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    public UserServiceImpl(){
//        this.userRepository = ComponentContextFactory.getComponentContext().getComponent("bean/UserRepository");
    }

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean register(User user) {
        return userRepository.save(user);
    }

    @Override
    public boolean deregister(User user) {
        return false;
    }

    @Override
    public boolean update(User user) {
        return false;
    }

    @Override
    public User queryUserById(Long id) {
        return null;
    }

    @Override
    public User queryUserByNameAndPassword(String name, String password) {
        return null;
    }

    private void init(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
//        statement.execute(DBConnectionManager.DROP_USERS_TABLE_DDL_SQL);
//        statement.execute(DBConnectionManager.CREATE_USERS_TABLE_DDL_SQL);
    }
}
