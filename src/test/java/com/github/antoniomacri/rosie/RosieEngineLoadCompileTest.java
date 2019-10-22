package com.github.antoniomacri.rosie;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


class RosieEngineLoadCompileTest {
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
    void testLoad() {
        String packageName = rosie.load("package x; foo = \"foo\"");
        assertThat(packageName).isEqualTo("x");
    }

    @Test
    void testLoadNoPackage() {
        String packageName = rosie.load("foo = \"foo\"");
        assertThat(packageName).isNull();
    }

    @Test
    void testCompileFoo() {
        testLoad();

        Pattern pattern = rosie.compile("x.foo");
        assertThat(pattern).isNotNull();
    }

    @Test
    void testCompilePattern() {
        Pattern pattern = rosie.compile("[:digit:]+");
        assertThat(pattern).isNotNull();
    }

    @Test
    void testCompileInvalidPattern() throws IOException {
        Throwable throwable = catchThrowable(() -> rosie.compile("[:foobar:]+"));
        assertThat(throwable).isInstanceOf(RosieException.class);

        RosieException rosieException = (RosieException) throwable;

        ObjectMapper objectMapper = new ObjectMapper();
        List errors = objectMapper.readValue(rosieException.getErrors(), List.class);

        assertThat(errors.size()).isGreaterThan(0);

        Map firstError = (Map) errors.get(0);
        assertThat(firstError.get("message")).isNotNull();
        assertThat(firstError.get("who")).isEqualTo("compiler");
    }

    @Test
    void testCompileInvalidPatternNumInt() throws IOException {
        Throwable throwable = catchThrowable(() -> rosie.compile("num.int"));
        assertThat(throwable).isInstanceOf(RosieException.class);

        RosieException rosieException = (RosieException) throwable;

        ObjectMapper objectMapper = new ObjectMapper();
        List errors = objectMapper.readValue(rosieException.getErrors(), List.class);

        assertThat(errors.size()).isGreaterThan(0);

        Map firstError = (Map) errors.get(0);
        assertThat(firstError.get("message")).isNotNull();
        assertThat(firstError.get("who")).isEqualTo("compiler");
    }

    @Test
    void testLoadInvalidPatternFoo() throws IOException {
        Throwable throwable = catchThrowable(() -> rosie.load("foo = \""));
        assertThat(throwable).isInstanceOf(RosieException.class);

        RosieException rosieException = (RosieException) throwable;

        ObjectMapper objectMapper = new ObjectMapper();
        List errors = objectMapper.readValue(rosieException.getErrors(), List.class);

        assertThat(errors.size()).isGreaterThan(0);

        Map firstError = (Map) errors.get(0);
        assertThat(firstError.get("message")).isNotNull();
        assertThat(firstError.get("who")).isEqualTo("parser");
    }
}
