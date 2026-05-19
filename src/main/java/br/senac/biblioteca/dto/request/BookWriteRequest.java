package br.senac.biblioteca.dto.request;

import br.senac.biblioteca.model.BookStatus;
import br.senac.biblioteca.model.MetadataSource;

import java.util.List;

public interface BookWriteRequest {
    String title();
    List<String> authors();
    String isbn();
    String publisher();
    String publishDate();
    Integer pageCount();
    String coverUrl();
    BookStatus status();
    Integer rating();
    String notes();
    List<String> tags();
    MetadataSource metadataSource();
}
