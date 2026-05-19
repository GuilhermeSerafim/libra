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
        Book own = bookForUser("user-1", "Clean Code");
        Book other = bookForUser("user-2", "Refactoring");
        bookRepository.saveAll(List.of(own, other));

        assertThat(bookRepository.findByUserId("user-1")).extracting(Book::getTitle).containsExactly("Clean Code");
        assertThat(bookRepository.findByIdAndUserId(own.getId(), "user-1")).isPresent();
        assertThat(bookRepository.findByIdAndUserId(other.getId(), "user-1")).isEmpty();
    }

    private static Book bookForUser(String userId, String title) {
        Instant now = Instant.now();
        Book book = new Book();
        book.setUserId(userId);
        book.setTitle(title);
        book.setAuthors(List.of("Author"));
        book.setIsbn("9780132350884");
        book.setPublisher("Publisher");
        book.setPublishDate("2008");
        book.setPageCount(431);
        book.setStatus(BookStatus.TO_READ);
        book.setNotes("Study reference");
        book.setTags(List.of("quality"));
        book.setMetadataSource(MetadataSource.MANUAL);
        book.setCreatedAt(now);
        book.setUpdatedAt(now);
        return book;
    }
}
