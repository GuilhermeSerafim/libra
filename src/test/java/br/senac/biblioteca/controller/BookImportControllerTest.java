package br.senac.biblioteca.controller;

import br.senac.biblioteca.AbstractMongoIntegrationTest;
import br.senac.biblioteca.repository.BookRepository;
import br.senac.biblioteca.repository.UserRepository;
import com.github.tomakehurst.wiremock.WireMockServer;
import jakarta.servlet.http.Cookie;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    private Cookie issueXsrfCookie() throws Exception {
        var csrfResponse = mockMvc.perform(get("/api/auth/csrf"))
                .andExpect(status().isOk())
                .andReturn();

        Cookie xsrfCookie = csrfResponse.getResponse().getCookie("XSRF-TOKEN");
        assertNotNull(xsrfCookie);
        return xsrfCookie;
    }

    private ResultActions performWithRealCsrf(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        Cookie xsrfCookie = issueXsrfCookie();
        return mockMvc.perform(requestBuilder
                .cookie(xsrfCookie)
                .header("X-XSRF-TOKEN", xsrfCookie.getValue()));
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
        performWithRealCsrf(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"User","email":"%s","password":"StrongPass123"}
                                """.formatted(email)))
                .andExpect(status().isCreated());

        return (MockHttpSession) performWithRealCsrf(post("/api/auth/login")
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
