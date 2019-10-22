package com.github.antoniomacri.rosie;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class RosieEngineConfigTest {
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
    void testConfig() {
        Configuration configuration = rosie.getConfiguration();
        assertThat(configuration).isNotNull();

        assertThat(configuration.getRosieVersion()).isNotBlank();
        assertThat(configuration.getRosieHome()).isNotBlank();
        assertThat(configuration.getLibdir()).isNotBlank();
        assertThat(configuration.getRplVersion()).isNotBlank();
        assertThat(configuration.getLibpath()).isNotBlank();
    }
}
