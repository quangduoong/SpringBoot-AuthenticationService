package contentcalendar.user.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.function.Function;

public interface JwtService {
    String extractUsername(String jwt);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    String generateAccessToken(UserDetails userDetails);
    String generateRefreshToken(UserDetails userDetails);

    boolean isTokenAuthenticated(String token, UserDetails userDetails);
}
