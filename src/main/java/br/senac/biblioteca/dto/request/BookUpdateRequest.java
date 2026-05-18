package br.senac.biblioteca.dto.request;

import br.senac.biblioteca.model.BookStatus;
import br.senac.biblioteca.model.MetadataSource;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record BookUpdateRequest(
        @NotBlank String title,
        List<String> authors,
        String isbn,
        String publisher,
        String publishDate,
        @Min(1) Integer pageCount,
        String coverUrl,
        BookStatus status,
        @Min(1) @Max(5) Integer rating,
        String notes,
        List<String> tags,
        MetadataSource metadataSource
) {}
