package hu.kuncystem.patient.webapp.user;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import hu.kuncystem.patient.dao.appointment.AppointmentDao;
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
    /**
     * This enum contains the result of the user operations.
     */
    private enum STATE {
        SAVE_OK, UPDATE_OK, USER_EXISTS, USER_NO_EXISTS, DATABASE_ERROR
    }

    // user data save was ok
    private final static String MESSAGES_USER_SAVE_OK = "sok";
    // user data update was ok
    private final static String MESSAGES_USER_UPDATE_OK = "uok";

    // user data remove was ok
    private final static String MESSAGES_USER_DELETE_OK = "dok";
    // there was an problem when we try remove the user
    private final static String MESSAGES_USER_DELETE_ERROR = "derror";

    // size of the user table list
    private final static int USER_LIST_LIMIT = 30;

    @Autowired
    private UserManager userManager;

    @Autowired
    private UserGroupManager userGroupManager;

    @Autowired
    private MessageSource messageSource;

    /**
     * Show the surface of the usermanager. Show the result of an process and
     * list all of user data. <br>
     * Use: /usermanager
     * 
     * @param offset
     *            Where from have to list the user data. <br>
     *            Use: /usermanager?o=30
     * @param state
     *            An process result, if it is not specified then it will be 0.
     *            <br>
     *            Use: /usermanager?state=dok
     * @param model
     */
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
                    model.put("message", messageSource.getMessage("user.message.delete_error", null,
                            LocaleContextHolder.getLocale()));
                    model.put("cls", "danger");
                    break;
                }
            }
        }

        // set default offset data
        if (offset == null) {
            offset = 0;
        }

        // add extra data to the view object
        // list all of user
        List<User> userList = userManager.getAllUsers(USER_LIST_LIMIT, offset, JDBCUserDao.ORDER_BY_FULLNAME_ACTIVE);
        model.put("userList", userList);

        model.put("limit", USER_LIST_LIMIT);
        model.put("offset", offset);

        // if it is true then we are end of the list.
        model.put("end", (userList.size() < USER_LIST_LIMIT));

        return "usermanager";
    }

    /**
     * Show the user edit surface and load the default user data if it is
     * necessary (example: update an user data). <br>
     * Use: /usermanager/addUser
     * 
     * @param rowId
     *            The user row id which we want to change. If it is null then we
     *            will add new user. <br>
     *            Use: /usermanager/addUser?row=2
     */
    @RequestMapping(value = "/usermanager/addUser", method = RequestMethod.GET)
    public String showSaveUserForm(@RequestParam(value = "row", required = false) Integer rowId,
            HttpServletRequest request, ModelMap model) {
        // this is an User DTO object, we can validate this later.
        UserForm userForm = new UserForm();
        // marker the ui surface that we want modify or add new user
        model.put("modify", false);

        if (rowId != null) {
            // because teh rowId exists so we will update an user
            // load default user data from the database
            User user = userManager.getUser(rowId);
            if (user != null && (user.getId() == (long) request.getAttribute("userId")
                    || !request.isUserInRole("ROLE_PATIENT"))) {
                // add user data to the DTO
                userForm.setId(user.getId());
                userForm.setFullname(user.getFullname());
                userForm.setUsername(user.getUserName());
                userForm.setEmail(user.getEmail());
                userForm.setActive(user.isActive());

                // load user groups
                List<String> groups = new ArrayList<String>();
                List<UserGroup> groupList = userGroupManager.getGroupOfUser(user.getId());
                for (UserGroup group : groupList) {
                    groups.add(group.getName());
                }
                userForm.setGroups(groups.toArray(new String[] {}));
            } else {
                model.clear();
                return "redirect:/index";
            }

            model.put("modify", true);
        } else if (request.isUserInRole("ROLE_PATIENT")) {
            model.clear();
            return "redirect:/index";
        }
        model.addAttribute("userForm", userForm);

        return "useredit";
    }

    /**
     * Create or update the user data. <br>
     * <br>
     * Use: /usermanager/addUser
     * 
     * @param model
     * @param userForm
     *            This is an DTO object. When the user send an form then the
     *            form data will load in this object. The Spring will validate
     *            this object automatically.
     * @param result
     *            We can check the result of the validation through the object.
     */
    @RequestMapping(value = "/usermanager/addUser", method = RequestMethod.POST)
    public String addUser(@Valid UserForm userForm, BindingResult result, ModelMap model, HttpServletRequest request) {
        if (result.hasErrors()) {
            return "useredit";
        }
        // operations state
        STATE state;
        String page = null;

        if (userForm.getId() == 0) {
            // insert new user data
            state = this.addUser(userForm);
            model.put("modify", false);
        } else {
            // update an user
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
                if (request.isUserInRole("ROLE_PATIENT")) {
                    model.put("message",
                            messageSource.getMessage("user.message.save_ok", null, LocaleContextHolder.getLocale()));
                    model.put("cls", "success");

                    page = "useredit";
                } else {
                    page = "redirect:/usermanager?state=" + MESSAGES_USER_SAVE_OK;
                }
                break;
            }
            case UPDATE_OK: {
                if (request.isUserInRole("ROLE_PATIENT")) {
                    model.put("message",
                            messageSource.getMessage("user.message.update_ok", null, LocaleContextHolder.getLocale()));
                    model.put("cls", "success");

                    page = "useredit";
                } else {
                    page = "redirect:/usermanager?state=" + MESSAGES_USER_UPDATE_OK;
                }
                break;
            }
            default: {
                if (request.isUserInRole("ROLE_PATIENT")) {
                    page = "redirect:/index";
                } else {
                    page = "redirect:/usermanager";
                }
                break;
            }
        }

        return page;
    }

    /**
     * Disabled an user in the database. If we run this then the user can't
     * login . <br>
     * Use: /usermanager/deleteUser?row=2
     * 
     * @param rowId
     *            The user row id which we want to disable.
     */
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

    @PostMapping(value = "/usermanager/userList", produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public UserListFormJsonResponse getUsers(@RequestParam(value = "query") String query,
            @RequestParam(value = "group", required = false) String group,
            @RequestParam(value = "datetime", required = false) String datetime) {
        UserListFormJsonResponse response = new UserListFormJsonResponse();
        response.setQuery(query);

        DateFormat formatter = new SimpleDateFormat(AppointmentDao.DATE_FORMAT);
        // searching the users and add the user data to the collection
        List<User> userList = null;
        if (group == null) {
            userList = userManager.getUsersByName(query);
        } else if (group.equals("Doctor")) {
            try {
                userList = userManager.getFreeDoctorsByName(query, formatter.parse(datetime));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (group.equals("Patient")) {
            try {
                userList = userManager.getFreePatientsByName(query, formatter.parse(datetime));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        String name;
        if (userList != null) {
            for (User user : userList) {
                // use the full name or the user name
                name = (user.getFullname() != null && !user.getFullname().trim().isEmpty()) ? user.getFullname()
                        : user.getUserName();
                name += " - " + user.getEmail();
                response.addUserDataToJson(user.getId(), name);
            }
        }

        return response;
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
            if (userForm.getResetPassword() == null) {
                if (password != null) {
                    password = Hash.BCrypt(userForm.getPassword());
                }
            } else {
                password = Hash.BCrypt("123456");
            }
            boolean ok = userManager.updateUser(userForm.getId(), userForm.getUsername(), password, userForm.isActive(),
                    userForm.getFullname(), userForm.getEmail());
            if (!ok) {
                System.err.println("can't modify the user data!");
                return STATE.DATABASE_ERROR;
            } else if (userForm.getGroups() != null) {

                // update user groups
                ArrayList<String> groups = new ArrayList<String>(Arrays.asList(userForm.getGroups()));
                if (!userGroupManager.changeUserGroup(userForm.getId(), groups)) {
                    System.err.println("can't modify the groups!");
                    return STATE.DATABASE_ERROR;
                }
            }
        } else {
            return STATE.USER_NO_EXISTS;
        }
        return STATE.UPDATE_OK;
    }
}
