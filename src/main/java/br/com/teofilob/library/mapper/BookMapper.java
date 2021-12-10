package br.com.teofilob.library.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import br.com.teofilob.library.dto.BookDTO;
import br.com.teofilob.library.entity.Book;

@Mapper
public interface BookMapper {
	
	BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);
	
	Book toModel( BookDTO bookDTO );
	
	BookDTO toDTO( Book book );

}
