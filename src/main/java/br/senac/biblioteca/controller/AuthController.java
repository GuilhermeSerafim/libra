package br.senac.biblioteca.controller;

import br.senac.biblioteca.dto.request.LoginRequest;
import br.senac.biblioteca.dto.request.RegisterRequest;
import br.senac.biblioteca.dto.response.AuthResponse;
import br.senac.biblioteca.dto.response.CsrfTokenResponse;
import br.senac.biblioteca.dto.response.UserResponse;
import br.senac.biblioteca.service.AuthService;
import br.senac.biblioteca.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final CurrentUserService currentUserService;
    private final CsrfTokenRepository csrfTokenRepository;

    public AuthController(AuthService authService, CurrentUserService currentUserService, CsrfTokenRepository csrfTokenRepository) {
        this.authService = authService;
        this.currentUserService = currentUserService;
        this.csrfTokenRepository = csrfTokenRepository;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        return authService.login(request, servletRequest);
    }

    @GetMapping("/csrf")
    public CsrfTokenResponse csrf(@Parameter(hidden = true) CsrfToken csrfToken, HttpServletRequest request, HttpServletResponse response) {
        csrfTokenRepository.saveToken(csrfToken, request, response);
        return new CsrfTokenResponse(csrfToken.getHeaderName(), csrfToken.getParameterName(), csrfToken.getToken());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request) {
        authService.logout(request);
    }

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        return currentUserService.currentUserResponse(authentication);
    }
}
