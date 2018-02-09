package hu.kuncystem.patient.servicelayer.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import hu.kuncystem.patient.dao.exception.DatabaseException;
import hu.kuncystem.patient.dao.user.UserDao;
import hu.kuncystem.patient.pojo.user.User;
import hu.kuncystem.patient.pojo.user.UserFactory;

/**
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2017. nov. 9.
 * 
 * @version 1.0
 */
@Service
@Scope("prototype")
public class DefaultUserManager implements UserManager {

    @Autowired
    @Qualifier("JDBCUserDao")
    private UserDao userDao;

    private final UserFactory userFactory;

    /**
     * This class manages all of the user's data. We can reach some operation
     * functions through the class. We can create, update or delete one user�s
     * data, too.
     *
     */
    public DefaultUserManager() {
        userFactory = new UserFactory();
    }

    public User createUser(String name, String password, boolean active) {
        return this.createUser(name, password, active, null, null);
    }

    public User createUser(String name, String password, boolean active, String fullname, String email) {
        User user = userFactory.getUser(UserFactory.DEFAULT);
        user.setUserName(name);
        user.setPassword(password);
        user.setActive(active);
        user.setFullname(fullname);
        user.setEmail(email);

        try {
            user = userDao.saveUser(user);
        } catch (DatabaseException e) {
            e.printStackTrace();
            return null;
        }
        return user;
    }

    public List<User> getAllUsers(int limit, int offset, String order) {
        try {
            return userDao.getAllUsers(limit, offset, order);
        } catch (DatabaseException e) {
            return new ArrayList<User>();
        }
    }

    public User getUser(long id) {
        return userDao.getUser(id);
    }

    public User getUser(String name) {
        return userDao.getUser(name);
    }

    public User getUser(String name, String password) {
        return userDao.getUser(name, password);
    }

    public boolean removeUser(long userId) {
        User user = userFactory.getUser(UserFactory.DEFAULT);
        user.setId(userId);

        try {
            return userDao.deleteUser(user);
        } catch (DatabaseException e) {
            return false;
        }
    }

    public boolean updateUser(long userId, String name, String password, boolean active, String fullname,
            String email) {
        User user = userFactory.getUser(UserFactory.DEFAULT);
        user.setId(userId);
        user.setUserName(name);
        user.setPassword(password);
        user.setActive(active);
        user.setEmail(email);
        user.setFullname(fullname);

        try {
            return userDao.updateUser(user);
        } catch (DatabaseException e) {
            return false;
        }
    }

}
