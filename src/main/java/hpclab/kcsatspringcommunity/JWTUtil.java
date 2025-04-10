package hpclab.kcsatspringcommunity;

import hpclab.kcsatspringcommunity.community.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

/**
 * JWT 토큰 설정 관련 클래스입니다.
 */
@Component
public class JWTUtil {

    private static final Long expiredMs = 3600000L;

    @Value("${jwt.secret}")
    private static String secretKey;
    private static final SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));

    public String generateToken(String userEmail, String userName, Role role) {
        return Jwts.builder()
                .claim("userEmail", userEmail)
                .claim("userName", userName)
                .claim("role", role)
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(key)
                .compact();
    }

    public Claims getClaims(String token) {
        String tokenWithoutHeader = token.split(" ")[1];
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(tokenWithoutHeader)
                .getPayload();
    }

    public long getExpiration(String token) {
        String tokenWithoutHeader = token.split(" ")[1];
        Claims claims = getClaims(tokenWithoutHeader);
        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }
}
