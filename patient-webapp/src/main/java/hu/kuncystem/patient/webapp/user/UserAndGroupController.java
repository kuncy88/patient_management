package hu.kuncystem.patient.webapp.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

import hu.kuncystem.patient.dao.user.JDBCUserDao;
import hu.kuncystem.patient.pojo.user.User;
import hu.kuncystem.patient.pojo.user.UserGroup;
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

    // size of the user table list
    private final static int USER_LIST_LIMIT = 30;

    @Autowired
    private UserManager userManager;

    @Autowired
    private UserGroupManager userGroupManager;

    @Autowired
    private MessageSource messageSource;

    @RequestMapping(value = "/usermanager", method = RequestMethod.GET)
    public String usermanager(@RequestParam(value = "o", required = false) Integer offset,
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

        if (offset == null) {
            offset = 0;
        }

        // list all of user
        List<User> userList = userManager.getAllUsers(USER_LIST_LIMIT, offset, JDBCUserDao.ORDER_BY_USERNAME);

        // add extra data to the view object
        model.put("userList", userList);
        model.put("limit", USER_LIST_LIMIT);
        model.put("offset", offset);
        model.put("end", (userList.size() < USER_LIST_LIMIT));

        return "usermanager";
    }

    @RequestMapping(value = "/usermanager/addUser", method = RequestMethod.GET)
    public String showSaveUserForm(@RequestParam(value = "row", required = false) Integer rowId, ModelMap model) {
        UserForm userForm = new UserForm();
        model.put("modify", false);
        
        if(rowId != null){
            User user = userManager.getUser(rowId);
            if(user != null){
                userForm.setId(user.getId());
                userForm.setFullname(user.getFullname());
                userForm.setUsername(user.getUserName());
                userForm.setEmail(user.getEmail());
                userForm.setActive(user.isActive());
                
                List<String> groups = new ArrayList<String>();
                List<UserGroup> groupList = userGroupManager.getGroupOfUser(user.getId());
                for(UserGroup group: groupList){
                    groups.add(group.getName());
                }
                userForm.setGroups(groups.toArray(new String[]{}));
            }
            
            model.put("modify", true);
        }
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
                model.put("message",
                        messageSource.getMessage("user.message.database_error", null, LocaleContextHolder.getLocale()));
                model.put("cls", "danger");

                page = "useredit";
            }
        } else {
            // user exists(is not so good :-) )
            model.put("message",
                    messageSource.getMessage("user.message.user_exists", null, LocaleContextHolder.getLocale()));
            model.put("cls", "warning");

            page = "useredit";
        }

        return page;
    }
}
