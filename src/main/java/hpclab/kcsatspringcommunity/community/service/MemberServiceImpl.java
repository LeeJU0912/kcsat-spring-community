package hpclab.kcsatspringcommunity.community.service;

import hpclab.kcsatspringcommunity.community.domain.Member;
import hpclab.kcsatspringcommunity.community.domain.Role;
import hpclab.kcsatspringcommunity.community.dto.MemberSignUpForm;
import hpclab.kcsatspringcommunity.community.dto.MemberResponseForm;
import hpclab.kcsatspringcommunity.community.repository.MemberRepository;
import hpclab.kcsatspringcommunity.myBook.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void signUp(MemberSignUpForm memberSignUpForm) {
        if (memberRepository.existsByEmail(memberSignUpForm.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일 입니다.");
        }

        Member member = Member.builder()
                .email(memberSignUpForm.getEmail())
                .username(memberSignUpForm.getUsername())
                .password(passwordEncoder.encode(memberSignUpForm.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        redisTemplate.opsForValue().set(memberSignUpForm.getEmail(), memberSignUpForm.getUsername());

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
        return memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 계정입니다."));
    }

    @Transactional(readOnly = true)
    @Override
    public Member findMemberById(Long mId) {
        return memberRepository.findById(mId).orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 회원입니다."));
    }
}
