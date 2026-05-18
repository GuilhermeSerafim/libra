package br.senac.biblioteca.controller;

import br.senac.biblioteca.AbstractMongoIntegrationTest;
import br.senac.biblioteca.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class AuthControllerTest extends AbstractMongoIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    void registersUserWithoutReturningPasswordHash() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Ada Lovelace","email":"ada@example.com","password":"StrongPass123"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.email").value("ada@example.com"))
                .andExpect(jsonPath("$.user.passwordHash").doesNotExist());
    }

    @Test
    void rejectsDuplicateEmail() throws Exception {
        String body = """
                {"name":"Ada Lovelace","email":"ada@example.com","password":"StrongPass123"}
                """;
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email ja cadastrado."));
    }

    @Test
    void loginCreatesSessionAndMeReturnsCurrentUser() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Ada Lovelace","email":"ada@example.com","password":"StrongPass123"}
                                """))
                .andExpect(status().isCreated());

        var login = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"ada@example.com","password":"StrongPass123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("JSESSIONID"))
                .andReturn();

        mockMvc.perform(get("/api/auth/me").session((org.springframework.mock.web.MockHttpSession) login.getRequest().getSession(false)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ada@example.com"));
    }

    @Test
    void unauthenticatedMeReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}
