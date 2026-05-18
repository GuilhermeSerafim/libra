package br.senac.biblioteca.dto.response;

import br.senac.biblioteca.model.BookStatus;
import br.senac.biblioteca.model.MetadataSource;

import java.time.Instant;
import java.util.List;

public record BookResponse(
        String id,
        String title,
        List<String> authors,
        String isbn,
        String publisher,
        String publishDate,
        Integer pageCount,
        String coverUrl,
        BookStatus status,
        Integer rating,
        String notes,
        List<String> tags,
        MetadataSource metadataSource,
        Instant createdAt,
        Instant updatedAt
) {}
