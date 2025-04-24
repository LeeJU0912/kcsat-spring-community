package hpclab.kcsatspringcommunity;

import hpclab.kcsatspringcommunity.community.domain.Member;
import hpclab.kcsatspringcommunity.community.dto.MemberAuthResponseForm;
import hpclab.kcsatspringcommunity.community.dto.MemberSignInForm;
import hpclab.kcsatspringcommunity.community.repository.MemberRepository;
import hpclab.kcsatspringcommunity.exception.ApiException;
import hpclab.kcsatspringcommunity.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원 인증 관련 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 회원 로그인 처리 메서드입니다.
     * @param form 로그인 포맷
     * @return JWT 토큰 발급
     */
    @Transactional(readOnly = true)
    public MemberAuthResponseForm login(MemberSignInForm form) {
        Member member = memberRepository.findByEmail(form.getUserEmail())
                .orElseThrow(() -> new ApiException(ErrorCode.LOGIN_FAILED));

        String encodedPassword = member.getPassword();
        String rawPassword = form.getPassword();

        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new ApiException(ErrorCode.LOGIN_FAILED);
        }

        return new MemberAuthResponseForm(member);
    }
}
