package br.com.teofilob.library.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import br.com.teofilob.library.dto.CreateBookRequest;
import br.com.teofilob.library.dto.BookResponse;
import br.com.teofilob.library.entity.Book;

@Mapper
public interface BookMapper {
	
	BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);
	
	@Mapping(target = "id", ignore = true)
	Book toModel(CreateBookRequest request);
	
	BookResponse toDTO(Book book);

}
