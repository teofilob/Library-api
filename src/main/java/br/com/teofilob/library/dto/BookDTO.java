package br.com.teofilob.library.dto;

import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
	
	private long id;
	
	@Size(min = 1, max = 200)
	private String title;
	
	@Size(min = 1, max = 20)
	private String isbn;
	
}
