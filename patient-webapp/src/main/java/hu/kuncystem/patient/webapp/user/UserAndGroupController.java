package hu.kuncystem.patient.webapp.user;

import java.util.Arrays;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import hu.kuncystem.patient.pojo.user.User;
import hu.kuncystem.patient.servicelayer.user.UserGroupManager;
import hu.kuncystem.patient.servicelayer.user.UserManager;
import hu.kuncystem.patient.servicelayer.utilities.Hash;

/**
 * This controller handle the user and usergroup process(request and response).
 *
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2018. jan. 31.
 * 
 * @version 1.0
 */
@Controller
public class UserAndGroupController {
    private final static String MESSAGES_USER_SAVE_OK = "user_save_ok";

    @Autowired
    private UserManager userManager;

    @Autowired
    private UserGroupManager userGroupManager;

    @Autowired
    private MessageSource messageSource;

    @RequestMapping(value = "/usermanager", method = RequestMethod.GET)
    public String usermanager(@RequestParam(value = "id", required = false) String rowId,
            @RequestParam(value = "state", required = false) String state, ModelMap model) {

        if (state != null) {
            switch (state) {
                case MESSAGES_USER_SAVE_OK: {
                    model.put("message",
                            messageSource.getMessage("user.message.save_ok", null, LocaleContextHolder.getLocale()));
                    model.put("cls", "success");
                    break;
                }
            }
        }
        return "usermanager";
    }

    @RequestMapping(value = "/usermanager/addUser", method = RequestMethod.GET)
    public String showSaveUserForm(ModelMap model) {
        UserForm userForm = new UserForm();
        model.addAttribute("userForm", userForm);
        
        return "useredit";
    }

    @Transactional
    @RequestMapping(value = "/usermanager/addUser", method = RequestMethod.POST)
    public String addUser(ModelMap model, @Valid UserForm userForm, BindingResult result) {
        if (result.hasErrors()) {
            return "useredit";
        }

        String page = "redirect:/usermanager?state=" + MESSAGES_USER_SAVE_OK;
        
        User user = userManager.getUser(userForm.getUsername());
        if (user == null) { // user does not exists
            user = userManager.createUser(userForm.getUsername(), Hash.BCrypt(userForm.getPassword()),
                    userForm.isActive(), userForm.getFullname(), userForm.getEmail());
            if (user != null) {
                if (userForm.getGroups() != null) {
                    if (!userGroupManager.saveRelation(user.getId(), Arrays.asList(userForm.getGroups()))) {
                        // save of relation was unsuccessful
                        model.put("message", messageSource.getMessage("user.message.database_error", null,
                                LocaleContextHolder.getLocale()));
                        model.put("cls", "danger");
                        
                        page = "useredit";
                    }
                }
            } else {
                // here is an error. the save of user was unsuccessful
                model.put("message", messageSource.getMessage("user.message.database_error", null,
                        LocaleContextHolder.getLocale()));
                model.put("cls", "danger");
                
                page = "useredit";
            }
        } else {
            // user exists(is not so good :-) )
            model.put("message", messageSource.getMessage("user.message.user_exists", null,
                    LocaleContextHolder.getLocale()));
            model.put("cls", "warning");
            
            page = "useredit";
        }

        return page;
    }
    
    @RequestMapping(value = "/myaccount", method = RequestMethod.GET)
    public String account() {
        return "myaccount";
    }

    @RequestMapping(value = "/saveUser", method = RequestMethod.POST)
    public String saveUser(ModelMap model, UserForm userForm) {

        /*
         * String redirect = "/usermanager?state=" + MESSAGES_USER_SAVE_OK;
         * //the datas is not correct System.out.println(username);
         * System.out.println(password); System.out.println(passwordAgain); if
         * (username.length() == 0 || !password.equals(passwordAgain)) {
         * redirect = "/usermanager?page=useredit&state=" + MESSAGES_DATA_ERROR;
         * } else { // check the user exists or not User user =
         * userManager.getUser(username); if (user == null) { //add new user
         * user = userManager.createUser(username, password, (active == "yes" ?
         * true : false), fullname, email); if (user != null) { if (groups !=
         * null) { //add user and group relation if
         * (!userGroupManager.saveRelation(user.getId(), Arrays.asList(groups)))
         * { //it heppaned database error redirect =
         * "/usermanager?page=useredit&state=" + MESSAGES_DATABASE_ERROR; } } }
         * else { //it happend database error redirect =
         * "/usermanager?page=useredit&state=" + MESSAGES_DATABASE_ERROR; } }
         * else { // user has been already exists redirect =
         * "/usermanager?page=useredit&state=" + MESSAGES_USER_EXISTS; } }
         */

        return "redirect:/usermanager?page=useredit";
    }
}
