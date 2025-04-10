package hpclab.kcsatspringcommunity;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // HTTP 헤더의 AUTHORIZATION 항목 추출
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        logger.info("authorization = " + authorization);

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

        // 인증 결과가 없으면, 필터 생략
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            logger.error("authorization 이 없습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        // Token에서 Claims 꺼내기
        Claims claims = jwtUtil.getClaims(authorization);

        // Token Expired 되었는지 여부  || userService.isTokenBlacklisted(authorization)
        if (claims.getExpiration().before(new Date())) {
            logger.error("Token 이 만료되었습니다.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Token expired\"}");
            return;
        }

        /**
         * (수정 필요)
         * 회원 email 아이디를 JWT에 저장하면 보안상 큰 문제 발생 여지가 있음.
         * Redis에 회원키:email 아이디 이런 형태로 매핑하여 저장하는 로직 도입 필요.
         */
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