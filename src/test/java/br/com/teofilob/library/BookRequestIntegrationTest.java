package br.com.teofilob.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import br.com.teofilob.library.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookRequestIntegrationTest {
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper json;
    @Autowired private BookRepository repository;

    static Stream<Arguments> invalidFields() {
        return Stream.of(
                Arguments.of("title", null), Arguments.of("title", ""),
                Arguments.of("title", "   "), Arguments.of("title", "a".repeat(201)),
                Arguments.of("isbn", null), Arguments.of("isbn", ""),
                Arguments.of("isbn", "   "), Arguments.of("isbn", "abc"),
                Arguments.of("isbn", "9780306406158"), Arguments.of("isbn", "0306406153"),
                Arguments.of("isbn", "123456789012345678901"),
                Arguments.of("isbn", "9780306406157!"), Arguments.of("isbn", "978O306406157"),
                Arguments.of("isbn", "978030640615X"));
    }

    @ParameterizedTest
    @MethodSource("invalidFields")
    void rejectsInvalidFieldsWithoutWriting(String field, String value) throws Exception {
        Map<String, Object> request = new LinkedHashMap<>(Map.of("title", "Java", "isbn", "9780306406157"));
        request.put(field, value);
        long count = repository.count();
        mvc.perform(post("/api/v1/books").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(request))).andExpect(status().isBadRequest());
        assertEquals(count, repository.count());
    }

    @ParameterizedTest
    @CsvSource({"title", "isbn"})
    void rejectsMissingFields(String field) throws Exception {
        Map<String, String> request = new LinkedHashMap<>(Map.of("title", "Java", "isbn", "9780306406157"));
        request.remove(field);
        mvc.perform(post("/api/v1/books").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(request))).andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @CsvSource({"978-0-306-40615-7,9780306406157", "0-306-40615-2,0306406152", "0 8044 2957 x,080442957X"})
    void acceptsAndPersistsNormalizedIsbn(String input, String expected) throws Exception {
        String body = mvc.perform(post("/api/v1/books").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("title", "  Java  ", "isbn", input))))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.title").value("Java"))
                .andExpect(jsonPath("$.isbn").value(expected))
                .andReturn().getResponse().getContentAsString();
        long id = json.readTree(body).get("id").asLong();
        assertEquals(expected, repository.findById(id).orElseThrow().getIsbn());
    }

    @Test void acceptsTitleAtLimit() throws Exception {
        mvc.perform(post("/api/v1/books").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("title", "a".repeat(200), "isbn", "9780306406157"))))
                .andExpect(status().isCreated());
    }

    @Test void rejectsIdAndPreservesExistingBook() throws Exception {
        String body = mvc.perform(post("/api/v1/books").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Original\",\"isbn\":\"9780306406157\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id = json.readTree(body).get("id").asLong();
        long count = repository.count();
        mvc.perform(post("/api/v1/books").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("id", id, "title", "Changed", "isbn", "9780306406157"))))
                .andExpect(status().isBadRequest());
        assertEquals(count, repository.count());
        mvc.perform(get("/api/v1/books/{id}", id)).andExpect(jsonPath("$.title").value("Original"));
    }

    @Test void rejectsUnknownFields() throws Exception {
        mvc.perform(post("/api/v1/books").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Java\",\"isbn\":\"9780306406157\",\"extra\":true}"))
                .andExpect(status().isBadRequest());
    }

    @Test void deletingMissingBookReturnsNotFound() throws Exception {
        mvc.perform(delete("/api/v1/books/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
    }
}
