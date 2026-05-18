package br.senac.biblioteca.controller;

import br.senac.biblioteca.AbstractMongoIntegrationTest;
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

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    void registersUserWithoutReturningPasswordHash() throws Exception {
        performWithRealCsrf(post("/api/auth/register")
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
        performWithRealCsrf(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

        performWithRealCsrf(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email ja cadastrado."));
    }

    @Test
    void loginCreatesSessionAndMeReturnsCurrentUser() throws Exception {
        performWithRealCsrf(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Ada Lovelace","email":"ada@example.com","password":"StrongPass123"}
                                """))
                .andExpect(status().isCreated());

        var login = performWithRealCsrf(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"ada@example.com","password":"StrongPass123"}
                                """))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);
        assertNotNull(session);

        mockMvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ada@example.com"));
    }

    @Test
    void postRegisterWithoutCsrfIsForbidden() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Ada Lovelace","email":"ada@example.com","password":"StrongPass123"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void csrfEndpointIssuesTokenCookieForFirstUnsafeRequest() throws Exception {
        mockMvc.perform(get("/api/auth/csrf"))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("XSRF-TOKEN"))
                .andExpect(jsonPath("$.headerName").value("X-XSRF-TOKEN"))
                .andExpect(jsonPath("$.parameterName").value("_csrf"))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void registerAcceptsTokenIssuedByCsrfEndpoint() throws Exception {
        Cookie xsrfCookie = issueXsrfCookie();

        mockMvc.perform(post("/api/auth/register")
                        .cookie(xsrfCookie)
                        .header("X-XSRF-TOKEN", xsrfCookie.getValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Ada Lovelace","email":"ada@example.com","password":"StrongPass123"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.email").value("ada@example.com"));
    }

    @Test
    void unauthenticatedMeReturnsJsonUnauthorized() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Nao autenticado."));
    }

    @Test
    void loginRotatesExistingSessionIdAndKeepsUserAuthenticated() throws Exception {
        performWithRealCsrf(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Ada Lovelace","email":"ada@example.com","password":"StrongPass123"}
                                """))
                .andExpect(status().isCreated());

        MockHttpSession existingSession = new MockHttpSession();
        String originalSessionId = existingSession.getId();

        var login = performWithRealCsrf(post("/api/auth/login")
                        .session(existingSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"ada@example.com","password":"StrongPass123"}
                                """))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession rotatedSession = (MockHttpSession) login.getRequest().getSession(false);
        assertNotNull(rotatedSession);
        assertNotEquals(originalSessionId, rotatedSession.getId());

        mockMvc.perform(get("/api/auth/me").session(rotatedSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ada@example.com"));
    }

    @Test
    void logoutInvalidatesSession() throws Exception {
        performWithRealCsrf(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Ada Lovelace","email":"ada@example.com","password":"StrongPass123"}
                                """))
                .andExpect(status().isCreated());

        var login = performWithRealCsrf(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"ada@example.com","password":"StrongPass123"}
                                """))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);
        assertNotNull(session);

        performWithRealCsrf(post("/api/auth/logout").session(session))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithWrongPasswordReturnsJsonUnauthorized() throws Exception {
        performWithRealCsrf(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Ada Lovelace","email":"ada@example.com","password":"StrongPass123"}
                                """))
                .andExpect(status().isCreated());

        performWithRealCsrf(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"ada@example.com","password":"WrongPass123"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciais invalidas."));
    }
}
