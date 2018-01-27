package hu.kuncystem.patient.dao.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import hu.kuncystem.patient.dao.exception.DatabaseException;
import hu.kuncystem.patient.pojo.user.User;
import hu.kuncystem.patient.pojo.user.UserFactory;

/**
 * Create a JDBC dao object which defines standard operations on a data source.
 * This data source belong to data of user.
 *
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2017. nov. 9.
 * 
 * @version 1.0
 */
@Repository
public class JDBCUserDao implements UserDao {

    public static class UserRowMapper implements RowMapper<User> {
        private final UserFactory userFactory = new UserFactory();

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet,
         * int)
         */
        public User mapRow(ResultSet rs, int row) throws SQLException {
            // get an object of User by group name
            User u = userFactory.getUser(rs.getString("group_name").toUpperCase());

            u.setUserName(rs.getString("user_name"));
            u.setPassword(rs.getString("passw"));
            u.setActive(rs.getBoolean("active"));
            u.setId(rs.getInt("id"));
            u.setEmail(rs.getString("email"));
            u.setFullname(rs.getString("fullname"));

            return u;
        }

    }

    private static final String SQL_FIND_BY_ID = "SELECT " + "u.*, COALESCE(ug.name,'') AS group_name "
            + "FROM users u " + "LEFT JOIN user_group_relation ugr ON (ugr.users_id = u.id)"
            + "LEFT JOIN user_group ug ON (ug.id = ugr.user_group_id AND ug.name IN ('Patient','Doctor'))"
            + "WHERE u.id = ?;";

    private static final String SQL_FIND = "SELECT " + "u.*, COALESCE(ug.name,'') AS group_name " + "FROM users u "
            + "LEFT JOIN user_group_relation ugr ON (ugr.users_id = u.id)"
            + "LEFT JOIN user_group ug ON (ug.id = ugr.user_group_id AND ug.name IN ('Patient','Doctor'))"
            + "WHERE u.user_name = ? AND u.passw = ?;";

    private static final String SQL_FIND_BY_NAME = "SELECT " + "u.*, COALESCE(ug.name,'') AS group_name "
            + "FROM users u " + "LEFT JOIN user_group_relation ugr ON (ugr.users_id = u.id)"
            + "LEFT JOIN user_group ug ON (ug.id = ugr.user_group_id AND ug.name IN ('Patient','Doctor'))"
            + "WHERE u.user_name = ?;";

    private static final String SQL_INSERT = "INSERT INTO users (user_name, passw, fullname, email) VALUES (?, ?, ?, ?);";

    private static final String SQL_UPDATE = "UPDATE users SET user_name = ?, passw = ?, fullname = ?, email = ?, active = ? WHERE id = ?;";

    private static final String SQL_DELETE = "DELETE FROM users WHERE id = ?;";

    @Autowired
    private JdbcOperations jdbc;

    public boolean deleteUser(User user) throws DatabaseException {
        try {
            int num = jdbc.update(SQL_DELETE, user.getId());
            return (num > 0) ? true : false;
        } catch (DataAccessException e) {
            throw new DatabaseException(DatabaseException.STRING_DATA_ACCESS_EXCEPTION + " " + SQL_DELETE, e);
        }
    }

    public User getUser(long id) throws DatabaseException {
        try {
            return jdbc.queryForObject(SQL_FIND_BY_ID, new UserRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new DatabaseException(DatabaseException.STRING_DATA_ACCESS_EXCEPTION + " " + SQL_FIND_BY_ID, e);
        }
    }

    public User getUser(String name) throws DatabaseException {
        try {
            return jdbc.queryForObject(SQL_FIND_BY_NAME, new UserRowMapper(), name);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new DatabaseException(DatabaseException.STRING_DATA_ACCESS_EXCEPTION + " " + SQL_FIND_BY_NAME, e);
        }
    }

    public User getUser(String name, String password) throws DatabaseException {
        try {
            return jdbc.queryForObject(SQL_FIND, new UserRowMapper(), name, password);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new DatabaseException(DatabaseException.STRING_DATA_ACCESS_EXCEPTION + " " + SQL_FIND, e);
        }
    }

    public User saveUser(final User user) throws DatabaseException {
        KeyHolder holder = new GeneratedKeyHolder();
        int rows = 0;
        try {
            rows = jdbc.update(new PreparedStatementCreator() {

                public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                    PreparedStatement ps = conn.prepareStatement(SQL_INSERT, new String[] { "id" });

                    ps.setString(1, user.getUserName());
                    ps.setString(2, user.getPassword());
                    ps.setString(3, user.getFullname());
                    ps.setString(4, user.getEmail());

                    return ps;
                }
            }, holder);
        } catch (DataAccessException e) {
            throw new DatabaseException(DatabaseException.STRING_DATA_ACCESS_EXCEPTION + " " + SQL_INSERT, e);
        }
        // if the operations was successed
        if (rows == 1) {
            user.setId(holder.getKey().longValue());
        } else {
            return null;
        }
        return user;
    }

    public boolean updateUser(User user) throws DatabaseException {
        try {
            int num = jdbc.update(SQL_UPDATE, user.getUserName(), user.getPassword(), user.getFullname(),
                    user.getEmail(), user.isActive(), user.getId());
            return (num > 0) ? true : false;
        } catch (DataAccessException e) {
            throw new DatabaseException(DatabaseException.STRING_DATA_ACCESS_EXCEPTION + " " + SQL_UPDATE, e);
        }
    }
}
