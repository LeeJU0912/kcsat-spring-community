package hpclab.kcsatspringcommunity.myBook.service;

import hpclab.kcsatspringcommunity.myBook.domain.Book;
import hpclab.kcsatspringcommunity.myBook.domain.BookQuestion;
import hpclab.kcsatspringcommunity.myBook.dto.BookResponseForm;
import hpclab.kcsatspringcommunity.myBook.repository.BookRepository;
import hpclab.kcsatspringcommunity.question.domain.Choice;
import hpclab.kcsatspringcommunity.question.domain.Question;
import hpclab.kcsatspringcommunity.question.dto.QuestionResponseForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 회원별 나만의 문제집 생성/조회 로직을 구현한 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    public void makeBook(String userEmail) {
        Book book = new Book(userEmail);
        bookRepository.save(book);
    }

    @Override
    public BookResponseForm findBook(String userEmail) {
        return BookToBookResponseForm(bookRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("Member not found")));
    }

    /**
     * Book -> BookDTO 형태로 변환하는 메서드입니다.
     * @param book 순수한 Book 질문 객체
     * @return BookDTO 객체로 변환하여 데이터 정합성을 보장합니다.
     */
    private BookResponseForm BookToBookResponseForm(Book book) {
        return BookResponseForm.builder()
                .question(book.getBookQuestions().stream()
                        .map(BookQuestion::getQuestion)
                        .map(this::questionToDto)
                        .toList())
                .build();
    }

    /**
     * Question -> QuestionDTO 형태로 변환하는 메서드입니다.
     * @param question 순수한 Question 질문 객체
     * @return QuestionDTO 객체로 변환하여 데이터 정합성을 보장합니다.
     */
    private QuestionResponseForm questionToDto(Question question) {

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
