package br.senac.biblioteca.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "open-library")
public record OpenLibraryProperties(String baseUrl, int timeoutMillis) {}
