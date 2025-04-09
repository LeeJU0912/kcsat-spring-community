package hpclab.kcsatspringcommunity.questionRank.service;

import hpclab.kcsatspringcommunity.question.domain.Choice;
import hpclab.kcsatspringcommunity.question.domain.Question;
import hpclab.kcsatspringcommunity.question.dto.QuestionResponseForm;
import hpclab.kcsatspringcommunity.question.repository.QuestionJPARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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

    private final RedisTemplate<String, String> redisTemplate;
    private final QuestionJPARepository questionJPARepository;


    @Override
    public List<QuestionResponseForm> getRankedQuestions() {
        List<QuestionResponseForm> questions = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            String qIdString = redisTemplate.opsForValue().get("question:rank:" + i);
            if (qIdString == null) {
                break;
            }

            Long qId = Long.parseLong(qIdString);

            Question question = questionJPARepository.findById(qId).orElseThrow(() -> new IllegalArgumentException("없는 문제입니다."));

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


    @Scheduled(cron = "0 0 0 ? * MON", zone = "Asia/Seoul") // 월요일 자정마다 실행
    public void updateQuestionRank() {
        log.info("cron update question rank");

        List<Question> questions = questionJPARepository.findAllByShareCounterGreaterThan(0L);

        questions.sort((o1, o2) ->
                Double.compare(redditRankingAlgorithm(Double.valueOf(o2.getShareCounter()), o2.getCreatedDate()),
                        redditRankingAlgorithm(Double.valueOf(o1.getShareCounter()), o1.getCreatedDate())));

        for (int i = 1; i <= min(questions.size(), 5); i++) {
            redisTemplate.opsForValue().set("question:rank:" + i, String.valueOf(questions.get(i - 1).getId()));
        }
    }


    private Double redditRankingAlgorithm(double up, LocalDateTime time) {
        double convertedTime = (double) time.atZone(ZoneOffset.UTC).toEpochSecond();
        return log10(up) + convertedTime / 45000;
    }
}
