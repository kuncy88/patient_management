package hu.kuncystem.patient.webapp.calendar;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * This is a DTO object which we use that validate and use an form data.
 *
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2018. febr. 13.
 * 
 * @version 1.0
 */
public class AppointmentForm {

    private Long appointmentId;

    @Min(1)
    private long doctorId;

    @Min(1)
    private long patientId;

    @Size(min = 1, max = 300)
    private String description;

    @NotEmpty
    private String startTime;

    @NotEmpty
    private String endTime;

    private String notes;

    public AppointmentForm() {
    }

    public long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(long doctorId) {
        this.doctorId = doctorId;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }
}
