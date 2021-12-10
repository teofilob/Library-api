package br.com.teofilob.library.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BookNoContentErrorException extends Exception {
	

	private static final String MESSAGE = "Error on execute this removemente, please verify the livrary, %s ";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	
	public BookNoContentErrorException(long id) {
		super( String.format(MESSAGE , id)  );
	}
}
