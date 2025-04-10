package hpclab.kcsatspringcommunity.questionRank.service;

import hpclab.kcsatspringcommunity.question.dto.QuestionResponseForm;

import java.util.List;

/**
 * 주간 문제 랭킹과 관련된 로직을 정의한 인터페이스입니다.
 */
public interface QuestionRankService {

    /**
     * 주간 인기 문제를 가져오는 메서드입니다.
     * 최대 5개까지 가져와서 반환합니다.
     *
     * @return 인기 문제 max 5개 목록으로 반환.
     */
    List<QuestionResponseForm> getRankedQuestions();

    /**
     * 매주 월요일 0시에 인기 문제를 계산합니다.
     * 계산된 문제는 Redis에 저장되어 캐시화됩니다.
     */
    void updateQuestionRank();
}
