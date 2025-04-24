package hpclab.kcsatspringcommunity;

import hpclab.kcsatspringcommunity.community.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

/**
 * JWT 토큰 설정 관련 클래스입니다.
 */
@Component
public class JWTUtil {

    private static final Long expiredMs = 3600000L;

    @Value("${jwt.secret}")
    private String secretKey;
    private final SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));

    public static final String USER_EMAIL = "userEmail";
    public static final String USER_NAME = "userName";
    public static final String ROLE = "role";

    // 토큰 생성 메서드
    public String generateToken(String userEmail, String userName, Role role) {
        return Jwts.builder()
                .claim(USER_EMAIL, userEmail)
                .claim(USER_NAME, userName)
                .claim(ROLE, role.getValue())
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(key)
                .compact();
    }

    // claim 반환 메서드
    public Claims getClaims(String token) {
        String tokenWithoutHeader = token.replace("Bearer ", "");
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(tokenWithoutHeader)
                .getPayload();
    }

    // 토큰 유효기간 반환 메서드
    public long getExpiration(String token) {
        String tokenWithoutHeader = token.replace("Bearer ", "");
        Claims claims = getClaims(tokenWithoutHeader);
        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }

    // 토큰 유효기간 확인 메서드
    public boolean isTokenExpired(String tokenWithHeader) {
        try {
            String token = tokenWithHeader.replace("Bearer ", "");
            Claims claims = getClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
