package br.com.teofilob.library.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import br.com.teofilob.library.dto.BookResponse;
import br.com.teofilob.library.dto.CreateBookRequest;
import br.com.teofilob.library.entity.Book;
import br.com.teofilob.library.exception.BookNotFoundException;
import br.com.teofilob.library.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock private BookRepository repository;
    @InjectMocks private BookService service;

    private Book book() { return new Book(7L, "Java", "9780306406157"); }

    @Test void readsBook() throws Exception {
        when(repository.findById(7L)).thenReturn(Optional.of(book()));
        assertEquals(new BookResponse(7L, "Java", "9780306406157"), service.getById(7L));
    }

    @Test void missingBookReturnsNotFound() {
        assertThrows(BookNotFoundException.class, () -> service.getById(7L));
    }

    @Test void listsBooks() {
        when(repository.findAll()).thenReturn(List.of(book()));
        assertEquals(List.of(new BookResponse(7L, "Java", "9780306406157")), service.listAll());
    }

    @Test void listsEmptyRepository() {
        when(repository.findAll()).thenReturn(List.of());
        assertTrue(service.listAll().isEmpty());
    }

    @Test void createsWithGeneratedIdAndNormalizedFields() {
        when(repository.save(any(Book.class))).thenReturn(book());
        BookResponse response = service.save(new CreateBookRequest(" Java ", "978-0-306-40615-7"));
        ArgumentCaptor<Book> captured = ArgumentCaptor.forClass(Book.class);
        verify(repository).save(captured.capture());
        assertEquals(0L, captured.getValue().getId());
        assertEquals("Java", captured.getValue().getTitle());
        assertEquals("9780306406157", captured.getValue().getIsbn());
        assertEquals(7L, response.id());
    }

    @Test void deletesById() throws Exception {
        Book book = book();
        when(repository.findById(7L)).thenReturn(Optional.of(book));
        service.delete(7L);
        verify(repository).delete(book);
    }

    @Test void doesNotDeleteMissingBook() {
        assertThrows(BookNotFoundException.class, () -> service.delete(7L));
        verify(repository, never()).delete(any());
    }
}
