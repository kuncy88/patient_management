package hu.kuncystem.patient.dao.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import hu.kuncystem.patient.pojo.user.User;
import hu.kuncystem.patient.pojo.user.UserGroup;

/**
 * Create a JDBC dao object which defines standard operations on a data source. This data source belong to data of user group.
 *
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2017. nov. 9.
 *  
 * @version 1.0
 */
@Repository
public class JDBCUserGroupDao implements UserGroupDao {
	
	@Autowired
	private JdbcOperations jdbc;
	
	private static final String SQL_INSERT_GROUP = "INSERT INTO user_group (name, note) VALUES (?, ?); ";
	
	private static final String SQL_GET_USERS_FROM_GROUP = "SELECT "
		+ "u.*,COALESCE(ug.name,'') AS group_name "
	+ "FROM users u "
	+ "INNER JOIN user_group_relation ugr ON (ugr.users_id = u.id) "
	+ "LEFT JOIN user_group ug ON (ug.id = ugr.user_group_id AND ug.name IN ('Patient','Doctor')) "
	+ "WHERE ugr.user_group_id = ?;";
	
	private static final String SQL_GROUPS_BY_USER = "SELECT "
		+ "ug.* "
	+ "FROM user_group ug "
	+ "INNER JOIN user_group_relation ugr ON (ugr.user_group_id = ug.id) "
	+ "WHERE ugr.users_id = ?";
	
	private static final String SQL_INSERT_GROUP_RELATION = "INSERT INTO user_group_relation (user_group_id, users_id) VALUES (?,?);";
	
	private static final String SQL_FIND_GROUP_BY_ID = "SELECT * FROM user_group WHERE id = ?;";
	
	public UserGroup saveUserGroup(final UserGroup group) {
		KeyHolder holder = new GeneratedKeyHolder();
		
		int rows = jdbc.update(new PreparedStatementCreator() {
			
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				PreparedStatement ps = conn.prepareStatement(SQL_INSERT_GROUP, new String[]{"id"});
				
				ps.setString(1, group.getName());
				ps.setString(2, group.getNote());
				
				return ps;
			}
		}, holder);
		//if the operations was successed
		if(rows == 1){
			group.setId(holder.getKey().longValue());
			return group;
		}
		return null;
	}

	public UserGroup getUserGroup(long id) {
		return jdbc.queryForObject(SQL_FIND_GROUP_BY_ID, new UserGroupRowMapper(), id);
	}

	public boolean saveUserGroupRelation(final UserGroup group, final User user) {
		int rows = jdbc.update(new PreparedStatementCreator() {
			
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				PreparedStatement ps = conn.prepareStatement(SQL_INSERT_GROUP_RELATION);
				
				ps.setLong(1, group.getId());
				ps.setLong(2, user.getId());
				
				return ps;
			}
		});
		//if the operations was successed
		if(rows == 1){
			return true;
		}
		return false;
	}

	public List<User> getAllUserFromGroup(UserGroup group) {
		return jdbc.query(SQL_GET_USERS_FROM_GROUP, new JDBCUserDao.UserRowMapper(), group.getId());
	}

	public List<UserGroup> getAllUserGroupByUser(User user) {
		return jdbc.query(SQL_GROUPS_BY_USER, new UserGroupRowMapper(), user.getId());
	}

	private class UserGroupRowMapper implements RowMapper<UserGroup>{

		/* (non-Javadoc)
		 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
		 */
		public UserGroup mapRow(ResultSet rs, int row) throws SQLException {
			UserGroup group = new UserGroup(rs.getLong("id"));
			group.setName(rs.getString("name"));
			group.setNote(rs.getString("note"));
			
			return group;
		}
		
	}
}
