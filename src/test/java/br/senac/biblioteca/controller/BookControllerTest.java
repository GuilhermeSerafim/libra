package br.senac.biblioteca.controller;

import br.senac.biblioteca.AbstractMongoIntegrationTest;
import br.senac.biblioteca.repository.BookRepository;
import br.senac.biblioteca.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@AutoConfigureMockMvc
class BookControllerTest extends AbstractMongoIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BookRepository bookRepository;

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
    void createsListsUpdatesAndDeletesOwnBook() throws Exception {
        MockHttpSession session = login("ada@example.com");

        String created = performWithRealCsrf(post("/api/books")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Clean Code","authors":["Robert C. Martin"],"isbn":"9780132350884","status":"READING","rating":5,"tags":["quality"]}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = created.replaceAll(".*\\\"id\\\":\\\"([^\\\"]+)\\\".*", "$1");

        mockMvc.perform(get("/api/books").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id));

        performWithRealCsrf(put("/api/books/" + id)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Clean Code Updated","authors":["Robert C. Martin"],"isbn":"9780132350884","status":"READ","rating":5,"tags":["quality","done"]}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Clean Code Updated"))
                .andExpect(jsonPath("$.status").value("READ"));

        performWithRealCsrf(delete("/api/books/" + id).session(session))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/books/" + id).session(session))
                .andExpect(status().isNotFound());
    }

    @Test
    void doesNotExposeBookFromAnotherUser() throws Exception {
        MockHttpSession owner = login("owner@example.com");
        MockHttpSession stranger = login("stranger@example.com");

        String created = performWithRealCsrf(post("/api/books")
                        .session(owner)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Private Book","status":"TO_READ"}
                                """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String id = created.replaceAll(".*\\\"id\\\":\\\"([^\\\"]+)\\\".*", "$1");

        mockMvc.perform(get("/api/books/" + id).session(stranger))
                .andExpect(status().isNotFound());

        performWithRealCsrf(delete("/api/books/" + id).session(stranger))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsBookCreationWithoutSession() throws Exception {
        performWithRealCsrf(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Clean Code","status":"READING"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsBookCreationWithoutCsrf() throws Exception {
        MockHttpSession session = login("ada@example.com");

        mockMvc.perform(post("/api/books")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Clean Code","status":"READING"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void rejectsInvalidBookStatusAsBadRequest() throws Exception {
        MockHttpSession session = login("ada@example.com");

        performWithRealCsrf(post("/api/books")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Bad","status":"DONE"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Dados invalidos."));
    }

    @Test
    void rejectsInvalidPageCount() throws Exception {
        MockHttpSession session = login("ada@example.com");

        performWithRealCsrf(post("/api/books")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Bad Pages","pageCount":0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Dados invalidos."));
    }

    @Test
    void persistsOpenLibraryMetadataSourceWhenProvided() throws Exception {
        MockHttpSession session = login("ada@example.com");

        performWithRealCsrf(post("/api/books")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Imported","metadataSource":"OPEN_LIBRARY"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.metadataSource").value("OPEN_LIBRARY"));
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
