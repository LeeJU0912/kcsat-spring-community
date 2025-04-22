package hpclab.kcsatspringcommunity.myBook.dto;

import hpclab.kcsatspringcommunity.myBook.domain.Book;
import hpclab.kcsatspringcommunity.myBook.domain.BookQuestion;
import hpclab.kcsatspringcommunity.question.domain.Choice;
import hpclab.kcsatspringcommunity.question.domain.Question;
import hpclab.kcsatspringcommunity.question.dto.QuestionResponseForm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 나만의 문제 반환 DTO 클래스입니다.
 */
@Data
@AllArgsConstructor
public class BookResponseForm {

    /**
     * 문제 DTO 모음
     */
    private List<QuestionResponseForm> question;

    @Builder
    public BookResponseForm(Book book) {
        this.question = book.getBookQuestions().stream()
                .map(BookQuestion::getQuestion)
                .map(this::questionToDTO)
                .toList();
    }

    /**
     * Question -> QuestionDTO 형태로 변환하는 메서드입니다.
     * @param question 순수한 Question 질문 객체
     * @return QuestionDTO 객체로 변환하여 데이터 정합성을 보장합니다.
     */
    private QuestionResponseForm questionToDTO(Question question) {

        return QuestionResponseForm.builder()
                .qId(question.getId())
                .questionType(question.getType().getKrName())
                .title(question.getTitle())
                .mainText(question.getMainText())
                .choices(question.getChoices().stream().map(Choice::getChoice).toList())
                .shareCounter(question.getShareCounter())
                .createdDate(question.getCreatedDate())
                .build();
    }
}