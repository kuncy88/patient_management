package hu.kuncystem.patient.dao.user;

import java.util.List;

import hu.kuncystem.patient.dao.exception.DatabaseException;
import hu.kuncystem.patient.pojo.user.User;
import hu.kuncystem.patient.pojo.user.UserGroup;

/**
 * This interface defines the standard operations to be performed on a model
 * object(s).
 *
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2017. nov. 9.
 * 
 * @version 1.0
 */
public interface UserGroupDao {
    /**
     * Return all users who are in this group.
     *
     * @param user
     *            Object. UserGroup is a simple POJO object. This object
     *            contains the data of the group. Get a user list from this
     *            group.
     * @return Object.List object which contains User objects.
     * @throws DatabaseException
     *             if the query fails
     */
    public List<User> getAllUserFromGroup(UserGroup group) throws DatabaseException;

    /**
     * This method returns all groups of a user.
     *
     * @param user
     *            Object. User is a simple POJO object. This object contains
     *            data of a user.
     * @return Object.List object which contains UserGroup objects.
     * @throws DatabaseException
     *             if the query fails
     */
    public List<UserGroup> getAllUserGroupByUser(User user) throws DatabaseException;

    /**
     * Get a row from the database by id.
     *
     * @param id
     *            The unique value of a row in database.
     * @return Object.UserGroup is a simple POJO object.
     * @throws DatabaseException
     *             if the query fails
     */
    public UserGroup getUserGroup(long id) throws DatabaseException;

    /**
     * Get a row from the database by name.
     *
     * @param name
     *            The name of group which identify an row in the database.
     * @return Object.UserGroup is a simple POJO object.
     * @throws DatabaseException
     *             if the query fails
     */
    public UserGroup getUserGroup(String name) throws DatabaseException;

    /**
     * Insert new user group into the database.
     *
     * @param group
     *            Object.User is a simple POJO object.
     * @return This is a simple POJO that we added in parameter and this object
     *         contains the new id that we got from the database.
     * @throws DatabaseException
     *             if there is any problem issuing the update
     */
    public UserGroup saveUserGroup(UserGroup group) throws DatabaseException;

    /**
     * This method inserts a user into a new group.
     *
     * @param group
     *            Object.UserGroup is a simple POJO object. This is a new group
     *            of user.
     * @param user
     *            Object.User is a simple POJO object. This user who will insert
     *            into the group.
     * @return It will return true for success otherwise it will return false.
     * @throws DatabaseException
     *             if there is any problem issuing the update
     */
    public boolean saveUserGroupRelation(UserGroup group, User user) throws DatabaseException;
}
