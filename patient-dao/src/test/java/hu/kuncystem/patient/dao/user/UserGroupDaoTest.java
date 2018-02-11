package hu.kuncystem.patient.dao.user;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

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
import hu.kuncystem.patient.pojo.user.UserGroup;

/**
 * This is a class for UserGroupDao interface test.
 *
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2018. jan. 6.
 * 
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { H2Config.class })
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserGroupDaoTest {

    private static User user;

    private static UserGroup group;

    @BeforeClass
    public static void setup() {
        UserFactory userFactory = new UserFactory();

        user = userFactory.getUser(UserFactory.DOCTOR);
        user.setUserName("teszt");
        user.setPassword("abcd12345");
        user.setEmail("test@domain.com");
        user.setFullname("Teszt Elek");

        group = new UserGroup("Admin");
        group.setNote("admin group");
    }

    @Autowired
    @Qualifier(value = "JDBCUserDao")
    private UserDao userDao;

    @Autowired
    @Qualifier(value = "JDBCUserGroupDao")
    private UserGroupDao userGroupDao;

    @Test
    public void stage1_schouldSuccessfullyWhenDaoVariablesContainJDBCObject() {
        assertThat(userDao, instanceOf(JDBCUserDao.class));
        assertThat(userGroupDao, instanceOf(JDBCUserGroupDao.class));
    }

    @Test
    public void stage2_schouldCreateUserGroupSuccessfullyWhenUserGroupDidNotExsist() {
        group = userGroupDao.saveUserGroup(group);
        assertNotNull(group);
        assertTrue("new user group create failed", group.getId() > 0);
    }

    @Test
    public void stage3_schouldCreateUserSuccessfullyWhenUserDidNotExsist() {
        user = userDao.saveUser(user);
        assertNotNull(user);
        assertTrue("new user create failed", user.getId() > 0);
    }

    @Test
    public void stage5_schouldGetUserGroupDataWhenUserGroupExsitsById() {
        group = userGroupDao.getUserGroup(group.getId());
        assertEquals("Admin", group.getName());
    }

    @Test
    public void stage6_schouldCreateUserAndGroupRelationSuccessfullyWhenRelationDidNotExsist() {
        assertTrue(userGroupDao.saveUserGroupRelation(group, user));
    }

    @Test
    public void stage7_schouldGetAllUsersFromAnGroup() {
        List<User> userLIst = userGroupDao.getAllUserFromGroup(group);
        assertTrue("test user list from group failed", userLIst.size() > 0);
    }

    @Test
    public void stage8_schouldGetAllGroupsByUser() {
        List<UserGroup> groupList = userGroupDao.getAllUserGroupByUser(user);
        assertTrue("test group list by user failed", groupList.size() > 0);
    }

    @Test
    public void stage9_schouldDeleteUserFromGroup() {
        assertTrue("delete relation was failed", userGroupDao.deleteUserGroupRelation(group, user));
    }

}
