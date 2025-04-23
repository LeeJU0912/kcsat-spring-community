package hpclab.kcsatspringcommunity;

import hpclab.kcsatspringcommunity.community.domain.Member;
import hpclab.kcsatspringcommunity.community.dto.MemberSignInForm;
import hpclab.kcsatspringcommunity.community.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * (수정 필요)
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
        Member member = memberRepository.findByEmail(form.getUserEmail()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        String encodedPassword = member.getPassword();
        String rawPassword = form.getPassword();

        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        return jwtUtil.generateToken(member.getEmail(), member.getUsername(), member.getRole());
    }

    public void logout(String token, long expiration) {
        // 토큰을 블랙리스트에 저장 (만료 시간까지 유지)
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "true", expiration, TimeUnit.MILLISECONDS);
    }

    public void removeFromBlacklist(String token) {
        redisTemplate.delete(BLACKLIST_PREFIX + token); // Redis에서 블랙리스트 삭제
    }

    public boolean isTokenBlacklisted(String token) {
        String noHeaderToken = token.split(" ")[1];
        return redisTemplate.hasKey(BLACKLIST_PREFIX + noHeaderToken);
    }
}
