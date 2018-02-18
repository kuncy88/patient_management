package hu.kuncystem.patient.servicelayer.appointment;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import hu.kuncystem.patient.dao.appointment.AppointmentDao;
import hu.kuncystem.patient.pojo.appointment.Appointment;
import hu.kuncystem.patient.pojo.user.User;
import hu.kuncystem.patient.pojo.user.UserGroup;
import hu.kuncystem.patient.servicelayer.exception.AppointmentNotExistsException;
import hu.kuncystem.patient.servicelayer.exception.AppointmentReservedException;
import hu.kuncystem.patient.servicelayer.exception.SessionNotExistsException;
import hu.kuncystem.patient.servicelayer.session.SessionManager;
import hu.kuncystem.patient.servicelayer.user.UserGroupManager;
import hu.kuncystem.patient.servicelayer.user.UserManager;
import hu.kuncystem.patient.servicelayer.utilities.Hash;

/**
 * Testing the ScheduleManager methods
 *
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2018. jan. 7.
 * 
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/beans_settings.xml" })
@ActiveProfiles("live")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ScheduleManagerTest {

    private static User doctor;

    private static User patient1;

    private static User patient2;

    private static Appointment test1Appointment;

    @Autowired
    @Qualifier("defaultScheduleManager")
    private ScheduleManager scheduleManager;

    @Autowired
    @Qualifier("defaultSessionManager")
    private SessionManager sessionManager;

    @Autowired
    @Qualifier("defaultUserManager")
    private UserManager userManager;

    @Autowired
    @Qualifier("defaultUserGroupManager")
    private UserGroupManager userGroupManager;

    @Test
    public void stage1_init() {
        UserGroup groupPatient = userGroupManager.createGroup("Patient", "");
        UserGroup groupDoctor = userGroupManager.createGroup("Doctor", "");

        doctor = userManager.createUser("Doctor1", Hash.MD5("123456"), true);
        userGroupManager.saveRelation(doctor.getId(), groupDoctor.getId());
        doctor = userManager.getUser(doctor.getId());

        patient1 = userManager.createUser("Patient1", Hash.MD5("123456"), true);
        userGroupManager.saveRelation(patient1.getId(), groupPatient.getId());
        patient1 = userManager.getUser(patient1.getId());

        patient2 = userManager.createUser("Patient2", Hash.MD5("123456"), true);
        userGroupManager.saveRelation(patient2.getId(), groupPatient.getId());
        patient2 = userManager.getUser(patient2.getId());

        // destroy old session(if exists)
        try {
            sessionManager.destroy();
        } catch (SessionNotExistsException e) {
            // we haven't session(do something)
        }
        // create new session proccess
        sessionManager.createSession(doctor.getId(), "127.0.0.1");
    }

    @Test
    public void stage2_schouldCreateAppointmentSuccessfullyWhenTimeFree() {
        Date date = null;
        Date date2 = null;
        try {
            date = new SimpleDateFormat(AppointmentDao.DATE_FORMAT).parse("2018-01-07 09:00:00");
            test1Appointment = scheduleManager.createAppointment(doctor.getId(), patient1.getId(), date, "", null);

            date2 = new SimpleDateFormat(AppointmentDao.DATE_FORMAT).parse("2018-01-07 09:15:00");
            scheduleManager.createAppointment(doctor.getId(), patient2.getId(), date2, "", null);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Test
    public void stage3_schouldCreateAppointmentUnSuccessfulyBecauseTimeReserved() {
        try {
            Date date = new SimpleDateFormat(AppointmentDao.DATE_FORMAT).parse("2018-01-07 09:25:00");

            scheduleManager.createAppointment(doctor.getId(), patient1.getId(), date, "", null);
            fail("The appointment wasn't reserved by user");
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (AppointmentReservedException e) {
            assertTrue(true);
        }
    }

    @Test
    public void stage4_schouldReScheduleSuccessfullyIfAppointmentExists() {
        Date srcDate = null;
        Date targetDate = null;
        try {
            srcDate = new SimpleDateFormat(AppointmentDao.DATE_FORMAT).parse("2018-01-07 09:00:00");
            targetDate = new SimpleDateFormat(AppointmentDao.DATE_FORMAT).parse("2018-01-07 10:15:00");

            scheduleManager.reScheduleAppointment(doctor.getId(), srcDate, targetDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void stage5_schouldReScheduleUnSuccessfullyBecauseAppointmentReserved() {
        Date srcDate = null;
        Date targetDate = null;
        try {
            srcDate = new SimpleDateFormat(AppointmentDao.DATE_FORMAT).parse("2018-01-07 10:15:00");
            targetDate = new SimpleDateFormat(AppointmentDao.DATE_FORMAT).parse("2018-01-07 09:10:00");

            scheduleManager.reScheduleAppointment(doctor.getId(), srcDate, targetDate);
            fail("Reservation failed: The appointment wasn't reserved by user");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (AppointmentReservedException e) {
            assertTrue(true);
        }
    }

    @Test
    public void stage6_schouldReScheduleUnSuccessfullyBecauseAppointmentDidNotExists() {
        Date srcDate = null;
        Date targetDate = null;
        try {
            srcDate = new SimpleDateFormat(AppointmentDao.DATE_FORMAT).parse("2018-10-26 12:15:00");
            targetDate = new SimpleDateFormat(AppointmentDao.DATE_FORMAT).parse("2018-01-10 11:30:00");

            if (scheduleManager.reScheduleAppointment(doctor.getId(), srcDate, targetDate)) {
                fail("The appointment exists.");
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (AppointmentNotExistsException e) {
            assertTrue(true);
        }
    }

    @Test
    public void stage61_schouldReScheduleDayAnDayToOtherDay() {
        Date date = null;
        Date date2 = null;
        DateFormat formatter = new SimpleDateFormat(AppointmentDao.DATE_FORMAT_WITHOUT_TIME);
        try {
            date = new SimpleDateFormat(AppointmentDao.DATE_FORMAT).parse("2018-03-07 09:00:00");
            scheduleManager.createAppointment(doctor.getId(), patient1.getId(), date, "", null);

            date2 = new SimpleDateFormat(AppointmentDao.DATE_FORMAT).parse("2018-03-07 10:15:00");
            scheduleManager.createAppointment(doctor.getId(), patient2.getId(), date2, "", null);

            boolean ok = scheduleManager.reScheduleDay(doctor.getId(), formatter.parse("2018-03-07"),
                    formatter.parse("2018-03-10"));
            assertTrue(ok);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail("hooooops! date format is not correct!");
        }
    }

    @Test
    public void stage62_schouldReScheduleDayUnsuccessfullyBecauseAnApointmentNotFree() {
        Date date = null;
        Date date2 = null;
        DateFormat formatter = new SimpleDateFormat(AppointmentDao.DATE_FORMAT_WITHOUT_TIME);
        try {
            date = new SimpleDateFormat(AppointmentDao.DATE_FORMAT).parse("2018-05-07 09:00:00");
            scheduleManager.createAppointment(doctor.getId(), patient1.getId(), date, "", null);

            date2 = new SimpleDateFormat(AppointmentDao.DATE_FORMAT).parse("2018-05-07 10:15:00");
            scheduleManager.createAppointment(doctor.getId(), patient2.getId(), date2, "", null);

            date2 = new SimpleDateFormat(AppointmentDao.DATE_FORMAT).parse("2018-05-10 10:15:00");
            scheduleManager.createAppointment(doctor.getId(), patient1.getId(), date2, "", null);

            boolean ok = scheduleManager.reScheduleDay(doctor.getId(), formatter.parse("2018-05-07"),
                    formatter.parse("2018-05-10"));
            assertTrue(!ok);
        } catch (ParseException e) {
            fail("hooooops! date format is not correct!");
        } catch (AppointmentReservedException e) {
            assertTrue(true);
        }
    }

    @Test
    public void stage7_schouldUpdateSuccessfullyIfExistsAppointment() {
        List<String> list = new ArrayList<String>();
        list.add("tag1");
        list.add("tag2");
        assertTrue(scheduleManager.updateAppointment(test1Appointment.getId(), doctor.getId(), patient1.getId(),
                "this is a new description", list));
    }

    @Test
    public void stage8_schouldUpdateUnSuccessfullyBecauseAppointmentDidNotExists() {
        try {
            scheduleManager.updateAppointment(1111, doctor.getId(), patient1.getId(), "this is a new2 description",
                    null);
            fail("The appointment exists still.");
        } catch (AppointmentNotExistsException e) {
            assertTrue(true);
        }
    }

    @Test
    public void stage9_schouldDeleteSuccessfullyIfAppointmentExists() {
        assertTrue(scheduleManager.remove(test1Appointment.getId()));
    }

}
