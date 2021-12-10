package br.com.teofilob.library.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.teofilob.library.dto.BookDTO;
import br.com.teofilob.library.entity.Book;
import br.com.teofilob.library.exception.BookNoContentErrorException;
import br.com.teofilob.library.exception.BookNotFoundException;
import br.com.teofilob.library.mapper.BookMapper;
import br.com.teofilob.library.repository.BookRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BookService  {

	private final BookMapper bookMapper = BookMapper.INSTANCE;
	 

	private BookRepository bookRepository;
	

	public BookDTO save(BookDTO bookDTO) {
		
		Book book = bookMapper.toModel(bookDTO);
		book = this.bookRepository.save(book); 
		return bookMapper.toDTO(book);
	}

	public List<BookDTO> listAll(){
		return  this.bookRepository.findAll().stream().map( bookMapper:: toDTO ).collect(Collectors.toList()); 
	}
	
	public BookDTO getById(Long id) throws BookNotFoundException {
		Optional<Book> book = this.bookRepository.findById(id);
		return book.map(bookMapper:: toDTO).orElseThrow( () -> new BookNotFoundException(id) ) ;
	}

	public boolean delete(BookDTO bookDTO) throws BookNoContentErrorException {
		boolean isDeleted = false;
		Optional<Book> book = this.bookRepository.findById(bookDTO.getId());
		if (book.isPresent() ) {
			this.bookRepository.delete(book.get());
			isDeleted = true;
		}else {
			throw new BookNoContentErrorException(bookDTO.getId());
		}
		return  isDeleted;
	}
	
}
