package hu.kuncystem.patient.dao.session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import hu.kuncystem.patient.dao.exception.DatabaseException;
import hu.kuncystem.patient.pojo.session.Session;
import hu.kuncystem.patient.pojo.user.User;

/**
 * Create a JDBC dao object which defines standard operations on a data source.
 * This data source belong to data of session.
 *
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2017. nov. 9.
 * 
 * @version 1.0
 */
@Repository
public class JDBCSessionDao implements SessionDao {

    public static class SessionRowMapper implements RowMapper<Session> {
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet,
         * int)
         */
        public Session mapRow(ResultSet rs, int row) throws SQLException {

            Session s = new Session(rs.getLong("user_id"), rs.getString("ip"));
            s.setUserAgent(rs.getString("user_agent"));
            s.setDisabled(rs.getBoolean("disabled"));
            s.setId(rs.getLong("id"));

            return s;
        }

    }

    private static final String SQL_INSERT = "INSERT INTO session_list (user_id, ip, user_agent, disabled) VALUES (?, ?, ?, ?);";

    private static final String SQL_UPDATE = "UPDATE public.session_list SET user_id = ?, ip = ?, user_agent = ?, disabled = ? WHERE id = ?;";

    private static final String SQL_FIND_BY_ID = "SELECT * FROM session_list WHERE id = ?;";

    private static final String SQL_FIND_BY_USER = "SELECT * FROM session_list WHERE user_id = ?";

    @Autowired
    private JdbcOperations jdbc;

    public Session getSession(long sessionId) throws DatabaseException {
        try {
            return jdbc.queryForObject(SQL_FIND_BY_ID, new SessionRowMapper(), sessionId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new DatabaseException(DatabaseException.STRING_DATA_ACCESS_EXCEPTION + " " + SQL_FIND_BY_ID, e);
        }
    }

    public List<Session> getSession(User user, boolean justActive) {
        String sql = SQL_FIND_BY_USER + ((justActive) ? " AND NOT disabled;" : ";");

        try {
            return jdbc.query(sql, new SessionRowMapper(), user.getId());
        } catch (DataAccessException e) {
            throw new DatabaseException(DatabaseException.STRING_DATA_ACCESS_EXCEPTION + " " + sql, e);
        }
    }

    public Session saveSession(final Session session) throws DatabaseException {
        KeyHolder holder = new GeneratedKeyHolder();
        int rows = 0;
        try {
            rows = jdbc.update(new PreparedStatementCreator() {

                public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                    PreparedStatement ps = conn.prepareStatement(SQL_INSERT, new String[] { "id" });

                    ps.setLong(1, session.getUserId());
                    ps.setString(2, session.getIp());
                    ps.setString(3, session.getUserAgent());
                    ps.setBoolean(4, session.isDisabled());

                    return ps;
                }
            }, holder);
        } catch (DataAccessException e) {
            throw new DatabaseException(DatabaseException.STRING_DATA_ACCESS_EXCEPTION + " " + SQL_INSERT, e);
        }
        // if the operations was successed
        if (rows == 1) {
            session.setId(holder.getKey().longValue());
        } else {
            return null;
        }
        return session;
    }

    public boolean updateSession(final List<Session> sessions) throws DatabaseException {
        if (sessions != null) {
            try {
                int[] num = jdbc.batchUpdate(SQL_UPDATE, new BatchPreparedStatementSetter() {

                    public int getBatchSize() {
                        return sessions.size();
                    }

                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Session session = sessions.get(i);

                        ps.setLong(1, session.getUserId());
                        ps.setString(2, session.getIp());
                        ps.setString(3, session.getUserAgent());
                        ps.setBoolean(4, session.isDisabled());
                        ps.setLong(5, session.getId());
                    }
                });
                return (Arrays.stream(num).sum() > 0) ? true : false;
            } catch (DataAccessException e) {
                throw new DatabaseException(DatabaseException.STRING_DATA_ACCESS_EXCEPTION + " " + SQL_UPDATE, e);
            }
        } else {
            return false;
        }
    }

    public boolean updateSession(Session session) throws DatabaseException {
        List<Session> list = new ArrayList<Session>();
        list.add(session);

        return updateSession(list);
    }

}
