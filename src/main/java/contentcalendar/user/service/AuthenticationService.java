package contentcalendar.user.service;

import contentcalendar.user.domain.User;
import contentcalendar.user.dto.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public interface AuthenticationService {
    AuthenticationResponse register(User user);
    AuthenticationResponse authenticate(String username, String password);
    Optional<AuthenticationResponse> refreshAccessToken(HttpServletRequest request);
}
