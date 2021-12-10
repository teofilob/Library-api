package br.com.teofilob.library.controller;

import static br.com.teofilob.library.util.JsonConvertionUtils.asJsonString;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import br.com.teofilob.library.builder.BookDTOBuilder;
import br.com.teofilob.library.controllers.BookController;
import br.com.teofilob.library.dto.BookDTO;
import br.com.teofilob.library.exception.BookNotFoundException;
import br.com.teofilob.library.service.BookService;

@ExtendWith(MockitoExtension.class)
public class BookControllerTest {
	
	private static final String BOOK_API_URI_PATH = "/api/v1/books";
	
	private static long VALID_BOOK_ID = 1l;
	private static long INVALID_BOOK_ID = 3l;
	
	private MockMvc mockMvc;
	
	@InjectMocks
	private BookController bookController;
	
	@Mock
	private BookService bookService;
	
	@BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
	}
	
	@Test
	public void whenGetListAllReturnOk() throws Exception {
		BookDTO book = BookDTOBuilder.builder().build().toBookDTO();
		List<BookDTO> lista =  Collections.singletonList(book);
		when( this.bookService.listAll() ).thenReturn(lista);
		
		this.mockMvc.perform( get(BOOK_API_URI_PATH)
				.contentType(MediaType.APPLICATION_JSON)
				.content( asJsonString(book)))
			.andExpect( status().isOk() )
			.andExpect(jsonPath("$[0].title", is(book.getTitle())))
			.andExpect(jsonPath("$[0].isbn", is(book.getIsbn())));
		
	}
	
	@Test
	public void whenPostIsCalledThenBookisCreated() throws Exception {
		BookDTO book = BookDTOBuilder.builder().build().toBookDTO();
		
		when( this.bookService.save(book) ).thenReturn(book);
		
		this.mockMvc.perform(post(BOOK_API_URI_PATH)
				.contentType(MediaType.APPLICATION_JSON)
				.content( asJsonString(book)))
			.andExpect( status().isCreated() )
			.andExpect(jsonPath("$.title", is(book.getTitle())))
			.andExpect(jsonPath("$.isbn", is(book.getIsbn())));
		
		
	}
	
	@Test
	public void whenGetByPathIdBookValidReturnOk() throws Exception {
		BookDTO book = BookDTOBuilder.builder().build().toBookDTO();
		
		when( this.bookService.getById( anyLong() )).thenReturn(book);
		
		this.mockMvc.perform( get(BOOK_API_URI_PATH+"/"+VALID_BOOK_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content( asJsonString(book)))
			.andExpect( status().isOk() )
			.andExpect(jsonPath("$.title", is(book.getTitle())))
			.andExpect(jsonPath("$.isbn", is(book.getIsbn())));
		
	}
	
	@Test
	public void whenGetByPathIdBookInvalidReturnNotFound() throws Exception {
		BookDTO book = BookDTOBuilder.builder().build().toBookDTO();
		
		when( this.bookService.getById( anyLong() )).thenThrow(BookNotFoundException.class);
		
		this.mockMvc.perform( get(BOOK_API_URI_PATH+"/"+INVALID_BOOK_ID)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect( status().isNotFound() );
		
	}
	
	@Test
	public void whenDeleteByPathIdBookValidReturnNoContent() throws Exception{
		BookDTO book = BookDTOBuilder.builder().build().toBookDTO();
	
		when( this.bookService.delete(book)).thenReturn( true );
		
		
		this.mockMvc.perform( delete(BOOK_API_URI_PATH+"/"+VALID_BOOK_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content( asJsonString(book)))
			.andExpect( status().isNoContent() );
		
	}
	
	@Test
	public void whenDeleteByPathIdBookInvalidReturnBadRequest() throws Exception{
		BookDTO book = BookDTOBuilder.builder().build().toBookDTO();
	
		this.mockMvc.perform( delete(BOOK_API_URI_PATH+"/"+INVALID_BOOK_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content( asJsonString(book)))
			.andExpect( status().isBadRequest());
		
	}
	
}
