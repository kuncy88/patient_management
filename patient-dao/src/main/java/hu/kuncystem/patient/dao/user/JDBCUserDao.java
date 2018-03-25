package hu.kuncystem.patient.dao.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import hu.kuncystem.patient.dao.appointment.AppointmentDao;
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
            String group = rs.getString("group_name");
            if (group == null) {
                group = UserFactory.DEFAULT;
            }
            User u = userFactory.getUser(group.toUpperCase());

            u.setId(rs.getInt("id"));
            u.setUserName(rs.getString("user_name"));
            u.setPassword(rs.getString("passw"));
            u.setActive(rs.getBoolean("active"));
            u.setEmail(rs.getString("email"));
            u.setFullname(rs.getString("fullname"));

            DateFormat format = new SimpleDateFormat(AppointmentDao.DATE_FORMAT);
            try {
                u.setCreateDate(format.parse(rs.getString("created_date")));
            } catch (ParseException e) {
                u.setCreateDate(null);
            }

            return u;
        }

    }

    /**
     * This is the username field of the users table
     */
    public static final String ORDER_BY_USERNAME = "u.user_name";
    public static final String ORDER_BY_FULLNAME_ACTIVE = " u.active DESC, COALESCE(u.fullname, u.user_name)";

    private static final String SQL_FIND = "SELECT " + "u.*, COALESCE(MIN(ug.name), '') AS group_name "
            + "FROM users u " + "LEFT JOIN user_group_relation ugr ON (ugr.users_id = u.id)"
            + "LEFT JOIN user_group ug ON (ug.id = ugr.user_group_id AND ug.name IN ('Patient','Doctor')) "
            + "WHERE $CONDITION$ GROUP BY u.id;";

    private static final String SQL_FIND_ALL = "SELECT " + "u.*, GROUP_CONCAT(ug.name SEPARATOR ', ') AS group_name "
            + "FROM users u " + "LEFT JOIN user_group_relation ugr ON (ugr.users_id = u.id)"
            + "LEFT JOIN user_group ug ON (ug.id = ugr.user_group_id) " + "GROUP BY u.id";

    private static final String SQL_FIND_USER_FILTER_GROUP_AND_NAME = "SELECT u.*, ug.name AS group_name "
            + "FROM users u " + "INNER JOIN user_group_relation ugr ON (ugr.users_id = u.id) "
            + "INNER JOIN user_group ug ON (ug.id = ugr.user_group_id AND ug.name = ?) "
            + "LEFT JOIN appointment_table at ON (at.$FIELD$ = u.id AND appointment = ?) "
            + "WHERE LOWER(COALESCE(u.fullname, u.user_name)) LIKE ? AND at.id IS NULL "
            + "ORDER BY COALESCE(u.fullname, u.user_name);";

    private static final String SQL_INSERT = "INSERT INTO users (user_name, passw, fullname, email, active) VALUES (?, ?, ?, ?, ?);";

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

    public List<User> getAllUsers(int limit, int offset, String order) {
        String sql = SQL_FIND_ALL;
        if (order != null) {
            sql += " ORDER BY " + order;
        }
        if (limit > -1) {
            sql += " LIMIT " + limit;
        }
        if (offset > -1) {
            sql += " OFFSET " + offset;
        }
        try {
            return jdbc.query(sql, new UserRowMapper());
        } catch (DataAccessException e) {
            throw new DatabaseException(DatabaseException.STRING_DATA_ACCESS_EXCEPTION + " " + sql, e);
        }
    }

    public List<User> getFreeUsersByNameFromGroup(String filter, String group, Date date) {
        filter = "%" + filter + "%";
        // get the field name which we want to filter
        String field = "";
        if (group == "Doctor") { // if we select doctors
            field = "doctor_id";
        } else if (group == "Patient") {// if we select patients
            field = "patient_id";
        }

        DateFormat formatter = new SimpleDateFormat(AppointmentDao.DATE_FORMAT);

        // get free user in this "appointment time"
        String sql = SQL_FIND_USER_FILTER_GROUP_AND_NAME.replace("$FIELD$", field);
        try {
            return jdbc.query(sql, new UserRowMapper(), group, formatter.format(date), filter.toLowerCase());
        } catch (DataAccessException e) {
            throw new DatabaseException(DatabaseException.STRING_DATA_ACCESS_EXCEPTION + " " + sql, e);
        }
    }

    public User getUser(long id) throws DatabaseException {
        String sql = SQL_FIND.replace("$CONDITION$", "u.id = ?");
        try {
            return jdbc.queryForObject(sql, new UserRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new DatabaseException(DatabaseException.STRING_DATA_ACCESS_EXCEPTION + " " + sql, e);
        }
    }

    public User getUser(String name) throws DatabaseException {
        String sql = SQL_FIND.replace("$CONDITION$", "u.user_name = ?");
        try {
            return jdbc.queryForObject(sql, new UserRowMapper(), name);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new DatabaseException(DatabaseException.STRING_DATA_ACCESS_EXCEPTION + " " + sql, e);
        }
    }

    public User getUser(String name, String password) throws DatabaseException {
        String sql = SQL_FIND.replace("$CONDITION$", " u.user_name = ? AND u.passw = ?");
        try {
            return jdbc.queryForObject(sql, new UserRowMapper(), name, password);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new DatabaseException(DatabaseException.STRING_DATA_ACCESS_EXCEPTION + " " + sql, e);
        }
    }

    public List<User> getUsersFilterByName(String query) {
        query = "%" + query + "%";
        // inject the filter
        String sql = SQL_FIND.replace("$CONDITION$", " LOWER(COALESCE(u.fullname, u.user_name)) LIKE ?");
        // add order command
        sql = sql.replace(";", " ORDER BY COALESCE(u.fullname, u.user_name);");

        try {
            return jdbc.query(sql, new UserRowMapper(), query.toLowerCase());
        } catch (DataAccessException e) {
            throw new DatabaseException(DatabaseException.STRING_DATA_ACCESS_EXCEPTION + " " + sql, e);
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
                    ps.setBoolean(5, user.isActive());

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
