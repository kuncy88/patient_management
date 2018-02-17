package hu.kuncystem.patient.webapp.calendar;

import java.util.Map;

import hu.kuncystem.patient.pojo.appointment.Appointment;

/**
 * this for comment of classes
 *
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2018. febr. 13.
 * 
 * @version 1.0
 */
public class AppointmentSaveJsonRespone {
    public static final int RESULT_RESERVED = -1;
    public static final int RESULT_DATE_ERROR = -2;
    public static final int RESULT_OTHER_ERROR = -3;
    public static final int RESULT_OK = 1;

    private Appointment appointment;

    private boolean validated;

    private Map<String, String> errorMessages;

    private int result;

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

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

}
