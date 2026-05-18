package br.senac.biblioteca.controller;

import br.senac.biblioteca.AbstractMongoIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class OpenApiDocumentationTest extends AbstractMongoIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void exposesOpenApiDocsWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.info.title").value("Gerenciador de Biblioteca Pessoal API"))
                .andExpect(jsonPath("$.paths['/api/auth/csrf'].get.parameters").doesNotExist());
    }

    @Test
    void documentsCsrfHeaderOnUnsafeOperations() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/auth/register'].post.parameters[0].name").value("X-XSRF-TOKEN"))
                .andExpect(jsonPath("$.paths['/api/books'].post.parameters[0].name").value("X-XSRF-TOKEN"))
                .andExpect(jsonPath("$.paths['/api/books/{id}'].put.parameters[0].name").value("X-XSRF-TOKEN"))
                .andExpect(jsonPath("$.paths['/api/books/{id}'].delete.parameters[0].name").value("X-XSRF-TOKEN"));
    }

    @Test
    void exposesSwaggerUiWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }
}
