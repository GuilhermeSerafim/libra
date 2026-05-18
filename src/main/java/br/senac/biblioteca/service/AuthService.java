package br.senac.biblioteca.service;

import br.senac.biblioteca.dto.request.LoginRequest;
import br.senac.biblioteca.dto.request.RegisterRequest;
import br.senac.biblioteca.dto.response.AuthResponse;
import br.senac.biblioteca.dto.response.UserResponse;
import br.senac.biblioteca.exception.DuplicateEmailException;
import br.senac.biblioteca.exception.NotFoundException;
import br.senac.biblioteca.model.User;
import br.senac.biblioteca.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException();
        }
        Instant now = Instant.now();
        User user = new User(null, request.name(), request.email(), passwordEncoder.encode(request.password()), now, now);
        try {
            return new AuthResponse(toResponse(userRepository.save(user)));
        } catch (DuplicateKeyException ex) {
            throw new DuplicateEmailException();
        }
    }

    public AuthResponse login(LoginRequest request, HttpServletRequest servletRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        HttpSession session = servletRequest.getSession(true);
        servletRequest.changeSessionId();
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new NotFoundException("Usuario nao encontrado."));
        return new AuthResponse(toResponse(user));
    }

    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
}
