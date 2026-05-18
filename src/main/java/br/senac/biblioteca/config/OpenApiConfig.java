package br.senac.biblioteca.config;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI bibliotecaOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gerenciador de Biblioteca Pessoal API")
                        .version("0.0.1")
                        .description("API REST para cadastro, autenticacao, CRUD de livros e pre-visualizacao por ISBN."));
    }

    @Bean
    OpenApiCustomizer csrfHeaderCustomizer() {
        return openApi -> openApi.getPaths().values().forEach(pathItem -> {
            addCsrfHeader(pathItem.getPost());
            addCsrfHeader(pathItem.getPut());
            addCsrfHeader(pathItem.getDelete());
            addCsrfHeader(pathItem.getPatch());
        });
    }

    private void addCsrfHeader(Operation operation) {
        if (operation == null) {
            return;
        }

        if (operation.getParameters() == null) {
            operation.setParameters(new ArrayList<>());
        }

        boolean alreadyDocumented = operation.getParameters().stream()
                .anyMatch(parameter -> "X-XSRF-TOKEN".equals(parameter.getName()));
        if (alreadyDocumented) {
            return;
        }

        operation.getParameters().add(0, new Parameter()
                .name("X-XSRF-TOKEN")
                .in("header")
                .required(true)
                .description("Valor do cookie XSRF-TOKEN obtido em GET /api/auth/csrf.")
                .schema(new StringSchema()));
    }
}
