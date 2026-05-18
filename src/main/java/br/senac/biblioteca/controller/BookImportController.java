package br.senac.biblioteca.controller;

import br.senac.biblioteca.dto.response.BookImportPreviewResponse;
import br.senac.biblioteca.service.CurrentUserService;
import br.senac.biblioteca.service.OpenLibraryService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books/import")
public class BookImportController {
    private final OpenLibraryService openLibraryService;
    private final CurrentUserService currentUserService;

    public BookImportController(OpenLibraryService openLibraryService, CurrentUserService currentUserService) {
        this.openLibraryService = openLibraryService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/isbn/{isbn}")
    public BookImportPreviewResponse preview(Authentication authentication, @PathVariable String isbn) {
        currentUserService.currentUser(authentication);
        return openLibraryService.previewByIsbn(isbn);
    }
}
