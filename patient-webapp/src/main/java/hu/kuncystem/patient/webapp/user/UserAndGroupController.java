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
    private enum STATE {
        SAVE_OK, UPDATE_OK, USER_EXISTS, USER_NO_EXISTS, DATABASE_ERROR
    }

    private final static String MESSAGES_USER_SAVE_OK = "sok";
    private final static String MESSAGES_USER_UPDATE_OK = "uok";
    
    private final static String MESSAGES_USER_DELETE_OK = "dok";
    private final static String MESSAGES_USER_DELETE_ERROR = "derror";

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
                case MESSAGES_USER_UPDATE_OK: {
                    model.put("message",
                            messageSource.getMessage("user.message.update_ok", null, LocaleContextHolder.getLocale()));
                    model.put("cls", "success");
                    break;
                }
                case MESSAGES_USER_DELETE_OK: {
                    model.put("message",
                            messageSource.getMessage("user.message.delete_ok", null, LocaleContextHolder.getLocale()));
                    model.put("cls", "success");
                    break;
                }
                case MESSAGES_USER_DELETE_ERROR: {
                    model.put("message",
                            messageSource.getMessage("user.message.delete_error", null, LocaleContextHolder.getLocale()));
                    model.put("cls", "danger");
                    break;
                }
            }
        }

        if (offset == null) {
            offset = 0;
        }

        // list all of user
        List<User> userList = userManager.getAllUsers(USER_LIST_LIMIT, offset, JDBCUserDao.ORDER_BY_FULLNAME_ACTIVE);
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

        if (rowId != null) {
            User user = userManager.getUser(rowId);
            if (user != null) {
                userForm.setId(user.getId());
                userForm.setFullname(user.getFullname());
                userForm.setUsername(user.getUserName());
                userForm.setEmail(user.getEmail());
                userForm.setActive(user.isActive());

                List<String> groups = new ArrayList<String>();
                List<UserGroup> groupList = userGroupManager.getGroupOfUser(user.getId());
                for (UserGroup group : groupList) {
                    groups.add(group.getName());
                }
                userForm.setGroups(groups.toArray(new String[] {}));
            }

            model.put("modify", true);
        }
        model.addAttribute("userForm", userForm);

        return "useredit";
    }

    @RequestMapping(value = "/usermanager/addUser", method = RequestMethod.POST)
    public String addUser(ModelMap model, @Valid UserForm userForm, BindingResult result) {
        if (result.hasErrors()) {
            return "useredit";
        }

        STATE state;
        String page = null;

        if (userForm.getId() == 0) { // insert new user data
            state = this.addUser(userForm);
            model.put("modify", false);
        } else { // update an user
            state = this.updateUser(userForm);
            model.put("modify", true);
        }

        switch (state) {
            case USER_EXISTS: {
                model.put("cls", "warning");
                model.put("message",
                        messageSource.getMessage("user.message.user_exists", null, LocaleContextHolder.getLocale()));

                page = "useredit";

                break;
            }
            case USER_NO_EXISTS: {
                model.put("cls", "warning");
                model.put("message",
                        messageSource.getMessage("user.message.user_no_exists", null, LocaleContextHolder.getLocale()));

                page = "useredit";
                break;
            }
            case DATABASE_ERROR: {
                model.put("cls", "danger");
                model.put("message",
                        messageSource.getMessage("user.message.database_error", null, LocaleContextHolder.getLocale()));

                page = "useredit";

                break;
            }
            case SAVE_OK: {
                page = "redirect:/usermanager?state=" + MESSAGES_USER_SAVE_OK;
                break;
            }
            case UPDATE_OK: {
                page = "redirect:/usermanager?state=" + MESSAGES_USER_UPDATE_OK;
                break;
            }
            default: {
                page = "redirect:/usermanager";
                break;
            }

        }

        return page;
    }

    @RequestMapping(value = "/usermanager/deleteUser")
    public String deleteUser(@RequestParam(value = "row") Integer rowId) {
        String page = "redirect:/usermanager";

        if (userManager.updateUser(rowId, null, null, false, null, null)) {
            page += "?state=" + MESSAGES_USER_DELETE_OK;
        } else {
            page += "?state=" + MESSAGES_USER_DELETE_ERROR;
        }

        return page;
    }

    /**
     * Create new user and create user and group relations.
     * 
     * @param userForm
     *            This is an DTO object which contains all of necessary data.
     * 
     * @return STATE enum.
     */
    @Transactional
    private STATE addUser(UserForm userForm) {
        User user = userManager.getUser(userForm.getUsername());
        if (user == null) {
            // create new user
            user = userManager.createUser(userForm.getUsername(), Hash.BCrypt(userForm.getPassword()),
                    userForm.isActive(), userForm.getFullname(), userForm.getEmail());
            if (user != null) {
                if (userForm.getGroups() != null) {
                    // create user and group relations
                    if (!userGroupManager.saveRelation(user.getId(), Arrays.asList(userForm.getGroups()))) {
                        return STATE.DATABASE_ERROR;
                    }
                }
            } else {
                return STATE.DATABASE_ERROR;
            }
        } else {
            return STATE.USER_EXISTS;
        }

        return STATE.SAVE_OK;
    }

    /**
     * Update an user data and update user and group relations.
     * 
     * @param userForm
     *            This is an DTO object which contains all of necessary data.
     * 
     * @return STATE enum.
     */
    @Transactional
    private STATE updateUser(UserForm userForm) {
        User user = userManager.getUser(userForm.getId());
        if (user != null) {
            // update user
            String password = userForm.getPassword();
            if (password != null) {
                password = Hash.BCrypt(userForm.getPassword());
            }
            boolean ok = userManager.updateUser(userForm.getId(), userForm.getUsername(), password, userForm.isActive(),
                    userForm.getFullname(), userForm.getEmail());
            if (!ok) {
                return STATE.DATABASE_ERROR;
            } else if (userForm.getGroups() != null) {
                // update user groups
                if (!userGroupManager.changeUserGroup(userForm.getId(), Arrays.asList(userForm.getGroups()))) {
                    return STATE.DATABASE_ERROR;
                }
            }
        } else {
            return STATE.USER_NO_EXISTS;
        }
        return STATE.UPDATE_OK;
    }
}
