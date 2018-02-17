package hu.kuncystem.patient.servicelayer.user;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import hu.kuncystem.patient.dao.user.JDBCUserDao;
import hu.kuncystem.patient.pojo.user.User;
import hu.kuncystem.patient.servicelayer.utilities.Hash;

/**
 * Testing the UserManager methods
 *
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2017. dec. 28.
 * 
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/beans_settings.xml" })
@ActiveProfiles("live")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserManagerTest {
    private static User user;

    @Autowired
    private UserManager userManager;

    @Test
    public void stage1_shouldCreateUserSuccessfullyWhenUserDidNotExists() {
        user = userManager.getUser("user1", Hash.MD5("abcd1234"));
        if (user == null) {
            user = userManager.createUser("user1", Hash.MD5("123456"), true);
            assertNotNull(user);
            assertTrue("create of new user failed", user.getId() > 0);
        } else {
            fail("The user already exsits!");
        }
    }

    @Test
    public void stage2_schouldUpdatePasswordAndSuccessfullyWhenOldAndNewNotMatch() {
        String oldPass = user.getPassword();
        // update password
        boolean ok = userManager.updateUser(user.getId(), user.getUserName(), Hash.MD5("abcd1234"), true, null, null);
        assertTrue("update of user's data failed", ok);

        // check, that is the old password equals the new password?
        user = userManager.getUser(user.getId());
        assertNotNull(user);
        assertTrue("update of user password failed", oldPass != user.getPassword());
    }

    @Test
    public void stage21_schouldGetAllUsers() {
        assertTrue(userManager.getAllUsers(10, 0, JDBCUserDao.ORDER_BY_USERNAME).size() > 0);
    }

    @Test
    public void stage22_schouldDontGetAllUserBecauseTheOffsetIsBad() {
        assertTrue(userManager.getAllUsers(10, 100, JDBCUserDao.ORDER_BY_USERNAME).size() == 0);
    }

    @Test
    public void stage23_schoulGetUsersUsingFilter() {
        assertTrue(userManager.getUsersByName("SeR").size() > 0);
    }

    @Test
    public void stage3_schouldDeleteUserSuccessfulyWhenUserExists() {
        user = userManager.getUser("user1");
        assertNotNull(user);

        assertTrue("removeUser:65 > delete user failed", userManager.removeUser(user.getId()));
    }
}
