package hu.kuncystem.patient.dao.appointment;

import java.util.Date;
import java.util.List;

import hu.kuncystem.patient.dao.exception.DatabaseException;
import hu.kuncystem.patient.pojo.appointment.Appointment;
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
public interface AppointmentDao {
    // used date format
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String DATE_FORMAT_WITHOUT_TIME = "yyyy-MM-dd";

    /**
     * Delete one appointment row from the database.
     *
     * @param appointment
     *            Object.Appointment is a simple POJO object. This object
     *            contains the data which we have to delete.
     * @return It will return true for success otherwise it will return false.
     * @throws DatabaseException
     *             if there is any problem issuing the update
     */
    public boolean deleteAppointment(Appointment appointment) throws DatabaseException;

    /**
     * Get one row from the database.
     *
     * @param id
     *            The unique row identification of appointment
     * @return Object.Appointment is a simple POJO object.
     * @throws DatabaseException
     *             if the query fails
     */
    public Appointment getAppointment(long id) throws DatabaseException;

    /**
     * Get more rows from database. These rows contain the appointment of the
     * doctor between two dates.
     *
     * @param user
     *            Object.User is a simple POJO object. This object contains the
     *            data of user whose appointment is necessary. If it is null
     *            then we don't filter the user.
     * @param dateFrom
     *            Begin of filter date. If this is null then the begin date is
     *            open.
     * @param dateTo
     *            End of filter date. If this is null then the end date is open.
     * @return Object.List object that it is contain Appointment objects.
     * @throws DatabaseException
     *             if the query fails
     */
    public List<Appointment> getAppointments(User user, Date dateFrom, Date dateTo) throws DatabaseException;

    /**
     * Insert new appointment into the database.
     *
     * @param appointment
     *            Object.Appointment is a simple POJO object.
     * @return Object.Appointment is a simple POJO object. This object contains
     *         the unique id, too. It will return null if the save of notes was
     *         unsuccess.
     * @throws DatabaseException
     *             if there is any problem issuing the update
     */
    public Appointment saveAppointment(Appointment appointment) throws DatabaseException;

    /**
     * Update one appointment row in the database.
     *
     * @param appointment
     *            Object.Appointment is a simple POJO object. This object
     *            contains the data which we have to modify.
     * @return It will return true for success otherwise it will return false.
     * @throws DatabaseException
     *             if there is any problem issuing the update
     */
    public boolean updateAppointment(Appointment appointment) throws DatabaseException;
}
