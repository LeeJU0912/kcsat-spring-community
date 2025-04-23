package hpclab.kcsatspringcommunity;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

/**
 * JWT 인증 필터 클래스입니다.
 * 매 요청마다 이 클래스의 필터를 통과하여 올바른 사용자인지 검증합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Options Preflight 요청인 경우, 필터 생략
        if (request.getMethod().equals(HttpMethod.OPTIONS.name())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 요청 URI 추출
        String path = request.getRequestURI();

        // 상태 체크 URI인 경우, 필터 생략
        if (path.startsWith("/health-check") || path.startsWith("/security-check") || path.startsWith("/reissue")) {
            filterChain.doFilter(request, response);
            return;
        }

        // HTTP 헤더의 AUTHORIZATION 항목 추출
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 인증 결과가 없으면, 필터 생략
        if (authorization == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.replace("Bearer ", "");

        // Token Expired 되었는지 여부
        if (jwtUtil.isTokenExpired(token) || userService.isTokenBlacklisted(token)) {
            log.error("만료되었거나 블랙리스트에 포함된 토큰입니다.");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Token expired or invalidated\"}");
            return;
        }

        // Token에서 Claims 꺼내기
        Claims claims = jwtUtil.getClaims(authorization);

        // 회원 email 아이디와 권한을 Token에서 꺼내기
        String userEmail = claims.get("userEmail", String.class);
        String role = claims.get("role", String.class);
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

        // 권한 부여
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userEmail, null, Collections.singletonList(authority));

        // Detail을 넣어준다.
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // 다음 필터로 이동
        filterChain.doFilter(request, response);
    }
}