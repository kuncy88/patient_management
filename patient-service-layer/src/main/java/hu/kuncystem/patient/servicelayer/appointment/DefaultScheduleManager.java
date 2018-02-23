package hu.kuncystem.patient.servicelayer.appointment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import hu.kuncystem.patient.dao.appointment.AppointmentDao;
import hu.kuncystem.patient.dao.exception.DatabaseException;
import hu.kuncystem.patient.pojo.appointment.Appointment;
import hu.kuncystem.patient.pojo.user.User;
import hu.kuncystem.patient.pojo.user.UserFactory;
import hu.kuncystem.patient.servicelayer.exception.AppointmentNotExistsException;
import hu.kuncystem.patient.servicelayer.exception.AppointmentReservedException;
import hu.kuncystem.patient.servicelayer.session.SessionManager;
import hu.kuncystem.patient.servicelayer.user.UserManager;

/**
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2017. nov. 9.
 * 
 * @version 1.0
 */
@Service("defaultScheduleManager")
@Scope("prototype")
public class DefaultScheduleManager implements ScheduleManager {
    public final static int INTERVAL_MIN = 15;
    @Autowired
    @Qualifier("JDBCAppointmentDao")
    private AppointmentDao appointmentDao;

    @Autowired
    @Qualifier("defaultSessionManager")
    private SessionManager sessionManager;

    @Autowired
    @Qualifier("defaultUserManager")
    private UserManager userManager;

    private final UserFactory userFactory;

    /**
     * This class manages scheduling processes. Through the object we can add
     * new appointment, change and check the appointment time that can be
     * inserted under different conditions.
     *
     */
    public DefaultScheduleManager() {
        userFactory = new UserFactory();
    }

    public Appointment createAppointment(long doctorId, long patientId, Date date, String description,
            List<String> notes) throws AppointmentReservedException {
        User doctor = userFactory.getUser(UserFactory.DOCTOR);
        doctor.setId(doctorId);

        User patient = userFactory.getUser(UserFactory.PATIENT);
        patient.setId(patientId);

        Appointment appointment = new Appointment(doctor, patient, date);
        appointment.setDescripton(description);
        appointment.setNotes(notes);
        appointment.setSidid(sessionManager.getSession().getId());

        // check the appointment
        if (isEnableAppointment(doctorId, date)) {

            try {
                appointment = appointmentDao.saveAppointment(appointment);
            } catch (DatabaseException e) {
                e.printStackTrace();
                appointment.setId(0);
            }
        } else {
            throw new AppointmentReservedException();
        }

        return appointment;
    }

    public Appointment getAppointment(long appointmentId) {
        Appointment appointment = null;
        try {
            appointment = appointmentDao.getAppointment(appointmentId);
        } catch (DatabaseException e) {
            appointment = null;
        }

        return appointment;
    }

    public List<Appointment> getAppointments(long userId, Date from, Date to) {
        List<Appointment> appointmentList = null;

        try {
            // we use the UserManager interface so it will be unequivocal if the
            // user is doctor or patient.
            appointmentList = appointmentDao.getAppointments(userManager.getUser(userId), from, to);
        } catch (DatabaseException e) {
            appointmentList = new ArrayList<Appointment>();
        }

        return appointmentList;
    }

    /**
     * We can check an appointment that this is not reserved.
     * 
     * @param userId
     *            This is a user id who we want to check.
     * @param appointmentDate
     *            An datetime which we want to check.
     * 
     * @return It will return true if the appointment is free otherwise itt will
     *         return false.
     */
    private boolean isEnableAppointment(long userId, Date appointmentDate) {
        boolean ok = true;
        /*
         * This method create two dates. We can check these dates if the new
         * appointment date is free. The first date is decreased with the
         * INTERVAL_MIN variable. The second date is increased with the
         * INTERVAL_MIN variable.
         */

        Calendar cal = Calendar.getInstance();
        cal.setTime(appointmentDate);
        cal.add(Calendar.MINUTE, INTERVAL_MIN * -1);

        Date date1 = cal.getTime();

        cal.setTime(appointmentDate);
        cal.add(Calendar.MINUTE, INTERVAL_MIN);

        Date date2 = cal.getTime();

        // get all of the appointments between two dates
        // and we check the result.
        List<Appointment> appointmentList = this.getAppointments(userId, date1, date2);
        if (appointmentList.size() == 0) { // the appointment is free
            ok = true;
        } else if (appointmentList.size() == 1) {
            // there is one appointment in the database
            // create the different time between two dates(date from param and
            // date from database)
            long diff = TimeUnit.MINUTES.convert(
                    appointmentDate.getTime() - appointmentList.get(0).getTimet().getTime(), TimeUnit.MILLISECONDS);
            // use the abs method because the different may be negative
            // if the diff time is greater or equals with the INTERVAL time
            // then the date is free.
            ok = Math.abs(diff) >= INTERVAL_MIN;
        } else if (appointmentList.size() >= 3) {
            // we have too many appointment so date from param can't be free
            ok = false;
        } else {
            // we have two dates
            // the dates(from the database) are in order
            // if any dates is not equal with the negative or positive pair then
            // date from param can't be free
            if (appointmentDate.compareTo(appointmentList.get(0).getTimet()) == 0
                    || appointmentDate.compareTo(appointmentList.get(1).getTimet()) == 0
                    || date1.compareTo(appointmentList.get(0).getTimet()) != 0
                    || date2.compareTo(appointmentList.get(1).getTimet()) != 0) {
                ok = false;
            }
        }

        return ok;
    }

