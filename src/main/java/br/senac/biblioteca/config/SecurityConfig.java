package br.senac.biblioteca.config;

import br.senac.biblioteca.dto.response.ErrorResponse;
import br.senac.biblioteca.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import java.time.Instant;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, ObjectMapper objectMapper) throws Exception {
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                writeError(response, objectMapper, HttpStatus.UNAUTHORIZED, "Nao autenticado.", request.getRequestURI()))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeError(response, objectMapper, HttpStatus.FORBIDDEN, "Acesso negado.", request.getRequestURI())))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/csrf").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    private void writeError(jakarta.servlet.http.HttpServletResponse response, ObjectMapper objectMapper, HttpStatus status, String message, String path) throws java.io.IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        ));
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    UserDetailsService userDetailsService(UserRepository userRepository) {
        return email -> userRepository.findByEmail(email)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPasswordHash())
                        .roles("USER")
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado."));
    }
}
