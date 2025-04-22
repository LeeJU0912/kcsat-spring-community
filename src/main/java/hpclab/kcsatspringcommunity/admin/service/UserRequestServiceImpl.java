package hpclab.kcsatspringcommunity.admin.service;


import hpclab.kcsatspringcommunity.admin.domain.UserRequest;
import hpclab.kcsatspringcommunity.admin.domain.RequestType;
import hpclab.kcsatspringcommunity.admin.dto.UserRequestRequestForm;
import hpclab.kcsatspringcommunity.admin.dto.UserRequestResponseForm;
import hpclab.kcsatspringcommunity.admin.repository.UserRequestRepository;
import hpclab.kcsatspringcommunity.community.domain.Member;
import hpclab.kcsatspringcommunity.community.service.MemberService;
import hpclab.kcsatspringcommunity.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * UserRequestService 구현체입니다. @Override 메서드 설명은 인터페이스 참조.
 */
@Service
@RequiredArgsConstructor
public class UserRequestServiceImpl implements UserRequestService {

    private final UserRequestRepository userRequestRepository;

    private final MemberService memberService;
    private final QuestionService questionService;

    private static final String QUESTION_ERROR = "QUESTION_ERROR";

    @Transactional(readOnly = true)
    @Override
    public UserRequestResponseForm getQuestionErrorForm(Long qId, String email) {
        return UserRequestResponseForm.builder()
                .type(RequestType.QUESTION_ERROR)
                .content(QUESTION_ERROR)
                .question(questionService.getQuestion(qId))
                .member(memberService.findMemberByEmail(email))
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public UserRequestResponseForm getImprovingForm(UserRequestRequestForm form, String email) {
        return UserRequestResponseForm.builder()
                .type(RequestType.IMPROVING)
                .content(form.getContent())
                .member(memberService.findMemberByEmail(email))
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public UserRequestResponseForm getETCForm(UserRequestRequestForm form, String email) {
        return UserRequestResponseForm.builder()
                .type(RequestType.ETC)
                .content(form.getContent())
                .member(memberService.findMemberByEmail(email))
                .build();
    }

    @Transactional
    @Override
    public UserRequestResponseForm updateUserRequestForm(UserRequestResponseForm form, String email) {

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

    @Transactional(readOnly = true)
    @Override
    public List<UserRequestResponseForm> getUserRequests() {
        List<UserRequest> requests = userRequestRepository.findAll();
        List<UserRequestResponseForm> forms = new ArrayList<>();

        for (UserRequest request : requests) {
            UserRequestResponseForm form;

            Member member = memberService.findMemberByEmail(request.getUsername());

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
                        .question(questionService.getQuestion(request.getQId()))
                        .build();
            }

            forms.add(form);
        }

        return forms;
    }
}
