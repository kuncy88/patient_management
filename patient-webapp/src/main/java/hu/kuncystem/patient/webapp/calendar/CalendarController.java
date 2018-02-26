package hu.kuncystem.patient.webapp.calendar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import hu.kuncystem.patient.dao.appointment.AppointmentDao;
import hu.kuncystem.patient.pojo.appointment.Appointment;
import hu.kuncystem.patient.servicelayer.appointment.ScheduleManager;
import hu.kuncystem.patient.servicelayer.exception.AppointmentNotExistsException;
import hu.kuncystem.patient.servicelayer.exception.AppointmentReservedException;

/**
 * This controller handle the calendar process(request and response).
 *
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2018. jan. 31.
 * 
 * @version 1.0
 */
@Controller
public class CalendarController {

    @Autowired
    @Qualifier("defaultScheduleManager")
    private ScheduleManager scheduleManager;

    /**
     * Show the calendar
     */
    @RequestMapping(value = "/mycalendar", method = RequestMethod.GET)
    public String calendar() {
        return "mycalendar";
    }

    /**
     * Save or update an appointment in the database.
     * 
     * @param appointmentForm
     *            This object contains all of form data. These values will
     *            checked with annotation. This is a DTO object.
     * @param result
     *            We can check the result of the validation through the object.
     */
    @PostMapping(value = "/mycalendar/saveAppointment", produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public AppointmentSaveJsonRespone saveAppointment(@ModelAttribute @Valid AppointmentForm appointmentForm,
            BindingResult result) {

        AppointmentSaveJsonRespone response = new AppointmentSaveJsonRespone();
        
        if (result.hasErrors()) { // it was any error

            // Get all of error messages
            Map<String, String> errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

            response.setValidated(false);
            response.setErrorMessages(errors);
        } else { // the form data was valid
            Appointment appointment = null;
            List<String> notes = null;
            // create tags to the appointment from the text
            if (appointmentForm.getNotes() != null) {
                notes = Arrays.asList(appointmentForm.getNotes().split(" "));
            }
            DateFormat format = new SimpleDateFormat(AppointmentDao.DATE_FORMAT);
            try {
                boolean ok = true;
                if (appointmentForm.getAppointmentId() == null || appointmentForm.getAppointmentId() <= 0) {
                    // create new appointment
                    appointment = scheduleManager.createAppointment(appointmentForm.getDoctorId(),
                            appointmentForm.getPatientId(), format.parse(appointmentForm.getStartTime()),
                            appointmentForm.getDescription(), notes);
                    // check the success of operation
                    ok = (appointment.getId() > 0);
                    appointmentForm.setAppointmentId(appointment.getId());

                } else {
                    // update an appointment
                    DateFormat formatter = new SimpleDateFormat(AppointmentDao.DATE_FORMAT);
                    Date formatDate = formatter.parse(appointmentForm.getStartTime());

                    // get the current appointment data by id
                    appointment = this.getAppointment(appointmentForm.getAppointmentId());
                    // we have to change the appointment date, too
                    if (appointment.getTimet().compareTo(formatDate) != 0) {
                        try {
                            ok = scheduleManager.reScheduleAppointment(appointmentForm.getDoctorId(),
                                    appointment.getTimet(), formatDate);
                        } catch (AppointmentReservedException e) {
                            // this appointment is used by other user
                            response.setResult(AppointmentSaveJsonRespone.RESULT_RESERVED);
                            ok = false;
                        } catch (AppointmentNotExistsException e) {
                            ok = false;
                        }
                    }

                    if (ok) {
                        // if the reschedule was ok, then we can update the
                        // appointment data
                        ok = scheduleManager.updateAppointment(appointmentForm.getAppointmentId().longValue(),
                                appointmentForm.getDoctorId(), appointmentForm.getPatientId(),
                                appointmentForm.getDescription(), notes);
                    }
                }

                if (ok) {
                    // now we select the updated data from database and take
                    // back these
                    // we can check that it was everything ok
                    appointment = this.getAppointment(appointmentForm.getAppointmentId());
                    // everythings ok
                    response.setResult(AppointmentSaveJsonRespone.RESULT_OK);
                } else {
                    appointment = null;
                    // perhapes databse error
                    if (response.getResult() == null)
                        response.setResult(AppointmentSaveJsonRespone.RESULT_OTHER_ERROR);
                }
            } catch (ParseException e) {
                // the date format was not correct
                response.setResult(AppointmentSaveJsonRespone.RESULT_DATE_ERROR);
            } catch (AppointmentReservedException e) {
                // this appointment is used by other user
                response.setResult(AppointmentSaveJsonRespone.RESULT_RESERVED);
            }

            response.setValidated(true);
            response.setAppointment(appointment);
        }

        return response;
    }

    /**
     * Get appointments between two date
     * 
     * @param userId
     *            The user whose appointments we want to select
     * @param fromDate
     *            Begin of filter
     * @param fromDate
     *            End of filter
     */
    @PostMapping(value = "/mycalendar/getAppointmentList", produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public List<Appointment> getAppointmentList(@RequestParam(value = "userId") long userId,
            @RequestParam(value = "from") String fromDate, @RequestParam(value = "to") String toDate) {

        List<Appointment> response;
        DateFormat format = new SimpleDateFormat(AppointmentDao.DATE_FORMAT);

        try {
            response = scheduleManager.getAppointments(userId, format.parse(fromDate), format.parse(toDate));
            for (Appointment appointment : response) {
                // remove the password
                // we don't show these in the response
                appointment.getDoctor().setPassword(null);
                appointment.getPatient().setPassword(null);
            }
        } catch (ParseException e) {
            response = null;
            e.printStackTrace();
        }

        return response;
    }

    /**
     * Get an appointment data from database.
     * 
     * @param id
     *            The unique row id.
     */
    @PostMapping(value = "/mycalendar/getAppointment", produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public Appointment getAppointment(@RequestParam(value = "id") long id) {
        Appointment appointment = scheduleManager.getAppointment(id);

        // we don't show these in the response
        appointment.getDoctor().setPassword(null);
        appointment.getPatient().setPassword(null);

        return appointment;
    }

    /**
     * Remove an appointment from database.
     * 
     * @param id
     *            The unique row id.
     */
    @PostMapping(value = "/mycalendar/removeAppointment", produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public boolean removeAppointment(@RequestParam(value = "id") long id) {
        return scheduleManager.remove(id);
    }

    /**
     * Move all of day appointments to another day. It will be unsuccessfully if
     * one ore more appointments are conflict.
     * 
     * @param userId
     *            The user whose appointments we want to move
     * @param oldDate
     *            From which we want to move the appointments
     * @param newDate
     *            Where we want to move the appointments
     */
    @PostMapping(value = "/mycalendar/reScheduleDay", produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public int reScheduleAnDay(@RequestParam(value = "userId") long userId,
            @RequestParam(value = "oldDate") String oldDate, @RequestParam(value = "newDate") String newDate) {

        DateFormat formatter = new SimpleDateFormat(AppointmentDao.DATE_FORMAT_WITHOUT_TIME);

        try {
            boolean ok = scheduleManager.reScheduleDay(userId, formatter.parse(oldDate), formatter.parse(newDate));
            if (ok) {
                return AppointmentSaveJsonRespone.RESULT_OK;
            } else {
                return AppointmentSaveJsonRespone.RESULT_OTHER_ERROR;
            }
        } catch (AppointmentReservedException e) {
            return AppointmentSaveJsonRespone.RESULT_RESERVED;
        } catch (ParseException e) {
            return AppointmentSaveJsonRespone.RESULT_DATE_ERROR;
        }
    }
}
