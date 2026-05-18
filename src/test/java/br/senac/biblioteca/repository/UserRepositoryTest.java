package br.senac.biblioteca.repository;

import br.senac.biblioteca.AbstractMongoIntegrationTest;
import br.senac.biblioteca.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserRepositoryTest extends AbstractMongoIntegrationTest {
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    void findsUserByEmail() {
        User user = new User(null, "Ada Lovelace", "ada@example.com", "hashed", Instant.now(), Instant.now());
        userRepository.save(user);

        assertThat(userRepository.findByEmail("ada@example.com")).isPresent();
        assertThat(userRepository.existsByEmail("ada@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("other@example.com")).isFalse();
    }

    @Test
    void rejectsDuplicateEmail() {
        User user = new User(null, "Ada Lovelace", "ada@example.com", "hashed", Instant.now(), Instant.now());
        User duplicate = new User(null, "Ada Byron", "ada@example.com", "other-hash", Instant.now(), Instant.now());
        userRepository.save(user);

        assertThatThrownBy(() -> userRepository.save(duplicate))
                .isInstanceOf(DuplicateKeyException.class);
    }
}
