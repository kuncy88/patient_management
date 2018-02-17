package hu.kuncystem.patient.webapp.user;

import java.util.ArrayList;
import java.util.List;

/**
 * this for comment of classes
 *
 * @author Csaba Kun <kuncy88@gmail.com>
 * @date 2018. febr. 13.
 * 
 * @version 1.0
 */
public class UserListFormJsonResponse {
    private List<UserDataToJson> userList;

    private String query;

    public UserListFormJsonResponse() {
    }

    public List<UserDataToJson> getUserList() {
        return userList;
    }

    public void setUserList(List<UserDataToJson> userList) {
        this.userList = userList;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
    
    public void addUserDataToJson(long id, String name){
        if(userList == null){
            userList = new ArrayList<UserDataToJson>();
        }
        
        UserDataToJson userDataToJson = new UserDataToJson();
        userDataToJson.setId(id);
        userDataToJson.setName(name);
        
        userList.add(userDataToJson);
    }
    
    public static final class UserDataToJson{
        private long id;
        private String name;
        public long getId() {
            return id;
        }
        public void setId(long id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        
        
    }
}
