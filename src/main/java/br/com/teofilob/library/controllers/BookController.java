package br.com.teofilob.library.controllers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.teofilob.library.dto.BookDTO;
import br.com.teofilob.library.exception.BookNoContentErrorException;
import br.com.teofilob.library.exception.BookNotFoundException;
import br.com.teofilob.library.service.BookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api("Manages Library")
@RestController
@RequestMapping(value = "/api/v1/books")
public class BookController {
	
	@Autowired
	private BookService bookService;
	
	@ApiOperation(value = "Book creation operation")
	@ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success book creation"),
            @ApiResponse(code = 400, message = "Missing required fields or wrong field range value.")
    })
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<BookDTO> created(@RequestBody @Valid BookDTO book) throws URISyntaxException{
		book =	this.bookService.save(book);
		URI uri = new URI("/books/"+book.getId());
		return ResponseEntity.created(uri).body(book);
	}
	
	@ApiOperation(value = "Returns a list of all book registered in the system")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "List of all book registered in the system"),
	    })
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<List<BookDTO>>list(){
		List<BookDTO> list = this.bookService.listAll();
		return ResponseEntity.ok(list);
	}
	
	@ApiOperation(value = "Returns book found by a given id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success book found in the system"),
            @ApiResponse(code = 404, message = "Book with given name not found.")
    })
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<BookDTO> getBook( @PathVariable long id ) throws BookNotFoundException {
		
		BookDTO book = this.bookService.getById(id);
		
		return ResponseEntity.ok(book);
	}
	
	@ApiOperation(value = "Delete a book found by a given valid Id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Success book deleted in the system"),
            @ApiResponse(code = 404, message = "Book with given id not found.")
    })
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> deleleBook( @PathVariable long id, @RequestBody @Valid BookDTO book) throws BookNoContentErrorException {
		
		if(book == null ||  book.getId() != id) {
			return  ResponseEntity.badRequest().build();
		}
		
		this.bookService.delete(book);
		return  ResponseEntity.noContent().build();
	}
}
