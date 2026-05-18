package br.senac.biblioteca.service;

import br.senac.biblioteca.dto.response.BookResponse;
import br.senac.biblioteca.model.Book;

public class BookMapper {
    private BookMapper() {
    }

    public static BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthors(),
                book.getIsbn(),
                book.getPublisher(),
                book.getPublishDate(),
                book.getPageCount(),
                book.getCoverUrl(),
                book.getStatus(),
                book.getRating(),
                book.getNotes(),
                book.getTags(),
                book.getMetadataSource(),
                book.getCreatedAt(),
                book.getUpdatedAt()
        );
    }
}
