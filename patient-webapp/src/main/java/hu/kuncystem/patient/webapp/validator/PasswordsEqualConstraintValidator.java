package hu.kuncystem.patient.webapp.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import hu.kuncystem.patient.webapp.user.UserForm;

/**
 * This object will check the passwords to equals or not.
 *
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2018. febr. 4.
 * 
 * @version 1.0
 */
public class PasswordsEqualConstraintValidator implements ConstraintValidator<PasswordsEqualConstraint, Object> {

    @Override
    public void initialize(PasswordsEqualConstraint arg0) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.validation.ConstraintValidator#isValid(java.lang.Object,
     * javax.validation.ConstraintValidatorContext)
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        UserForm user = (UserForm) value;
        if (user.getPassword() != null && user.getConfirmPassword() != null) {
            return user.getPassword().equals(user.getConfirmPassword());
        } else {
            return true;
        }
    }

}
