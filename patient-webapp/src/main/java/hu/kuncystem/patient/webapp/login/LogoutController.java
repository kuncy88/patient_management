package hu.kuncystem.patient.webapp.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import hu.kuncystem.patient.servicelayer.exception.SessionDataChangeException;
import hu.kuncystem.patient.servicelayer.exception.SessionNotExistsException;
import hu.kuncystem.patient.servicelayer.session.SessionManager;

/**
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2018. jan. 23.
 * 
 * @version 1.0
 */
@Controller
public class LogoutController {

    @Autowired
    @Qualifier("defaultSessionManager")
    private SessionManager sessionManager;

    /**
     * This method will remove the user's session. After we run this method the
     * user has to login again.
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(value = "type", required = false) String type) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {

            try {
                // destroy older session, we want to create new, later.
                sessionManager.destroy();
            } catch (SessionNotExistsException e) {
                // do nothing, it is very good that there isn't session.
            } catch (SessionDataChangeException e) {
                // there is an error, so we can't logout the user.
                return "message?type=logout_error&class=error";
            }

            // invalidate the user's spring security session
            new SecurityContextLogoutHandler().logout(request, response, auth);
            request.getSession().invalidate();
        }
        
        if(type == null){           //if the  message marker doesn't exisst then we set an default
            type = LoginController.MESSAGE_TYPE_LOGOUT;
        }

        return "redirect:/login?type=" + type;
    }

}
