package br.senac.biblioteca.controller;

import br.senac.biblioteca.dto.request.BookCreateRequest;
import br.senac.biblioteca.dto.request.BookUpdateRequest;
import br.senac.biblioteca.dto.response.BookResponse;
import br.senac.biblioteca.model.User;
import br.senac.biblioteca.service.BookService;
import br.senac.biblioteca.service.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;
    private final CurrentUserService currentUserService;

    public BookController(BookService bookService, CurrentUserService currentUserService) {
        this.bookService = bookService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public List<BookResponse> list(Authentication authentication) {
        return bookService.list(currentUserService.currentUser(authentication));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse create(Authentication authentication, @Valid @RequestBody BookCreateRequest request) {
        User user = currentUserService.currentUser(authentication);
        return bookService.create(user, request);
    }

    @GetMapping("/{id}")
    public BookResponse get(Authentication authentication, @PathVariable String id) {
        return bookService.get(currentUserService.currentUser(authentication), id);
    }

    @PutMapping("/{id}")
    public BookResponse update(Authentication authentication, @PathVariable String id, @Valid @RequestBody BookUpdateRequest request) {
        return bookService.update(currentUserService.currentUser(authentication), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Authentication authentication, @PathVariable String id) {
        bookService.delete(currentUserService.currentUser(authentication), id);
    }
}
