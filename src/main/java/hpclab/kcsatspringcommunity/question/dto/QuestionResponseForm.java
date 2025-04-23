package hpclab.kcsatspringcommunity.question.dto;

import hpclab.kcsatspringcommunity.question.domain.Choice;
import hpclab.kcsatspringcommunity.question.domain.Question;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 문제 저장시 정보를 담는 DTO 클래스입니다.
 * 각 항목 별 자세한 설명은 Question 클래스 참조.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponseForm {

    private Long qId;

    private String questionType;

    private String title;
    private String mainText;
    private List<String> choices;

    private LocalDateTime createdDate;

    private Long shareCounter;

    @Builder
    public QuestionResponseForm(Question question) {
        this.qId = question.getId();
        this.questionType = question.getType().getKrName();
        this.title = question.getTitle();
        this.mainText = question.getMainText();
        this.choices = question.getChoices().stream().map(Choice::getChoice).toList();
        this.createdDate = question.getCreatedDate();
        this.shareCounter = question.getShareCounter();
    }
}