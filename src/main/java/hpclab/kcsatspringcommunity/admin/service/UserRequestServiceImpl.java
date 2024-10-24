package hpclab.kcsatspringcommunity.admin.service;


import hpclab.kcsatspringcommunity.admin.domain.UserRequest;
import hpclab.kcsatspringcommunity.admin.dto.RequestType;
import hpclab.kcsatspringcommunity.admin.dto.UserRequestRequestForm;
import hpclab.kcsatspringcommunity.admin.dto.UserRequestResponseForm;
import hpclab.kcsatspringcommunity.admin.repository.UserRequestRepository;
import hpclab.kcsatspringcommunity.community.domain.Member;
import hpclab.kcsatspringcommunity.community.repository.MemberRepository;
import hpclab.kcsatspringcommunity.question.repository.QuestionJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRequestServiceImpl implements UserRequestService {

    private final UserRequestRepository userRequestRepository;
    private final MemberRepository memberRepository;
    private final QuestionJPARepository questionRepository;
    private final QuestionJPARepository questionJPARepository;

    @Override
    public UserRequestResponseForm getQuestionErrorForm(Long qId, String email) {
        return UserRequestResponseForm.builder()
                .type(RequestType.QUESTION_ERROR)
                .content("QUESTION_ERROR")
                .question(questionJPARepository.findById(qId).orElseThrow(() -> new IllegalArgumentException("getQuestionErrorForm: 없는 문제입니다.")))
                .member(memberRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("getQuestionErrorForm: 없는 사람입니다.")))
                .build();
    }


    @Override
    public UserRequestResponseForm getImprovingForm(UserRequestRequestForm form, String email) {
        return UserRequestResponseForm.builder()
                .type(RequestType.IMPROVING)
                .content(form.getContent())
                .member(memberRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("getImprovingForm: 없는 사람입니다.")))
                .build();
    }

    @Override
    public UserRequestResponseForm getETCForm(UserRequestRequestForm form, String email) {
        return UserRequestResponseForm.builder()
                .type(RequestType.ETC)
                .content(form.getContent())
                .member(memberRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("getETCForm: 없는 사람입니다.")))
                .build();
    }

    @Override
    public UserRequestResponseForm saveUserRequest(UserRequestResponseForm form, String email) {

        if (form.getQuestion() == null) {
            userRequestRepository.save(UserRequest.builder()
                    .type(form.getType())
                    .content(form.getContent())
                    .username(email)
                    .qId(0L)
                    .build());
        }
        else {
            userRequestRepository.save(UserRequest.builder()
                    .type(form.getType())
                    .content(form.getContent())
                    .username(email)
                    .qId(form.getQuestion().getId())
                    .build());
        }

        return form;
    }

    @Override
    public List<UserRequestResponseForm> getUserRequests() {
        List<UserRequest> requests = userRequestRepository.findAll();
        List<UserRequestResponseForm> forms = new ArrayList<>();

        for (UserRequest request : requests) {
            UserRequestResponseForm form;

            Member member = memberRepository.findByEmail(request.getUsername()).orElseThrow(() -> new UsernameNotFoundException("getUserRequests: 없는 사람입니다."));

            if (request.getQId() == 0L) {
                form = UserRequestResponseForm.builder()
                        .type(request.getType())
                        .content(request.getContent())
                        .member(member)
                        .build();
            }
            else {
                form = UserRequestResponseForm.builder()
                        .type(request.getType())
                        .content(request.getContent())
                        .member(member)
                        .question(questionRepository.findById(request.getQId()).orElseThrow(() -> new IllegalArgumentException("getUserRequests: 없는 문제입니다.")))
                        .build();
            }

            forms.add(form);
        }

        return forms;
    }
}
