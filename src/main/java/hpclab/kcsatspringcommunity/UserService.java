package hpclab.kcsatspringcommunity;

import hpclab.kcsatspringcommunity.community.domain.Member;
import hpclab.kcsatspringcommunity.community.domain.Role;
import hpclab.kcsatspringcommunity.community.dto.MemberSignInForm;
import hpclab.kcsatspringcommunity.community.repository.MemberRepository;
import hpclab.kcsatspringcommunity.exception.ApiException;
import hpclab.kcsatspringcommunity.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * 회원 인증 관련 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private static final String BLACKLIST_PREFIX = "blacklist:";
    private static final Long EXPIRED_MS = 1000 * 60 * 60L;

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 회원 로그인 처리 메서드입니다.
     * @param form 로그인 포맷
     * @return JWT 토큰 발급
     */
    @Transactional(readOnly = true)
    public String login(MemberSignInForm form) {
        Member member = memberRepository.findByEmail(form.getUserEmail())
                .orElseThrow(() -> new ApiException(ErrorCode.LOGIN_FAILED));

        String encodedPassword = member.getPassword();
        String rawPassword = form.getPassword();

        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new ApiException(ErrorCode.LOGIN_FAILED);
        }

        return jwtUtil.generateToken(member.getEmail(), member.getUsername(), member.getRole());
    }

    /**
     * 게스트 로그인 처리 메서드입니다.
     * @return JWT 토큰 발급
     */
    public String guestLogin() {
        return jwtUtil.generateToken(null, null, Role.ROLE_GUEST);
    }

    public void logout(String token) {
        String noHeaderToken = token.replace("Bearer ", "");

        // 토큰을 블랙리스트에 저장 (만료 시간까지 유지)
        redisTemplate.opsForValue()
                .set(BLACKLIST_PREFIX + noHeaderToken, "true", jwtUtil.getExpiration(token), TimeUnit.MILLISECONDS);
    }

    public boolean isTokenBlacklisted(String token) {
        String noHeaderToken = token.replace("Bearer ", "");
        return redisTemplate.hasKey(BLACKLIST_PREFIX + noHeaderToken);
    }
}
