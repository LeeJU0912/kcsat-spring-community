package hpclab.kcsatspringcommunity.question.dto;

import hpclab.kcsatspringcommunity.question.domain.Choice;
import hpclab.kcsatspringcommunity.question.domain.Question;
import hpclab.kcsatspringcommunity.question.domain.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 문제 상세 DTO 클래스입니다.
 * 각 항목 별 자세한 설명은 Question 클래스 참조.
 */
@Data
@Builder
@AllArgsConstructor
public class QuestionDetailsDto {
    private Long qId;

    private QuestionType questionType;
    private String title;
    private String mainText;
    private List<String> choices;

    private String answer;
    private String translation;
    private String explanation;

    private Long shareCounter;

    @Builder
    public QuestionDetailsDto(Question question) {
        this.qId = question.getId();
        this.questionType = question.getType();
        this.title = question.getTitle();
        this.mainText = question.getMainText();
        this.choices = question.getChoices().stream().map(Choice::getChoice).toList();
        this.answer = question.getAnswer();
        this.translation = question.getTranslation();
        this.explanation = question.getExplanation();
        this.shareCounter = question.getShareCounter();
    }
}
