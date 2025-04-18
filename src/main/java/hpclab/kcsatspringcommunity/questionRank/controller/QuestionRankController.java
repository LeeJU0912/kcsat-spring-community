package hpclab.kcsatspringcommunity.questionRank.controller;

import hpclab.kcsatspringcommunity.question.dto.QuestionResponseForm;
import hpclab.kcsatspringcommunity.questionRank.service.QuestionRankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 주간 인기 문제 랭킹을 가져오는 컨트롤러 클래스입니다.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class QuestionRankController {

    private final QuestionRankService questionRankService;

    /**
     * 주간 인기 문제를 가져오는 메서드입니다.
     *
     * @return 가장 인기 있는 문제 5개를 선별하여 목록으로 반환합니다.
     */
    @GetMapping("/api/community/open/weekly")
    public ResponseEntity<List<QuestionResponseForm>> weeklyQuestionRank() {
        log.info("weekly question rank");

        // weekly Question 5개 랭킹 순서대로 가져 와서 모델에 넣기.
        return ResponseEntity.ok(questionRankService.getRankedQuestions());
    }
}
