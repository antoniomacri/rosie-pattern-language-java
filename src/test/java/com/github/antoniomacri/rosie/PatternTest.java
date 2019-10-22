package com.github.antoniomacri.rosie;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class PatternTest {
    private RosieEngine rosie;

    @BeforeEach
    void init() {
        rosie = new RosieEngine();
    }

    @AfterEach
    void close() {
        rosie.close();
    }


    @Test
    void testCompilePattern() {
        String expression = "[:digit:]+";
        Pattern pattern = rosie.compile(expression);
        assertThat(String.valueOf(pattern)).isEqualTo(expression);
    }
}
