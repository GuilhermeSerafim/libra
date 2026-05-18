package br.senac.biblioteca.service;

import br.senac.biblioteca.dto.request.BookCreateRequest;
import br.senac.biblioteca.dto.request.BookUpdateRequest;
import br.senac.biblioteca.dto.response.BookResponse;
import br.senac.biblioteca.exception.NotFoundException;
import br.senac.biblioteca.model.Book;
import br.senac.biblioteca.model.BookStatus;
import br.senac.biblioteca.model.MetadataSource;
import br.senac.biblioteca.model.User;
import br.senac.biblioteca.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookResponse> list(User user) {
        return bookRepository.findByUserId(user.getId()).stream().map(BookMapper::toResponse).toList();
    }

    public BookResponse create(User user, BookCreateRequest request) {
        Instant now = Instant.now();
        Book book = new Book(null, user.getId(), request.title(), request.authors(), request.isbn(), request.publisher(),
                request.publishDate(), request.pageCount(), request.coverUrl(),
                request.status() == null ? BookStatus.TO_READ : request.status(), request.rating(), request.notes(),
                request.tags(), request.metadataSource() == null ? MetadataSource.MANUAL : request.metadataSource(), now, now);
        return BookMapper.toResponse(bookRepository.save(book));
    }

    public BookResponse get(User user, String id) {
        return BookMapper.toResponse(findOwnBook(user, id));
    }

    public BookResponse update(User user, String id, BookUpdateRequest request) {
        Book book = findOwnBook(user, id);
        book.setTitle(request.title());
        book.setAuthors(request.authors());
        book.setIsbn(request.isbn());
        book.setPublisher(request.publisher());
        book.setPublishDate(request.publishDate());
        book.setPageCount(request.pageCount());
        book.setCoverUrl(request.coverUrl());
        book.setStatus(request.status() == null ? BookStatus.TO_READ : request.status());
        book.setRating(request.rating());
        book.setNotes(request.notes());
        book.setTags(request.tags());
        book.setMetadataSource(request.metadataSource() == null ? book.getMetadataSource() : request.metadataSource());
        book.setUpdatedAt(Instant.now());
        return BookMapper.toResponse(bookRepository.save(book));
    }

    public void delete(User user, String id) {
        Book book = findOwnBook(user, id);
        bookRepository.delete(book);
    }

    private Book findOwnBook(User user, String id) {
        return bookRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Livro nao encontrado."));
    }
}
