package br.com.teofilob.library.builder;

import br.com.teofilob.library.dto.BookDTO;
import lombok.Builder;

@Builder
public class BookDTOBuilder {
	
	@Builder.Default
	private long id = 1l;
	
	@Builder.Default
	private String title = "Sprint: how to solve big problems and test new ideas in just five days";
	
	@Builder.Default
	private String isbn = "978-85-510-0152-3";
	
	public BookDTO toBookDTO() {
		return new BookDTO( this.id, this.title, this.isbn );
	}
	
	
}
