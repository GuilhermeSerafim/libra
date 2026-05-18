package br.senac.biblioteca.repository;

import br.senac.biblioteca.AbstractMongoIntegrationTest;
import br.senac.biblioteca.model.Book;
import br.senac.biblioteca.model.BookStatus;
import br.senac.biblioteca.model.MetadataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BookRepositoryTest extends AbstractMongoIntegrationTest {
    @Autowired
    BookRepository bookRepository;

    @BeforeEach
    void clean() {
        bookRepository.deleteAll();
    }

    @Test
    void keepsBooksIsolatedByUserId() {
        Book own = new Book(null, "user-1", "Clean Code", List.of("Robert C. Martin"), "9780132350884",
                "Prentice Hall", "July 2008", 431, null, BookStatus.TO_READ, null,
                "Study reference", List.of("quality"), MetadataSource.MANUAL, Instant.now(), Instant.now());
        Book other = new Book(null, "user-2", "Refactoring", List.of("Martin Fowler"), "9780201485677",
                "Addison-Wesley", "1999", 431, null, BookStatus.READING, null,
                null, List.of("design"), MetadataSource.MANUAL, Instant.now(), Instant.now());
        bookRepository.saveAll(List.of(own, other));

        assertThat(bookRepository.findByUserId("user-1")).extracting(Book::getTitle).containsExactly("Clean Code");
        assertThat(bookRepository.findByIdAndUserId(own.getId(), "user-1")).isPresent();
        assertThat(bookRepository.findByIdAndUserId(other.getId(), "user-1")).isEmpty();
    }
}
