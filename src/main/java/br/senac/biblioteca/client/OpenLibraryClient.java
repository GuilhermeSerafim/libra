package br.senac.biblioteca.client;

import br.senac.biblioteca.config.OpenLibraryProperties;
import br.senac.biblioteca.dto.response.BookImportPreviewResponse;
import br.senac.biblioteca.exception.NotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class OpenLibraryClient {
    private final RestClient restClient;

    public OpenLibraryClient(OpenLibraryProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.timeoutMillis());
        factory.setReadTimeout(properties.timeoutMillis());
        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestFactory(factory)
                .build();
    }

    public BookImportPreviewResponse findByIsbn(String isbn) {
        JsonNode root = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/books")
                        .queryParam("bibkeys", "ISBN:" + isbn)
                        .queryParam("format", "json")
                        .queryParam("jscmd", "data")
                        .build())
                .retrieve()
                .body(JsonNode.class);

        JsonNode book = root == null ? null : root.get("ISBN:" + isbn);
        if (book == null || book.isMissingNode() || book.isNull()) {
            throw new NotFoundException("ISBN nao encontrado na Open Library.");
        }

        return new BookImportPreviewResponse(
                text(book, "title"),
                names(book.path("authors")),
                isbn,
                firstName(book.path("publishers")),
                text(book, "publish_date"),
                book.path("number_of_pages").isNumber() ? book.path("number_of_pages").asInt() : null,
                book.path("cover").path("small").isTextual() ? book.path("cover").path("small").asText() : null
        );
    }

    private static String text(JsonNode node, String field) {
        return node.path(field).isTextual() ? node.path(field).asText() : null;
    }

    private static String firstName(JsonNode array) {
        if (!array.isArray() || array.isEmpty()) {
            return null;
        }
        JsonNode first = array.get(0);
        return first.path("name").isTextual() ? first.path("name").asText() : null;
    }

    private static List<String> names(JsonNode array) {
        List<String> names = new ArrayList<>();
        if (array.isArray()) {
            for (JsonNode item : array) {
                if (item.path("name").isTextual()) {
                    names.add(item.path("name").asText());
                }
            }
        }
        return names;
    }
}
