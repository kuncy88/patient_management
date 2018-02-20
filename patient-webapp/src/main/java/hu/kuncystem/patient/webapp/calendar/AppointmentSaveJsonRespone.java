package hu.kuncystem.patient.webapp.calendar;

import java.util.Map;

import hu.kuncystem.patient.pojo.appointment.Appointment;

/**
 * This class is used to the server response which contains the operation result.
 *
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2018. febr. 13.
 * 
 * @version 1.0
 */
public class AppointmentSaveJsonRespone {
    //the appointment is resevrd
    public static final int RESULT_RESERVED = -1;
    //the date format is not correct
    public static final int RESULT_DATE_ERROR = -2;
    //perhaps database error(we have to check the logs)
    public static final int RESULT_OTHER_ERROR = -3;
    //the operation was success
    public static final int RESULT_OK = 1;

    //The appointment which we created or updated
    private Appointment appointment;

    //Marker tahe the form data is valid or not
    private boolean validated;

    //if tha validation was unsuccess then it will contains the errors
    private Map<String, String> errorMessages;

    //it contains the value of RESULT_**
    private Integer result;

    public AppointmentSaveJsonRespone() {
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public Map<String, String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(Map<String, String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

}
