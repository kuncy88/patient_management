package hu.kuncystem.patient.dao.appointment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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
import hu.kuncystem.patient.pojo.appointment.Appointment;
import hu.kuncystem.patient.pojo.user.User;
import hu.kuncystem.patient.pojo.user.UserFactory;

/**
 * Create a JDBC dao object which defines standard operations on a data source.
 * This data source belong to data of appointment.
 *
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2017. nov. 9.
 * 
 * @version 1.0
 */
@Repository
public class JDBCAppointmentDao implements AppointmentDao {

    public static class AppointmentRowMapper implements RowMapper<Appointment> {

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet,
         * int)
         */
        public Appointment mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserFactory userFactory = new UserFactory();

            // create simple user pojo object
            User doctor = userFactory.getUser(UserFactory.DOCTOR);
            doctor.setId(rs.getLong("doctor_id"));
            doctor.setUserName(rs.getString("doctor_user_name"));
            doctor.setPassword(rs.getString("doctor_passw"));
            doctor.setFullname(rs.getString("doctor_fullname"));
            doctor.setEmail(rs.getString("doctor_email"));
            doctor.setActive(rs.getBoolean("doctor_active"));

            // create simple user pojo object
            User patient = userFactory.getUser(UserFactory.PATIENT);
            patient.setId(rs.getLong("patient_id"));
            patient.setUserName(rs.getString("patient_user_name"));
            patient.setPassword(rs.getString("patient_passw"));
            patient.setFullname(rs.getString("patient_fullname"));
            patient.setEmail(rs.getString("patient_email"));
            patient.setActive(rs.getBoolean("patient_active"));

            // create appointment pojo object
            Appointment appointment = new Appointment(doctor, patient, rs.getTimestamp("appointment"));
            appointment.setId(rs.getLong("id"));
            appointment.setDescripton(rs.getString("description"));
            appointment.setSidid(rs.getLong("sid"));

