package hu.kuncystem.patient.dao.user;

import java.util.Date;
import java.util.List;

import hu.kuncystem.patient.dao.exception.DatabaseException;
import hu.kuncystem.patient.pojo.user.User;

/**
 * This interface defines the standard operations to be performed on a model
 * object(s).
 *
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2017. nov. 9.
 * 
 * @version 1.0
 */
public interface UserDao {
    /**
     * Delete one row from the database.
     *
     * @param user
     *            Object.User is a simple POJO object.
     * @return It will return true for success otherwise it will return false.
     * @throws DatabaseException
     *             if there is any problem issuing the update
     */
    public boolean deleteUser(User user) throws DatabaseException;

    /**
     * Get all of users from the database without condition.
     *
     * @param limit
     *            How many users do we want to get. If it is -1 then we won't
     *            use the limit.
     * @param offset
     *            Where from we want to get. If it is -1 then we won't use the
     *            offset.
     * @param order
     *            This is the column which we can order the result.
     * @return Object.List object which contains User objects.
     * @throws DatabaseException
     *             if the query fails
     */
    public List<User> getAllUsers(int limit, int offset, String order);

    /**
     * Get all of users by usernamer or fullname who are free on required time.
     * It will get just those user who are in the group which we add in param.
     *
     * @param filter
     *            Username filter.
     * @param group
     *            From which we select the users
     * @param date
     *            Required time where the user haven't insterted yet.
     * 
     * @return Object.List object which contains User objects.
     * @throws DatabaseException
     *             if the query fails
     */
    public List<User> getFreeUsersByNameFromGroup(String filter, String group, Date date) throws DatabaseException;

    /**
     * Get one row from the database.
     *
     * @param id
     *            Unique identification of the row.
     * @return Object.User is a simple POJO object.
     * @throws DatabaseException
     *             if the query fails
     */
    public User getUser(long id) throws DatabaseException;

    /**
     * Get one row from the database. Both parameters together identify one user
     * in the database.
     *
     * @param name
     *            This is the user's name.
     * @return Object.User is a simple POJO object.
     * @throws DatabaseException
     *             if the query fails
     */
    public User getUser(String name) throws DatabaseException;

    /**
     * Get one row from the database. Both parameters together identify one user
     * in the database.
     *
     * @param name
     *            This is the user's name.
     * @param password
     *            This is the user's password.
     * @return Object.User is a simple POJO object.
     * @throws DatabaseException
     *             if the query fails
     */
    public User getUser(String name, String password) throws DatabaseException;

    /**
     * Get all of users from the database which matches the pattern. This
     * pattern will filter the username or the fullname.
     *
     * @param query
     *            This is the filter string which we filter the user list.
     * 
     * @return Object.List object which contains User objects.
     * @throws DatabaseException
     *             if the query fails
     */
    public List<User> getUsersFilterByName(String query) throws DatabaseException;

    /**
     * Insert new user into the database.
     *
     * @param user
     *            Object.User is a simple POJO object(Patient or Doctor model
     *            object).
     * @return This is a simple POJO that we added in parameter and this object
     *         contains the new id that we got from the database.
     * @throws DatabaseException
     *             if there is any problem issuing the update
     */
    public User saveUser(User user) throws DatabaseException;

    /**
     * Update one row in the database.
     *
     * @param user
     *            Object.User is a simple POJO object.
     * @return It will return true for success otherwise it will return false.
     * @throws DatabaseException
     *             if there is any problem issuing the update
     */
    public boolean updateUser(User user) throws DatabaseException;
}
