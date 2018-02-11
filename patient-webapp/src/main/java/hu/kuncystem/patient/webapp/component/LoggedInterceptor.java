package hu.kuncystem.patient.webapp.component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import hu.kuncystem.patient.pojo.user.User;
import hu.kuncystem.patient.servicelayer.user.UserManager;

/**
 * This is an interceptor class. This class run each time when it happend an request and set default user data.
 * We can use these data in the html code.
 *
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2018. jan. 30.
 * 
 * @version 1.0
 */
@Component
public class LoggedInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    @Qualifier("defaultUserManager")
    private UserManager userManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        
        //get current auth data
        //if the user is not loggeed then the name is: anonymousUser
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null && !auth.getName().equals("anonymousUser")) {
            User user = userManager.getUser(auth.getName());        //get logged user data
            if(user != null){
                String name = user.getFullname();
                if (name.length() == 0) {
                    name = user.getUserName();
                }
                request.setAttribute("userFullname", name);
                request.setAttribute("userId", user.getId());
            }
        }

        return true;
    }
}
