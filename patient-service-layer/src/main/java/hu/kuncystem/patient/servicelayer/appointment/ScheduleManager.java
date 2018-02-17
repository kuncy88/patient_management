package hu.kuncystem.patient.servicelayer.appointment;

import java.util.Date;
import java.util.List;

import hu.kuncystem.patient.pojo.appointment.Appointment;

/**
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2017. nov. 9.
 * 
 * @version 1.0
 */
public interface ScheduleManager {

    /**
     * Add new appointment into the database.
     *
     * @param doctorId
     *            Identification of one doctor user.
     * @param patientId
     *            Identification of one patient user.
     * @param appointment
     *            Date of appointment when the user will meet her/his doctor.
     * @param description
     *            The meeting details.
     * @param notes
     *            Short tags of appointment.
     * @return Appointment POJO object which contains the new row id.
     */
    public Appointment createAppointment(long doctorId, long patientId, Date appointment, String description,
            List<String> notes);

    /**
     * Get one appointment row from the database.
     *
     * @param id
     *            The unique row identification of appointment
     * @return Object.Appointment is a simple POJO object.
     */
    public Appointment getAppointment(long appointmentId);

    /**
     * Get more rows from the data source. These rows contain the appointment of
     * the user between two dates.
     *
     * @param userId
     *            The user of id whose data we want to be queried.
     * @param from
     *            Begin of filter date. If this is null then the begin date is
     *            open.
     * @param to
     *            End of filter date. If this is null then the end date is open.
     * @return Object.List object which contain Appointment objects.
     */
    public List<Appointment> getAppointments(long userId, Date from, Date to);

    /**
     * The appointment is deleted from the data source.
     *
     * @param scheduleId
     *            It is a unique id of appointment in the data source.
     * @return It will return true for success otherwise it will return false.
     */
    public boolean remove(long scheduleId);

    /**
     * An appointment is copied to another day.
     *
     * @param userId
     *            Object.User is a simple POJO object. The user whose data we
     *            would like to copy.
     * @param srcDate
     *            the day of copy
     * @param targetDate
     *            target day
     * @return It will return true for success otherwise it will return false.
     */
    public boolean reScheduleAppointment(long userId, Date srcDate, Date targetDate);

    /**
     * Update an appointment in the database. Don't use this to change the time
     * of appointment. If you want to change the time then you have to use the
     * reScheduleAppointment method.
     *
     * @param appointmentId
     *            It is a unique id of appointment in the data source. We will
     *            update this row.
     * @param doctorId
     *            Identification of one doctor user.
     * @param patientId
     *            Identification of one patient user.
     * @param description
     *            The meeting details.
     * @param notes
     *            Short tags of appointment.
     * @return It will return true for success otherwise it will return false.
     */
    public boolean updateAppointment(long appointmentId, long doctorId, long patientId, String description,
            List<String> notes);
}
