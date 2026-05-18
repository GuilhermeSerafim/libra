package br.senac.biblioteca.client;

import br.senac.biblioteca.exception.ExternalServiceException;
import br.senac.biblioteca.exception.NotFoundException;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OpenLibraryClientTest {
    static final WireMockServer WIREMOCK = new WireMockServer(
            wireMockConfig().dynamicPort().usingFilesUnderDirectory("src/test/resources/wiremock/openlibrary")
    );

    @BeforeAll
    static void startWireMock() {
        WIREMOCK.start();
    }

    @AfterAll
    static void stopWireMock() {
        WIREMOCK.stop();
    }

    @Test
    void importsBookMetadataByIsbn() {
        OpenLibraryClient client = new OpenLibraryClient(new br.senac.biblioteca.config.OpenLibraryProperties(WIREMOCK.baseUrl(), 2000));

        var preview = client.findByIsbn("9780132350884");

        assertThat(preview.title()).isEqualTo("Clean Code");
        assertThat(preview.authors()).containsExactly("Robert C. Martin");
        assertThat(preview.publisher()).isEqualTo("Prentice Hall");
        assertThat(preview.pageCount()).isEqualTo(431);
    }

    @Test
    void throwsNotFoundWhenOpenLibraryReturnsEmptyResponse() {
        OpenLibraryClient client = new OpenLibraryClient(new br.senac.biblioteca.config.OpenLibraryProperties(WIREMOCK.baseUrl(), 2000));

        assertThatThrownBy(() -> client.findByIsbn("0000000000"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("ISBN nao encontrado na Open Library.");
    }

    @Test
    void throwsControlledExceptionWhenOpenLibraryFails() {
        OpenLibraryClient client = new OpenLibraryClient(new br.senac.biblioteca.config.OpenLibraryProperties(WIREMOCK.baseUrl(), 2000));

        assertThatThrownBy(() -> client.findByIsbn("1111111111"))
                .isInstanceOf(ExternalServiceException.class)
                .hasMessage("Open Library indisponivel.");
    }
}
