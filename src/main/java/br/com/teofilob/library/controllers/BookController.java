package br.com.teofilob.library.controllers;

import java.net.URI;

import java.util.List;

import jakarta.validation.Valid;

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

import br.com.teofilob.library.dto.BookResponse;
import br.com.teofilob.library.dto.CreateBookRequest;

import br.com.teofilob.library.exception.BookNotFoundException;
import br.com.teofilob.library.service.BookService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(name = "Manages Library")
@RestController
@RequestMapping(value = "/api/v1/books")
public class BookController {
	
	@Autowired
	private BookService bookService;
	
	@Operation(summary = "Book creation operation")
	@ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success book creation"),
            @ApiResponse(responseCode = "400", description = "Missing required fields or wrong field range value.")
    })
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<BookResponse> created(@RequestBody @Valid CreateBookRequest request){
		BookResponse book = this.bookService.save(request);
		URI uri = URI.create("/api/v1/books/" + book.id());
		return ResponseEntity.created(uri).body(book);
	}
	
	@Operation(summary = "Returns a list of all book registered in the system")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "List of all book registered in the system"),
	    })
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<List<BookResponse>>list(){
		List<BookResponse> list = this.bookService.listAll();
		return ResponseEntity.ok(list);
	}
	
	@Operation(summary = "Returns book found by a given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success book found in the system"),
            @ApiResponse(responseCode = "404", description = "Book with given name not found.")
    })
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<BookResponse> getBook( @PathVariable long id ) throws BookNotFoundException {
		
		BookResponse book = this.bookService.getById(id);
		
		return ResponseEntity.ok(book);
	}
	
	@Operation(summary = "Delete a book found by a given valid Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Success book deleted in the system"),
            @ApiResponse(responseCode = "404", description = "Book with given id not found.")
    })
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<Void> deleteBook(@PathVariable long id) throws BookNotFoundException {
        this.bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
