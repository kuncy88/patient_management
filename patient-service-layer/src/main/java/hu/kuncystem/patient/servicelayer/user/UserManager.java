package hu.kuncystem.patient.servicelayer.user;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import hu.kuncystem.patient.pojo.user.User;

/**
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2017. nov. 9.
 * 
 * @version 1.0
 */
public interface UserManager {

    /**
     * Create new user in the database.
     * 
     * @param name
     *            This is the user's name.
     * @param password
     *            This is the user's password.
     * @param active
     *            This is a flag that the user's state is enable or disable.
     * @return It will return new User POJO object for success otherwise it will
     *         return null.
     */
    @Transactional
    public User createUser(String name, String password, boolean active);

    /**
     * Create new user in the database.
     *
     * @param name
     *            This is the user's name.
     * @param password
     *            This is the user's password.
     * @param active
     *            This is a flag that the user's state is enable or disable.
     * @param fullname
     *            This is the user's full name(surname and firstname).
     * @param email
     *            This is the user's valid email address.
     * @return It will return new User POJO object for success otherwise it will
     *         return null.
     */
    @Transactional
    public User createUser(String name, String password, boolean active, String fullname, String email);

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
     */
    public List<User> getAllUsers(int limit, int offset, String order);

    /**
     * Get one user's data from the database.
     *
     * @param id
     *            This is the unique user id. This id identifies one row in User
     *            table.
     * @return Object.User object is a simple POJO object. This method return
     *         null if the user isn't found in the database.
     */
    public User getUser(long id);

    /**
     * Get one user's data from the database. The name and the password can
     * identify one row, too.
     *
     * @param name
     *            This is the user's name.
     * @return Object.User object is a simple POJO object. This method return
     *         null if the user isn't found in the database.
     */
    public User getUser(String name);

    /**
     * Get one user's data from the database. The name and the password can
     * identify one row, too.
     *
     * @param name
     *            This is the user's name.
     * @param password
     *            This is the user's password.
     * @return Object.User object is a simple POJO object. This method return
     *         null if the user isn't found in the database.
     */
    public User getUser(String name, String password);

    /**
     * Delete one row from the database.
     *
     * @param userId
     *            This is the unique user id. This id identifies one row in User
     *            table. We will delete this row.
     * @return It will return true for success otherwise it will return false.
     */
    @Transactional
    public boolean removeUser(long userId);

    /**
     * Update one row in the database.
     *
     * @param userId
     *            This is the unique user id. This id identifies one row in User
     *            table. We will update this row.
     * @param name
     *            This is the user's name.
     * @param password
     *            This is the user's password.
     * @param active
     *            This is a flag that the user's state is enable or disable.
     * @param fullname
     *            This is the user's full name(surname and firstname).
     * @param email
     *            This is the user's valid email address.
     * @return will return true for success otherwise it will return false.
     */
    @Transactional
    public boolean updateUser(long userId, String name, String password, boolean active, String fullname, String email);
}