    public boolean remove(long scheduleId) {
        boolean ok = false;
        try {
            ok = appointmentDao.deleteAppointment(new Appointment(scheduleId));
        } catch (DatabaseException e) {
            ok = false;
        }

        return ok;
    }

    public boolean reScheduleAppointment(long userId, Date srcDate, Date targetDate)
            throws AppointmentReservedException, AppointmentNotExistsException {

        boolean ok = false;

        if (isEnableAppointment(userId, targetDate)) {
            List<Appointment> appointmentList = this.getAppointments(userId, srcDate, srcDate);
            if (appointmentList.size() == 1) {
                // add new data
                appointmentList.get(0).setTime(targetDate);
                appointmentList.get(0).setSidid(sessionManager.getSession().getId());

                // we don't want to update the notes list
                appointmentList.get(0).setNotes(null);
                try {
                    ok = appointmentDao.updateAppointment(appointmentList.get(0));
                } catch (DatabaseException e) {
                    ok = false;
                }
            } else if (appointmentList.size() > 1) {
                // there are too many appointments
                // We check this with the isEnableAppointment method, but later
                // we might use this.
            } else {
                // appointment not found, we can't update
                // this doesn't exists
                throw new AppointmentNotExistsException();
            }
        } else {
            throw new AppointmentReservedException();
        }

        return ok;
    }

    public boolean reScheduleDay(long userId, Date srcDate, Date targetDate)
            throws ParseException, AppointmentReservedException {
        DateFormat format = new SimpleDateFormat(AppointmentDao.DATE_FORMAT_WITHOUT_TIME);
        DateFormat newFormat = new SimpleDateFormat(AppointmentDao.DATE_FORMAT);
        DateFormat timeFormat = new SimpleDateFormat(AppointmentDao.TIME_FORMAT);

        String sourceDateString = format.format(srcDate);
        String targetDateString = format.format(targetDate);

        boolean ok = true;

        List<Appointment> appointmentList = this.getAppointments(userId,
                newFormat.parse(sourceDateString + " 00:00:00"), newFormat.parse(sourceDateString + " 23:59:59"));
        if (appointmentList.size() > 0) {
            Date newAppointmentDate;

            for (Appointment appointment : appointmentList) {
                newAppointmentDate = newFormat
                        .parse(targetDateString + " " + timeFormat.format(appointment.getTimet()));
                if (!isEnableAppointment(userId, newAppointmentDate)) {
                    throw new AppointmentReservedException();
                }

                appointment.setTime(newAppointmentDate);
                appointment.setNotes(null);
                appointment.setSidid(sessionManager.getSession().getId());
            }

            // all of appointment is free, we can update these to other day
            for (Appointment appointment : appointmentList) {
                try {
                    ok = appointmentDao.updateAppointment(appointment);
                    if (!ok) {
                        break;
                    }
                } catch (DatabaseException e) {
                    ok = false;
                    break;
                }
            }
        }

        return ok;
    }

    public boolean updateAppointment(long appointmentId, long doctorId, long patientId, String description,
            List<String> notes) throws AppointmentNotExistsException, AppointmentReservedException {

        boolean ok = false;
        Appointment appointment = this.getAppointment(appointmentId);
        if (appointment == null) {
            throw new AppointmentNotExistsException();
        } else {

            if (doctorId != appointment.getDoctor().getId() && !isEnableAppointment(doctorId, appointment.getTimet())) {
                throw new AppointmentReservedException();
            }

            User doctor = userFactory.getUser(UserFactory.DOCTOR);
            doctor.setId(doctorId);

            User patient = userFactory.getUser(UserFactory.PATIENT);
            patient.setId(patientId);

            appointment.setDoctor(doctor);
            appointment.setPatient(patient);
            appointment.setDescripton(description);
            appointment.setNotes(notes);
            appointment.setSidid(sessionManager.getSession().getId());

            try {
                ok = appointmentDao.updateAppointment(appointment);
            } catch (DatabaseException e) {
                e.printStackTrace();
                ok = false;
            }
        }

        return ok;
    }

}
