package br.senac.biblioteca.controller;

import br.senac.biblioteca.AbstractMongoIntegrationTest;
import br.senac.biblioteca.repository.BookRepository;
import br.senac.biblioteca.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    void createsListsUpdatesAndDeletesOwnBook() throws Exception {
        MockHttpSession session = login("ada@example.com");

        String created = mockMvc.perform(post("/api/books")
                        .session(session)
                        .with(csrf())
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

        mockMvc.perform(put("/api/books/" + id)
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Clean Code Updated","authors":["Robert C. Martin"],"isbn":"9780132350884","status":"READ","rating":5,"tags":["quality","done"]}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Clean Code Updated"))
                .andExpect(jsonPath("$.status").value("READ"));

        mockMvc.perform(delete("/api/books/" + id).session(session).with(csrf()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/books/" + id).session(session))
                .andExpect(status().isNotFound());
    }

    @Test
    void doesNotExposeBookFromAnotherUser() throws Exception {
        MockHttpSession owner = login("owner@example.com");
        MockHttpSession stranger = login("stranger@example.com");

        String created = mockMvc.perform(post("/api/books")
                        .session(owner)
                        .with(csrf())
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

        mockMvc.perform(delete("/api/books/" + id).session(stranger).with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsBookCreationWithoutSession() throws Exception {
        mockMvc.perform(post("/api/books")
                        .with(csrf())
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
