package hu.kuncystem.patient.servicelayer.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hu.kuncystem.patient.dao.exception.DatabaseException;
import hu.kuncystem.patient.dao.user.UserGroupDao;
import hu.kuncystem.patient.pojo.user.User;
import hu.kuncystem.patient.pojo.user.UserFactory;
import hu.kuncystem.patient.pojo.user.UserGroup;

/**
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2017. nov. 9.
 * 
 * @version 1.0
 */
@Service("defaultUserGroupManager")
@Scope("prototype")
public class DefaultUserGroupManager implements UserGroupManager {
    @Autowired
    @Qualifier(value = "JDBCUserGroupDao")
    private UserGroupDao userGroupDao;

    private final UserFactory userFactory;

    /**
     * This class manages all of the data of a group. We can reach some
     * operation functions through the class. We can create, update or delete
     * one data of the group, too.
     *
     */
    public DefaultUserGroupManager() {
        userFactory = new UserFactory();
    }

    public UserGroup createGroup(String name) {
        return this.createGroup(name, null);
    }

    public UserGroup createGroup(String name, String note) {
        // create new POJO obejct
        UserGroup group = new UserGroup(name);
        group.setNote(note);

        try {
            group = userGroupDao.saveUserGroup(group);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        return group;
    }

    public UserGroup getGroup(long id) {
        return userGroupDao.getUserGroup(id);
    }

    public List<UserGroup> getGroupOfUser(long userId) {
        // create new user POJO object
        User user = userFactory.getUser(UserFactory.DEFAULT);
        user.setId(userId);

        return userGroupDao.getAllUserGroupByUser(user);
    }

    public List<User> getUsersFromGroup(long groupId) {
        UserGroup group = new UserGroup(groupId);

        return userGroupDao.getAllUserFromGroup(group);
    }

    @Transactional
    public boolean saveRelation(long userId, List<String> groupList) {
        boolean ok = true;

        UserGroup group;
        if (groupList != null) {
            for (String groupName : groupList) {
                group = userGroupDao.getUserGroup(groupName);
                // the group not found
                if (group == null) {
                    ok = false;
                    break;
                } else {
                    ok = this.saveRelation(userId, group.getId());
                }
                // itt happend an error
                if (ok == false) {
                    break;
                }
            }
        }
        return ok;

    }

    public boolean saveRelation(long userId, long groupId) {
        // user POJO object
        User user = userFactory.getUser(UserFactory.DEFAULT);
        user.setId(userId);

        // group POJO object
        UserGroup group = new UserGroup(groupId);

        try {
            return userGroupDao.saveUserGroupRelation(group, user);
        } catch (DatabaseException e) {
            return false;
        }
    }

}
