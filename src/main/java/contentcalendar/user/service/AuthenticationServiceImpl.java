package contentcalendar.user.service;

import contentcalendar.user.domain.Token;
import contentcalendar.user.domain.User;
import contentcalendar.user.repo.TokenRepo;
import contentcalendar.user.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepo userRepo;
    private final TokenRepo tokenRepo;
    private final JwtServiceImpl jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public String register(User user) {
        String jwtToken;

        userRepo.save(user);
        // Generate token for saved user
        jwtToken = jwtService.generateToken(user);
        // Create new token entity
        saveUserToken(user, jwtToken);
        log.info("Saved new user and their first access token.");

        return jwtToken;
    }

    @Override
    public String authenticate(String username, String password) {
        String jwtToken;
        User user;

        log.info("Username: {}", username);
        log.info("Password: {}", password);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username, password)
        );
        user = userRepo.findByUsername(username);
        if (user == null)
            throw new UsernameNotFoundException(
                    "Username " + username + " not found");
        jwtToken = jwtService.generateToken(user);
        // revoke old tokens
        revokeAllUserTokens(user);
        // save new token
        saveUserToken(user, jwtToken);
        log.info("Saved token.");

        return jwtToken;
    }

    private void revokeAllUserTokens(User user) {
        List<Token> validTokens = tokenRepo.findAllValidTokenByUserId(user.getId());

        if (validTokens.isEmpty()) return;
        // revoke tokens
        validTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepo.saveAll(validTokens);
    }

    private void saveUserToken(User user, String jwtToken) {
        Token token = Token.builder()
                .user(user.getId())
                .token(jwtToken)
                .isExpired(false)
                .isRevoked(false)
                .build();
        tokenRepo.save(token);
    }

}
