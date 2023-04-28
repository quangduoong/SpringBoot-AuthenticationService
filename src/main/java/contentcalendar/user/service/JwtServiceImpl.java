package contentcalendar.user.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    @Value(value = "${application.security.jwt.secret-key}")
    private  String SECRET_KEY;
    @Value(value = "${application.security.jwt.access-token.expiration}")
    private  String ACCESS_TOKEN_EXP;
    @Value(value = "${application.security.jwt.refresh-token.expiration}")
    private  String REFRESH_TOKEN_EXP;

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public <T> T extractClaim(String token, @NonNull Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public String generateAccessToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, this.ACCESS_TOKEN_EXP);
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, this.REFRESH_TOKEN_EXP);
    }

    @Override
    public boolean isTokenAuthenticated(String token, @NonNull UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {

        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String buildToken(Map<String, Object> extraClaims
            , @NonNull UserDetails userDetails, String expiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(expiration)))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @NonNull
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.SECRET_KEY.substring(7));
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
