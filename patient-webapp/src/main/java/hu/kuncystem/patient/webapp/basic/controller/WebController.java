package hu.kuncystem.patient.webapp.basic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This controller is a default controller. We can display messages or we can do
 * other things.
 *
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2018. jan. 27.
 * 
 * @version 1.0
 */
@Controller
public class WebController {
    public final static String MESSAGE_TYPE_LOGOUT_ERROR = "logout_error";

    @Autowired
    private MessageSource messageSource;

    /**
     * This method display an message on the screen. We can add the message type
     * which define the message text and we can add extra class to the message, here.
     */
    @RequestMapping(value = "/message")
    public String message(@RequestParam(value = "type") String type,
            @RequestParam(value = "class", required = false) String cls, ModelMap model) {

        String text = "";
        switch (type) {
            case MESSAGE_TYPE_LOGOUT_ERROR: {
                text = messageSource.getMessage("message.logout_error", null, LocaleContextHolder.getLocale());
                break;
            }
        }

        model.put("message", text);
        if (cls != null) {
            model.put("cls", cls);
        }
        return "message";
    }
    
    /**
     * This is the start page
     */
    @RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
    public String index() {
        return "index";
    }
    
    /**
     * This is an description about the software.
     * */
    @RequestMapping(value = "/about", method = RequestMethod.GET)
    public String about() {
        return "about";
    }
    
    /**
     * Error page: 403
     * */
    @RequestMapping(value = "/403", method = RequestMethod.GET)
    public String error403() {
        return "403";
    }
    
    /**
     * Error page: 404
     * */
    @RequestMapping(value = "/404", method = RequestMethod.GET)
    public String error404() {
        return "404";
    }
    
    /**
     * Error page: 500
     * */
    @RequestMapping(value = "/500", method = RequestMethod.GET)
    public String error500() {
        return "500";
    }
}
