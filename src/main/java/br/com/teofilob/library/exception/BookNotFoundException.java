package br.com.teofilob.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import br.com.teofilob.library.dto.BookDTO;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookNotFoundException extends Exception {
	
	private static final String MESSAGE = "The book not found %s ";
	
	private static final String MESSAGE_BOOK_REMOVE = "The book %s not found  ";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public BookNotFoundException(BookDTO book) {
		super( String.format(MESSAGE_BOOK_REMOVE , book.getTitle())  );
	}
	
	public BookNotFoundException(long id) {
		super( String.format(MESSAGE , id)  );
	}

}
