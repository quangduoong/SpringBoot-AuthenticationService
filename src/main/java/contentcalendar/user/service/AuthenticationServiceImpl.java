package contentcalendar.user.service;

import contentcalendar.user.domain.User;
import contentcalendar.user.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepo userRepo;
    private final JwtServiceImpl jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public String register(User user) {
        userRepo.save(user);
        return jwtService.generateToken(user);
    }

    @Override
    public String authenticate(String username, String password) {
        String jwtToken;
        User user;

        log.info("Username: {}", username);
        log.info("Password: {}", password);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        user = userRepo.findByUsername(username);
        if (user == null)
            throw new UsernameNotFoundException("Username " + username + " not found");
        jwtToken = jwtService.generateToken(user);

        return jwtToken;
    }
}
