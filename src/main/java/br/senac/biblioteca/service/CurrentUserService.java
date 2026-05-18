package br.senac.biblioteca.service;

import br.senac.biblioteca.exception.NotFoundException;
import br.senac.biblioteca.model.User;
import br.senac.biblioteca.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    private final UserRepository userRepository;
    private final AuthService authService;

    public CurrentUserService(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public User currentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotFoundException("Usuario autenticado nao encontrado.");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Usuario autenticado nao encontrado."));
    }

    public br.senac.biblioteca.dto.response.UserResponse currentUserResponse(Authentication authentication) {
        return authService.toResponse(currentUser(authentication));
    }
}
