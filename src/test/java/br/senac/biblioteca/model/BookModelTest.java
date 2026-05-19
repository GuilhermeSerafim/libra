package br.senac.biblioteca.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BookModelTest {
    @Test
    void doesNotExposeBrainOverloadConstructors() {
        List<Integer> parameterCounts = Arrays.stream(Book.class.getConstructors())
                .mapToInt(constructor -> constructor.getParameterTypes().length)
                .boxed()
                .toList();

        assertThat(parameterCounts)
                .isNotEmpty()
                .allMatch(parameterCount -> parameterCount <= 7);
    }
}
