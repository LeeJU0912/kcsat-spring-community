package hpclab.kcsatspringcommunity.myBook.service;

import hpclab.kcsatspringcommunity.exception.ApiException;
import hpclab.kcsatspringcommunity.exception.ErrorCode;
import hpclab.kcsatspringcommunity.myBook.domain.Book;
import hpclab.kcsatspringcommunity.myBook.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원별 나만의 문제집 생성/조회 로직을 구현한 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Transactional
    @Override
    public void makeBook(String userEmail) {
        Book book = new Book(userEmail);
        bookRepository.save(book);
    }

    @Transactional(readOnly = true)
    @Override
    public Book findBook(String userEmail) {
        return bookRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApiException(ErrorCode.MYBOOK_NOT_FOUND));
    }
}