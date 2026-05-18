package br.senac.biblioteca.repository;

import br.senac.biblioteca.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends MongoRepository<Book, String> {
    List<Book> findByUserId(String userId);
    Optional<Book> findByIdAndUserId(String id, String userId);
}
