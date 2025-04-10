package hpclab.kcsatspringcommunity.question.dto;

import hpclab.kcsatspringcommunity.question.domain.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 문제 간이형 DTO 클래스입니다.
 * 각 항목 별 자세한 설명은 Question 클래스 참조.
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class QuestionDto {

    private QuestionType questionType;
    private String title;
    private String mainText;
    private List<String> choices;

    private String answer;
    private String translation;
    private String explanation;
}
