package contentcalendar.user.service;

import contentcalendar.user.domain.Role;
import contentcalendar.user.domain.User;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.util.List;

public interface UserService extends LogoutHandler {
    void saveUser(User user);

    void saveRole(Role role);

    void addRoleToUser(String username, String roleName);

    User getUser(String username);

    List<User> getUsers();

}
