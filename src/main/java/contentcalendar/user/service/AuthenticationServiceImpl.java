package contentcalendar.user.service;

import contentcalendar.user.domain.Token;
import contentcalendar.user.domain.User;
import contentcalendar.user.dto.AuthenticationResponse;
import contentcalendar.user.repo.TokenRepo;
import contentcalendar.user.repo.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepo userRepo;
    private final TokenRepo tokenRepo;
    private final JwtServiceImpl jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse register(User user) {
        String accessToken;
        String refreshToken;

        userRepo.save(user);
        // Generate token for saved user
        accessToken = jwtService.generateAccessToken(user);
        refreshToken = jwtService.generateRefreshToken(user);
        // Create new token entity
        saveUserToken(user, accessToken);
        log.info("Saved new user and their first access token.");

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthenticationResponse authenticate(String username, String password) {
        String accessToken;
        String refreshToken;
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
        accessToken = jwtService.generateAccessToken(user);
        refreshToken = jwtService.generateRefreshToken(user);
        // revoke old tokens
        revokeAllUserTokens(user);
        // save new token
        saveUserToken(user, accessToken);
        log.info("Saved token.");

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public Optional<AuthenticationResponse> refreshAccessToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String refreshToken;
        String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("Missing header values.");
            throw new NoSuchElementException("Missing header values.");
        }

        refreshToken = authHeader.substring(7);
        username = jwtService.extractUsername(refreshToken);
        if (username != null) {
            var user = this.userRepo.findByUsername(username);
            if (user == null) throw new NoSuchElementException("Cannot find username " + username);
            if (jwtService.isTokenAuthenticated(refreshToken, user)) {
                var accessToken = jwtService.generateAccessToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                return Optional.ofNullable(AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build());
            }
        }

        throw new NoSuchElementException("Cannot extract username " + username + " from token.");
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