package hpclab.kcsatspringcommunity.community.service;

import hpclab.kcsatspringcommunity.redis.RedisKeyUtil;
import hpclab.kcsatspringcommunity.community.domain.Member;
import hpclab.kcsatspringcommunity.community.domain.Role;
import hpclab.kcsatspringcommunity.community.dto.MemberResponseForm;
import hpclab.kcsatspringcommunity.community.dto.MemberSignUpForm;
import hpclab.kcsatspringcommunity.community.repository.MemberRepository;
import hpclab.kcsatspringcommunity.exception.ApiException;
import hpclab.kcsatspringcommunity.exception.ErrorCode;
import hpclab.kcsatspringcommunity.myBook.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


/**
 * 회원 정보 처리 로직을 담당하는 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final BookService bookService;
    private final PasswordEncoder passwordEncoder;

    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    @Override
    public void signUp(MemberSignUpForm form) {
        String signupLockKey = RedisKeyUtil.userIdemCheck(form.getEmail());

        Boolean lockAcquired = redisTemplate.opsForValue()
                .setIfAbsent(signupLockKey, "locked", Duration.ofMinutes(5));

        if (Boolean.FALSE.equals(lockAcquired)) {
            throw new ApiException(ErrorCode.SIGNUP_IN_PROGRESS);
        }

        if (memberRepository.existsByEmail(form.getEmail())) {
            throw new ApiException(ErrorCode.DUPLICATE_EMAIL);
        }

        Member member = Member.builder()
                .email(form.getEmail())
                .username(form.getUsername())
                .password(passwordEncoder.encode(form.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        redisTemplate.opsForValue().set(form.getEmail(), form.getUsername());

        memberRepository.save(member);
        bookService.makeBook(member.getEmail());
    }

    @Transactional(readOnly = true)
    @Override
    public List<MemberResponseForm> findMembers() {
        List<Member> all = memberRepository.findAll();
        List<MemberResponseForm> members = new ArrayList<>();

        all.forEach(member -> members.add(new MemberResponseForm(member)));

        return members;
    }

    @Override
    public String findUsername(String userEmail) {
        return redisTemplate.opsForValue().get(userEmail);
    }

    @Transactional(readOnly = true)
    @Override
    public Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    @Override
    public Member findMemberById(Long mId) {
        return memberRepository.findById(mId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
    }
}
