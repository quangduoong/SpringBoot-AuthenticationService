package contentcalendar.user.service;

import contentcalendar.user.domain.User;

public interface AuthenticationService {
    String register(User user);
    String authenticate(String username, String password);
}
