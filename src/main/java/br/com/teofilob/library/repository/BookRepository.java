package br.com.teofilob.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.teofilob.library.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long>{

}
