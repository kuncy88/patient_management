package hu.kuncystem.patient.dao.user;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import hu.kuncystem.patient.dao.H2Config;
import hu.kuncystem.patient.pojo.user.User;
import hu.kuncystem.patient.pojo.user.UserFactory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

/**
 * This is a class for UserDao interface test.
 *
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2017. nov. 12.
 * 
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { H2Config.class })
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserDaoTest {

    private static User user;

    @BeforeClass
    public static void setup() {
        UserFactory userFactory = new UserFactory();

        user = userFactory.getUser(UserFactory.DOCTOR);
        user.setUserName("teszt");
        user.setPassword("abcd12345");
        user.setEmail("test@domain.com");
        user.setFullname("Teszt Elek");
    }

    @Autowired
    @Qualifier(value = "JDBCUserDao")
    private UserDao userDao;

    @Test
    public void stage1_schouldSuccessfullyWhenUserDaoVariableContainJDBCUserDaoObject() {
        assertThat(userDao, instanceOf(JDBCUserDao.class));
    }

    @Test
    public void stage2_schouldCreateUserSuccessfullyWhenUserDidNotExsist() {
        user = userDao.saveUser(user);
        assertNotNull(user);
        assertTrue("new user create failed", user.getId() > 0);
    }

    @Test
    public void stage3_schouldUpdateEmailWhenUserExsitsById() {
        user.setEmail("test1@domain.com");
        assertTrue("user update failed", userDao.updateUser(user));

        // get user by id(check that the update was successfully)
        user = userDao.getUser(user.getId());
        assertEquals("test1@domain.com", user.getEmail());
    }

    @Test
    public void stage31_schouldGetUserIfExsitsByName() {
        // get user by name
        user = userDao.getUser("teszt");
        assertNotNull(user);
    }

    @Test
    public void stage32_schouldGetAllUsers() {
        // get user by name
        assertTrue(userDao.getAllUsers(10, 0, JDBCUserDao.ORDER_BY_USERNAME).size() > 0);
    }

    @Test
    public void stage4_schouldGetUserDataWhenUserExsitsByNameAndPassword() {
        user = userDao.getUser("teszt", "abcd12345");
        assertEquals("test1@domain.com", user.getEmail());
    }

    @Test
    public void stage5_schouldDeleteSuccessfullyWhenUserExsist() {
        assertTrue(userDao.deleteUser(user));
    }
}
