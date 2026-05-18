package br.senac.biblioteca.controller;

import br.senac.biblioteca.AbstractMongoIntegrationTest;
import br.senac.biblioteca.repository.BookRepository;
import br.senac.biblioteca.repository.UserRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class BookImportControllerTest extends AbstractMongoIntegrationTest {
    static final WireMockServer WIREMOCK = new WireMockServer(
            wireMockConfig().dynamicPort().usingFilesUnderDirectory("src/test/resources/wiremock/openlibrary")
    );

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BookRepository bookRepository;

    @BeforeAll
    static void startWireMock() {
        WIREMOCK.start();
    }

    @AfterAll
    static void stopWireMock() {
        WIREMOCK.stop();
    }

    @DynamicPropertySource
    static void openLibraryProperties(DynamicPropertyRegistry registry) {
        registry.add("open-library.base-url", WIREMOCK::baseUrl);
    }

    @BeforeEach
    void clean() {
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void previewsBookFromOpenLibraryReplay() throws Exception {
        MockHttpSession session = login("ada@example.com");

        mockMvc.perform(get("/api/books/import/isbn/9780132350884").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.authors[0]").value("Robert C. Martin"));

        assertThat(bookRepository.count()).isZero();
    }

    @Test
    void importRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/books/import/isbn/9780132350884"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsInvalidIsbn() throws Exception {
        MockHttpSession session = login("grace@example.com");

        mockMvc.perform(get("/api/books/import/isbn/123").session(session))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("ISBN deve ter 10 ou 13 caracteres validos."));
    }

    @Test
    void acceptsIsbn10EndingWithX() throws Exception {
        MockHttpSession session = login("katherine@example.com");

        mockMvc.perform(get("/api/books/import/isbn/012345678x").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("ISBN X Example"));
    }

    @Test
    void returnsServiceUnavailableWhenOpenLibraryFails() throws Exception {
        MockHttpSession session = login("margaret@example.com");

        mockMvc.perform(get("/api/books/import/isbn/1111111111").session(session))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value("Open Library indisponivel."));
    }

    private MockHttpSession login(String email) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"User","email":"%s","password":"StrongPass123"}
                                """.formatted(email)))
                .andExpect(status().isCreated());

        return (MockHttpSession) mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"StrongPass123"}
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andReturn()
                .getRequest()
                .getSession(false);
    }
}