            // check the notes that does it exist
            String notes = rs.getString("notes");
            if (notes != null) {
                appointment.setNotes(Arrays.asList(notes.split(",")));
            }
            return appointment;
        }

    }

    private static final String SQL_INSERT_APPOINTMENT = "INSERT INTO "
            + "appointment_table (doctor_id, patient_id, appointment, description, sid) " + "VALUES (?, ?, ?, ?, ?);";

    private static final String SQL_INSERT_APPOINTMENT_NOTES = "INSERT INTO "
            + "appointment_notes (appointment_id, note, sid) " + "VALUES (?, ?, ?);";

    // we use here an joker characters($1$), so we can use this sql more
    // place.(just, the sql condition is different)
    private static final String SQL_SELECT_APPOINTMENT = "SELECT " + "at.id," + "at.appointment," + "at.description,"
            + "at.sid," + "STRING_AGG(an.note, ',') AS notes," + "u1.id AS doctor_id,"
            + "u1.user_name AS doctor_user_name," + "u1.passw AS doctor_passw," + "u1.fullname AS doctor_fullname,"
            + "u1.email AS doctor_email," + "u1.active AS doctor_active," + "u2.id AS patient_id,"
            + "u2.user_name AS patient_user_name," + "u2.passw AS patient_passw," + "u2.fullname AS patient_fullname,"
            + "u2.email AS patient_email," + "u2.active AS patient_active " + "FROM appointment_table at "
            + "INNER JOIN users u1 ON (u1.id = at.doctor_id) " + "INNER JOIN users u2 ON (u2.id = at.patient_id) "
            + "LEFT JOIN appointment_notes an ON (an.appointment_id = at.id) " + " WHERE $1$ "
            + "GROUP BY at.id, u1.id, u2.id " + "ORDER BY at.appointment ASC;";

    private static final String SQL_DELETE_APPOINTMENT = "DELETE FROM appointment_table WHERE id = ?;";

    private static final String SQL_DELETE_APPOINTMENT_NOTES = "DELETE FROM appointment_notes WHERE appointment_id = ?;";

    private static final String SQL_UPDATE = "UPDATE appointment_table "
            + "SET doctor_id = ?, patient_id = ?, appointment = ?, description = ? WHERE id = ?;";

    @Autowired
    private JdbcOperations jdbc;

    public boolean deleteAppointment(Appointment appointment) throws DatabaseException {
        try {
            // delete appointment notes if these are exists
            jdbc.update(SQL_DELETE_APPOINTMENT_NOTES, appointment.getId());
        } catch (DataAccessException e) {
            throw new DatabaseException(
                    DatabaseException.STRING_DATA_ACCESS_EXCEPTION + " " + SQL_DELETE_APPOINTMENT_NOTES, e);
        }
        try {
            // delete appointment
            if (jdbc.update(SQL_DELETE_APPOINTMENT, appointment.getId()) == 0) {
                // not found the appointment by id
                return false;
            } else {
                return true;
            }
        } catch (DataAccessException e) {
            throw new DatabaseException(DatabaseException.STRING_DATA_ACCESS_EXCEPTION + " " + SQL_DELETE_APPOINTMENT,
                    e);
        }

    }

    public Appointment getAppointment(long id) throws DatabaseException {
        // replace the joker characters to concrete sql
        String sql = SQL_SELECT_APPOINTMENT.replace("$1$", "at.id = ?");
        try {
            return jdbc.queryForObject(sql, new AppointmentRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new DatabaseException(DatabaseException.STRING_DATA_ACCESS_EXCEPTION + " " + sql, e);
        }
    }

    public List<Appointment> getAppointments(User user, Date dateFrom, Date dateTo) throws DatabaseException {

        StringBuilder text = new StringBuilder();
        UserFactory userFactory = new UserFactory();

        // check the user type so we can filter(doctor or patient
        // appointment).
        if (user != null) {
            if (userFactory.isPatient(user)) {
                text.append("at.patient_id = ?");
            } else {
                text.append("at.doctor_id = ?");
            }
        }

        // date filter from if it is necessary
        if (dateFrom != null) {
            if (text.length() > 0) {
                text.append(" AND");
            }
            text.append(" at.appointment >= '" + new SimpleDateFormat(DATE_FORMAT).format(dateFrom) + "'");
        }
        // date filter to if it is necessary
        if (dateTo != null) {
            if (text.length() > 0) {
                text.append(" AND");
            }
            text.append(" at.appointment <= '" + new SimpleDateFormat(DATE_FORMAT).format(dateTo) + "'");
        }
        // replace the joker characters to concrete sql
        String sql = SQL_SELECT_APPOINTMENT.replace("$1$", text);
        try {
            if (user != null) {
                return jdbc.query(sql, new AppointmentRowMapper(), user.getId());
            } else {
                return jdbc.query(sql, new AppointmentRowMapper());
            }
        } catch (DataAccessException e) {
            throw new DatabaseException(DatabaseException.STRING_DATA_ACCESS_EXCEPTION + " " + sql, e);
        }
    }

    public Appointment saveAppointment(final Appointment appointment) throws DatabaseException {
        KeyHolder holder = new GeneratedKeyHolder();
        int rows = 0;
        try {
            rows = jdbc.update(new PreparedStatementCreator() {

                public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                    PreparedStatement ps = conn.prepareStatement(SQL_INSERT_APPOINTMENT, new String[] { "id" });

                    ps.setLong(1, appointment.getDoctor().getId());
                    ps.setLong(2, appointment.getPatient().getId());
                    ps.setString(3, new SimpleDateFormat(DATE_FORMAT).format(appointment.getTimet()));
                    ps.setString(4, appointment.getDescription());
                    ps.setLong(5, appointment.getSidid());

                    return ps;
                }
            }, holder);
        } catch (DataAccessException e) {
            throw new DatabaseException(DatabaseException.STRING_DATA_ACCESS_EXCEPTION + " " + SQL_INSERT_APPOINTMENT,
                    e);
        }
        if (rows > 0) {
            appointment.setId(holder.getKey().longValue());

            if (!this.saveAppointmentNote(appointment.getId(), appointment.getNotes(), appointment.getSidid())) {
                return null;
            } else {
                return appointment;
            }
        } else {
            return null;
        }
    }

    /**
     * Save the notes of appointment.
     * 
     * @param id
     *            The unique row identification of appointment
     * @param notes
     *            This is a list which contains all of the notes
     * @param sid
     *            User session id
     * @return It will return true for success otherwise it will return false.
     */
    private boolean saveAppointmentNote(final long id, final List<String> notes, final long sid) {

        if (notes != null) {
            try {
                jdbc.batchUpdate(SQL_INSERT_APPOINTMENT_NOTES, new BatchPreparedStatementSetter() {

                    public int getBatchSize() {
                        return notes.size();
                    }

                    public void setValues(PreparedStatement ps, int i) throws SQLException {

                        ps.setLong(1, id);
                        ps.setString(2, notes.get(i));
                        ps.setLong(3, sid);
                    }
                });
            } catch (DataAccessException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public boolean updateAppointment(Appointment appointment) throws DatabaseException {
        boolean ok = false;
        int num = 0;

        try {
            // first update the row of appointment
            num = jdbc.update(SQL_UPDATE, appointment.getDoctor().getId(), appointment.getPatient().getId(),
                    new SimpleDateFormat(DATE_FORMAT).format(appointment.getTimet()), appointment.getDescription(),
                    appointment.getId());
        } catch (DataAccessException e) {
            throw new DatabaseException(DatabaseException.STRING_DATA_ACCESS_EXCEPTION + " " + SQL_UPDATE, e);
        }

        if (num > 0) {
            ok = true;
            // delete and resave the notes
            if (appointment.getNotes() != null) {
                // delete old notes
                try {
                    jdbc.update(SQL_DELETE_APPOINTMENT_NOTES, appointment.getId());
                } catch (DataAccessException e) {
                    throw new DatabaseException(
                            DatabaseException.STRING_DATA_ACCESS_EXCEPTION + " " + SQL_DELETE_APPOINTMENT_NOTES, e);
                }

                // add new notes
                ok = saveAppointmentNote(appointment.getId(), appointment.getNotes(), appointment.getSidid());
            }
        } // otherwise the update was unsuccessful

        return ok;
    }

}
