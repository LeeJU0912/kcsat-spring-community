package hpclab.kcsatspringcommunity.questionRank.service;

import hpclab.kcsatspringcommunity.RedisKeyUtil;
import hpclab.kcsatspringcommunity.exception.ApiException;
import hpclab.kcsatspringcommunity.exception.ErrorCode;
import hpclab.kcsatspringcommunity.question.domain.Choice;
import hpclab.kcsatspringcommunity.question.domain.Question;
import hpclab.kcsatspringcommunity.question.dto.QuestionResponseForm;
import hpclab.kcsatspringcommunity.question.repository.QuestionJPARepository;
import hpclab.kcsatspringcommunity.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.log10;
import static java.lang.Math.min;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionRankServiceImpl implements QuestionRankService {

    private final QuestionJPARepository questionJPARepository;

    private final RedisTemplate<String, String> redisTemplate;

    @Transactional(readOnly = true)
    @Override
    public List<QuestionResponseForm> getRankedQuestions() {
        List<QuestionResponseForm> questions = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            String qIdString = redisTemplate.opsForValue().get(RedisKeyUtil.questionRank(i));
            if (qIdString == null) {
                break;
            }

            Long qId = Long.parseLong(qIdString);

            Question question = questionJPARepository.findWithChoicesById(qId)
                    .orElseThrow(() -> new ApiException(ErrorCode.QUESTION_NOT_FOUND));

            questions.add(QuestionResponseForm.builder()
                    .qId(question.getId())
                    .questionType(question.getType().getKrName())
                    .title(question.getTitle())
                    .mainText(question.getMainText())
                    .choices(question.getChoices().stream().map(Choice::getChoice).toList())
                    .shareCounter(question.getShareCounter())
                    .createdDate(question.getCreatedDate())
                    .build());
        }

        return questions;
    }

    @Scheduled(cron = "0 0 0 ? * MON", zone = "Asia/Seoul")
    @Transactional(readOnly = true)
    @Override
    public void updateQuestionRank() {
        log.info("cron update question rank");

        List<Question> questions = questionJPARepository.findAllByShareCounterGreaterThan(0L);

        questions.sort((o1, o2) ->
                Double.compare(redditRankingAlgorithm(Double.valueOf(o2.getShareCounter()), o2.getCreatedDate()),
                        redditRankingAlgorithm(Double.valueOf(o1.getShareCounter()), o1.getCreatedDate())));

        for (int i = 1; i <= min(questions.size(), 5); i++) {
            redisTemplate.opsForValue().set(RedisKeyUtil.questionRank(i), String.valueOf(questions.get(i - 1).getId()));
        }
    }

    /**
     * RedditRankingAlgorithm에 따른 선호도 갱신
     * (log10(추천 수) + 작성 시간 내림차순) 공식으로 계산
     *
     * @param up 문제 공유 수
     * @param time 문제 생성 시각
     * @return 추천수 + (시간 변환값) 으로 결과 반환
     */
    private Double redditRankingAlgorithm(double up, LocalDateTime time) {
        double convertedTime = (double) time.atZone(ZoneOffset.UTC).toEpochSecond();
        return log10(up) + convertedTime / 45000;
    }
}