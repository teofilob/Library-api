package br.com.teofilob.library.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import br.com.teofilob.library.controllers.BookController;
import br.com.teofilob.library.dto.BookResponse;
import br.com.teofilob.library.dto.CreateBookRequest;
import br.com.teofilob.library.exception.BookNotFoundException;
import br.com.teofilob.library.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {
    @Mock private BookService service;
    @InjectMocks private BookController controller;
    private MockMvc mvc;
    private final BookResponse book = new BookResponse(1L, "Java", "9780306406157");

    @BeforeEach void setup() { mvc = MockMvcBuilders.standaloneSetup(controller).build(); }

    @Test void createsBook() throws Exception {
        when(service.save(new CreateBookRequest("Java", "9780306406157"))).thenReturn(book);
        mvc.perform(post("/api/v1/books").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Java\",\"isbn\":\"9780306406157\"}"))
                .andExpect(status().isCreated()).andExpect(header().string("Location", "/api/v1/books/1"))
                .andExpect(jsonPath("$.id", is(1))).andExpect(jsonPath("$.title", is("Java")));
    }

    @Test void listsBooks() throws Exception {
        when(service.listAll()).thenReturn(List.of(book));
        mvc.perform(get("/api/v1/books")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].isbn", is(book.isbn())));
    }

    @Test void readsBook() throws Exception {
        when(service.getById(1L)).thenReturn(book);
        mvc.perform(get("/api/v1/books/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(book.title())));
    }

    @Test void missingBookReturnsNotFound() throws Exception {
        when(service.getById(1L)).thenThrow(new BookNotFoundException(1L));
        mvc.perform(get("/api/v1/books/1")).andExpect(status().isNotFound());
    }

    @Test void deletesWithoutBody() throws Exception {
        mvc.perform(delete("/api/v1/books/1")).andExpect(status().isNoContent());
        verify(service).delete(1L);
    }

    @Test void missingDeleteReturnsNotFound() throws Exception {
        doThrow(new BookNotFoundException(1L)).when(service).delete(1L);
        mvc.perform(delete("/api/v1/books/1")).andExpect(status().isNotFound());
    }
}
