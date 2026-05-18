package br.senac.biblioteca.dto.response;

import java.util.List;

public record BookImportPreviewResponse(
        String title,
        List<String> authors,
        String isbn,
        String publisher,
        String publishDate,
        Integer pageCount,
        String coverUrl
) {}
