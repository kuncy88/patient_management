package hu.kuncystem.patient.webapp.login;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import hu.kuncystem.patient.pojo.user.User;
import hu.kuncystem.patient.servicelayer.session.SessionManager;
import hu.kuncystem.patient.servicelayer.user.UserManager;

/**
 * This class control the process of login.
 *
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2018. jan. 16.
 * 
 * @version 1.0
 */
@Controller
public class LoginController {
    public final static String MESSAGE_TYPE_LOGOUT = "logout";

    public final static String MESSAGE_TYPE_LOGIN_ERROR = "error";

    public final static String MESSAGE_TYPE_ERROR_SESSION = "session";

    public final static String MESSAGE_TYPE_ERROR_SESSIONUSER = "session_user";

    @Autowired
    private MessageSource messageSource;

    @Autowired
    @Qualifier("defaultSessionManager")
    private SessionManager sessionManager;

    @Autowired
    @Qualifier("defaultUserManager")
    private UserManager userManager;

    /**
     * We can run extra process if the login was success.
     * 
     * @param redirect
     *            End of the extra process we redirect the user other page. This
     *            is an url. (/index?msg=login_success)
     */
    @RequestMapping(value = "/loginSuccess")
    public String loginSuccess(@RequestParam(value = "redirect") String redirect, HttpServletRequest request,
            Principal principal) {
        if (redirect == null) {
            redirect = "index";
        }
        // get logged in username
        User user = userManager.getUser(principal.getName());
        if (user != null) {
            if(user.isActive()){            //the user is active now
                // destroy all active session
                if (sessionManager.destroyAllActiveSession(user.getId())) {
                    try {
                        sessionManager.createSession(user.getId(), request.getRemoteAddr(),
                                request.getHeader("User-Agent"));
                    } catch (Exception e) { // session create error
                        redirect = "logout?type=" + MESSAGE_TYPE_ERROR_SESSION;
                        e.printStackTrace();
                    }
                } else { // destroy error
                    redirect = "logout?type=" + MESSAGE_TYPE_ERROR_SESSION;
                }
            } else {                        //this user is not active (it has been deleted)
                redirect = "logout?type=" + MESSAGE_TYPE_LOGIN_ERROR;
            }
        } else { // user not found
            redirect = "logout?type=" + MESSAGE_TYPE_ERROR_SESSIONUSER;
        }
        return "redirect:/" + redirect;
    }

    /**
     * If the user doesn't have permission an page we will show this page.
     */
    @RequestMapping(value = "/403")
    public String accesssDenied() {
        return "403";
    }

    /**
     * This is the login page. We show the login form and we handle the process
     * of login result.
     * 
     * @param type
     *            This is the message type which we want to show.
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(@RequestParam(value = "type", required = false) String type, ModelMap model,
            final Principal principal) {

        if (principal != null) { // the login was successfully earlier
            return "redirect:/";
        } else {
            if (type != null) {
                String text = "";
                String cls = "info";
                switch (type) {
                    case MESSAGE_TYPE_LOGOUT: { // logout was success
                        text = messageSource.getMessage("message.logout_success", null,
                                LocaleContextHolder.getLocale());

                        cls = "success";
                        break;
                    }
                    case MESSAGE_TYPE_LOGIN_ERROR: { // bad user data
                        text = messageSource.getMessage("message.bad_auth_data", null, LocaleContextHolder.getLocale());

                        cls = "danger";
                        break;
                    }
                    case MESSAGE_TYPE_ERROR_SESSION: // session create error
                    case MESSAGE_TYPE_ERROR_SESSIONUSER: {
                        text = messageSource.getMessage("message.login_session_error", null,
                                LocaleContextHolder.getLocale());
                        cls = "danger";
                        break;
                    }
                }

                model.put("cls", cls);
                model.put("message", text);
            }
            return "login";
        }
    }
}
