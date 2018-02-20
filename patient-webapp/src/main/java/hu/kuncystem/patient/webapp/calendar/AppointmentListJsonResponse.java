package hu.kuncystem.patient.webapp.calendar;

import hu.kuncystem.patient.pojo.user.User;

/**
 * This class is used to the server response which contains an appointment data.
 *
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2018. febr. 15.
 * 
 * @version 1.0
 */
public class AppointmentListJsonResponse {

    private long id;

    private User user;

    private String date;

    private String description;

    private String[] notes;

    public AppointmentListJsonResponse() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getNotes() {
        return notes;
    }

    public void setNotes(String[] notes) {
        this.notes = notes;
    }
}
