package hpclab.kcsatspringcommunity.myBook.dto;

import hpclab.kcsatspringcommunity.question.dto.QuestionResponseForm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 나만의 문제 반환 DTO 클래스입니다.
 */
@Data
@Builder
@AllArgsConstructor
public class BookResponseForm {

    /**
     * 문제 DTO 모음
     */
    private List<QuestionResponseForm> question;
}