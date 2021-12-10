package br.com.teofilob.library.service;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.teofilob.library.builder.BookDTOBuilder;
import br.com.teofilob.library.dto.BookDTO;
import br.com.teofilob.library.entity.Book;
import br.com.teofilob.library.exception.BookNoContentErrorException;
import br.com.teofilob.library.exception.BookNotFoundException;
import br.com.teofilob.library.mapper.BookMapper;
import br.com.teofilob.library.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

	@Mock
	private BookRepository bookRepository; 
	
	@InjectMocks
	public BookService bookService;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	
	@Test
	public void findById() throws BookNotFoundException {
		BookDTO bookDto = BookDTOBuilder.builder().build().toBookDTO();
		Book book = BookMapper.INSTANCE.toModel(bookDto);
		Optional<Book> optional = Optional.of(book);
		
		when( this.bookRepository.findById(bookDto.getId()) ).thenReturn( optional );
		
		BookDTO bookDtoCriated = this.bookService.getById(bookDto.getId());
		
		assertEquals(bookDto.getId(), bookDtoCriated.getId());
		assertEquals(bookDto.getTitle(), bookDtoCriated.getTitle());
		assertEquals(bookDto.getIsbn(), bookDtoCriated.getIsbn());
	}
	
	@Test
	public void findAllReturnListEmpty() {
	
		when( this.bookRepository.findAll() ).thenReturn( Collections.EMPTY_LIST );
		
		List<BookDTO> list = bookService.listAll();
		
		assertEquals(list.size(), 0);
		
	}
	
	@Test
	public void findAllWithReturnList() {
		BookDTO bookDto = BookDTOBuilder.builder().build().toBookDTO();
		Book book = BookMapper.INSTANCE.toModel(bookDto);
		List listModel = Arrays.asList(book, book,book);

		
		when( this.bookRepository.findAll() ).thenReturn( listModel );
		
		List<BookDTO> listDto = bookService.listAll();
		
		assertEquals(listDto.size(), 3);
	}
	
	@Test
	public void saveNewObject() {
		
		BookDTO  bookDto = BookDTOBuilder.builder().build().toBookDTO();
				
		bookDto.setId(0);
		
		Book book = BookMapper.INSTANCE.toModel(bookDto);
		book.setId(0);
		
		Book bookReturn = BookMapper.INSTANCE.toModel(BookDTOBuilder.builder().build().toBookDTO());
		
		
		when( this.bookRepository.save( book ) ).thenReturn(  bookReturn  );
		
		BookDTO bookSaved =	this.bookService.save(bookDto);
		
		assertEquals( bookSaved.getId() , bookReturn.getId());
		assertEquals( bookSaved.getTitle() , bookReturn.getTitle());
		assertEquals( bookSaved.getIsbn() , bookReturn.getIsbn());
		
	}
	
	
	
	@Test
	public void deleteWithNotContentError() throws BookNoContentErrorException {
		BookDTO  bookDto = BookDTOBuilder.builder().build().toBookDTO();
		assertThrows( BookNoContentErrorException.class, () ->   this.bookService.delete(bookDto) );	
	}
	
	@Test
	public void deleteWithSucess() throws BookNoContentErrorException  {
		
		BookDTO  bookDto = BookDTOBuilder.builder().build().toBookDTO();
		Book book = BookMapper.INSTANCE.toModel(bookDto);
		Optional<Book> optional = Optional.of(book);
		when( this.bookRepository.findById( anyLong()  )  ).thenReturn(optional);
		
		boolean isFinish = this.bookService.delete(bookDto);
		
		assertTrue(isFinish);
	}
	
	@Test
	public void deleteWithNotSucess() throws BookNoContentErrorException  {
		
		BookDTO  bookDto = BookDTOBuilder.builder().build().toBookDTO();
		Book book = BookMapper.INSTANCE.toModel(bookDto);
		Optional<Book> optional = Optional.empty();
		when( this.bookRepository.findById( anyLong()  )  ).thenReturn(optional);
		
		assertThrows( BookNoContentErrorException.class, () ->   this.bookService.delete(bookDto) );

	}
}
