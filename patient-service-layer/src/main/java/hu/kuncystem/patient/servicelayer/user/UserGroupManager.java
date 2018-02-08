package hu.kuncystem.patient.servicelayer.user;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import hu.kuncystem.patient.pojo.user.User;
import hu.kuncystem.patient.pojo.user.UserGroup;

/**
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2017. nov. 9.
 * 
 * @version 1.0
 */
public interface UserGroupManager {

    /**
     * Create new user group in the database.
     *
     * @param name
     *            This is the name of group.
     * @return It will return new pojo object for success otherwise it will
     *         return null.
     */
    @Transactional
    public UserGroup createGroup(String name);

    /**
     * Create new user group in the database.
     *
     * @param name
     *            This is the name of group.
     * @param note
     *            This is a short description of group.
     * @return It will return new pojo object for success otherwise it will
     *         return null.
     */
    @Transactional
    public UserGroup createGroup(String name, String note);

    /**
     * Get one data of group from the database.
     *
     * @param id
     *            This is the unique group id. This id identifies one row in
     *            UserGroup table.
     * @return Object.UserGroup object is a simple POJO object. This method
     *         return null if the group isn't found in database.
     */
    public UserGroup getGroup(long id);

    /**
     * This method returns all groups of a user.
     *
     * @param userId
     *            This is the unique user id. This id identifies one row in User
     *            table.
     * @return Object.List object which contains UserGroup objects.
     */
    public List<UserGroup> getGroupOfUser(long userId);

    /**
     * Return all users who are in this group.
     *
     * @param groupId
     *            This is the unique group id. This id identifies one row in
     *            UserGroup table.
     * @return Object.List object which contains User objects.
     */
    public List<User> getUsersFromGroup(long groupId);

    /**
     * Create a relation between a group and a user.
     *
     * @param userId
     *            This is the unique user id. This id identifies one row in User
     *            table.
     * @param groupId
     *            This is the unique group id. This id identifies one row in
     *            UserGroup table.
     * @return will return true for success otherwise it will return false.
     */
    @Transactional
    public boolean saveRelation(long userId, long groupId);

    /**
     * Create a relation between a group and a user.
     *
     * @param userId
     *            This is the unique user id. This id identifies one row in User
     *            table.
     * @param groupList
     *            This is the list of group name.
     * @return will return true for success otherwise it will return false.
     */
    @Transactional
    public boolean saveRelation(long userId, List<String> groupList);
}
