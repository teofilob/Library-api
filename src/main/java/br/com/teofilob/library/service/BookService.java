package br.com.teofilob.library.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.teofilob.library.dto.BookResponse;
import br.com.teofilob.library.dto.CreateBookRequest;
import br.com.teofilob.library.entity.Book;
import br.com.teofilob.library.exception.BookNotFoundException;
import br.com.teofilob.library.mapper.BookMapper;
import br.com.teofilob.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookMapper bookMapper = BookMapper.INSTANCE;
    private final BookRepository bookRepository;

    public BookResponse save(CreateBookRequest request) {
        Book book = bookMapper.toModel(request);
        return bookMapper.toDTO(bookRepository.save(book));
    }

    public List<BookResponse> listAll() {
        return bookRepository.findAll().stream().map(bookMapper::toDTO).toList();
    }

    public BookResponse getById(Long id) throws BookNotFoundException {
        return bookMapper.toDTO(findBook(id));
    }

    @Transactional
    public void delete(long id) throws BookNotFoundException {
        bookRepository.delete(findBook(id));
    }

    private Book findBook(long id) throws BookNotFoundException {
        return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }
}
