package hu.kuncystem.patient.servicelayer.user;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import hu.kuncystem.patient.pojo.user.User;
import hu.kuncystem.patient.pojo.user.UserGroup;
import hu.kuncystem.patient.servicelayer.utilities.Hash;

/**
 * Testing the DefaultUserGroupManager methods
 *
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2017. dec. 31.
 * 
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/beans_settings.xml" })
@ActiveProfiles("live")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserGroupManagerTest {
    private static UserGroup group;

    private static User user;

    @Autowired
    private UserGroupManager userGroupManager;

    @Autowired
    private UserManager userManager;

    @Test
    public void stage1_schouldCreateUserGroupSuccessfully() {
        group = userGroupManager.createGroup("Group1", "note of test group");
        assertTrue("create of new group failed", group.getId() > 0);
    }

    @Test
    public void stage2_shouldCreateUserSuccessfullyWhenUserDidNotExists() {
        user = userManager.getUser("Teszt User", Hash.MD5("abcd1234"));
        if (user == null) {
            user = userManager.createUser("Teszt User", Hash.MD5("123456"), true);
            assertNotNull(user);
            assertTrue("create of new user failed", user.getId() > 0);
        } else {
            fail("The user already exsits!");
        }
    }

    @Test
    public void stage3_schouldCreateNewRelationWhenDidNotExists() {
        boolean match = false;
        // get all group by user
        List<UserGroup> groupList = userGroupManager.getGroupOfUser(user.getId());
        for (UserGroup userGroup : groupList) {
            if (userGroup.getId() == group.getId()) { // check, does the
                                                      // relation(user and
                                                      // group) exists
                match = true;
                break;
            }
        }

        if (!match) { // not exists
            assertTrue("create of user and group relation failed",
                    userGroupManager.saveRelation(user.getId(), group.getId()));
        } else { // already exists
            fail("This relation already exists!");
        }
    }

    @Test
    public void stage4_schouldGetListOfUserByGroupId() {
        List<User> userList = userGroupManager.getUsersFromGroup(group.getId());
        assertTrue("User list not found by group id", userList.size() > 0);
    }

    @Test
    public void stage5_schouldGetDataOfGroupById() {
        UserGroup group1 = userGroupManager.getGroup(group.getId());
        assertTrue("Get data of group failed", group.getId() == group1.getId());
    }

    @Test
    public void stage6_schouldChangeTheGroupOfUser() {
        userGroupManager.createGroup("Group2", "note of test group2");
        userGroupManager.createGroup("Group3", "note of test group3");

        // check old relation
        List<UserGroup> groupList = userGroupManager.getGroupOfUser(user.getId());
        assertTrue((groupList.size() == 1));

        List<String> newGroups = new ArrayList<String>();
        newGroups.add("Group2");
        newGroups.add("Group3");
        // change relation
        assertTrue("the change of group was unsuccessful", userGroupManager.changeUserGroup(user.getId(), newGroups));

        // check new relation
        groupList = userGroupManager.getGroupOfUser(user.getId());
        assertTrue((groupList.size() == 2));
    }
}
