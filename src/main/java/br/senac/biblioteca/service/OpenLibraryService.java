package br.senac.biblioteca.service;

import br.senac.biblioteca.client.OpenLibraryClient;
import br.senac.biblioteca.dto.response.BookImportPreviewResponse;
import br.senac.biblioteca.exception.BadRequestException;
import org.springframework.stereotype.Service;

@Service
public class OpenLibraryService {
    private final OpenLibraryClient client;

    public OpenLibraryService(OpenLibraryClient client) {
        this.client = client;
    }

    public BookImportPreviewResponse previewByIsbn(String isbn) {
        String normalized = isbn == null ? "" : isbn.trim().replaceAll("[-\\s]", "").toUpperCase();
        if (!normalized.matches("\\d{13}|\\d{9}[\\dX]")) {
            throw new BadRequestException("ISBN deve ter 10 ou 13 caracteres validos.");
        }
        return client.findByIsbn(normalized);
    }
}
