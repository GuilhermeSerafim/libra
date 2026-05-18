package br.senac.biblioteca.dto.response;

public record CsrfTokenResponse(String headerName, String parameterName, String token) {}
