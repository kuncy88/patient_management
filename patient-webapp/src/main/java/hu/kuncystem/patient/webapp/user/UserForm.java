package hu.kuncystem.patient.webapp.user;

import java.io.Serializable;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import hu.kuncystem.patient.webapp.validator.PasswordsEqualConstraint;

/**
 * this for comment of classes
 *
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2018. febr. 4.
 *  
 * @version 1.0
 */
@PasswordsEqualConstraint(message = "")
public final class UserForm implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private long id;

    @Size(min= 1, max = 45)
    private String username;
    
    @Size(min= 6)
    private String password;
    
    @Size(min= 6)
    private String confirmPassword;
    
    @Size(max = 255)
    private String fullname;
    
    @Pattern(regexp="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
    @Size(max = 128)
    private String email;
    
    private boolean active = true;
    private String[] groups = new String[]{};
    private String resetPassword;
    
    public UserForm(){
        
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String[] getGroups() {
        return groups;
    }

    public void setGroups(String[] groups) {
        this.groups = groups;
    }
    
    public String getResetPassword() {
        return resetPassword;
    }

    public void setResetPassword(String resetPassword) {
        this.resetPassword = resetPassword;
    }    
}
