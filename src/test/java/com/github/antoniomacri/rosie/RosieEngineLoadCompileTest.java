package com.github.antoniomacri.rosie;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class RosieEngineLoadCompileTest {
    private RosieEngine rosie;

    @Before
    public void init() {
        rosie = new RosieEngine();
    }

    @After
    public void close() {
        rosie.close();
    }


    @Test
    public void testLoad() {
        String packageName = rosie.load("package x; foo = \"foo\"");
        assertThat(packageName).isEqualTo("x");
    }

    @Test
    public void testLoadNoPackage() {
        String packageName = rosie.load("foo = \"foo\"");
        assertThat(packageName).isNull();
    }

    @Test
    public void testCompileFoo() {
        testLoad();

        Pattern pattern = rosie.compile("x.foo");
        assertThat(pattern).isNotNull();
    }

    @Test
    public void testCompilePattern() {
        Pattern pattern = rosie.compile("[:digit:]+");
        assertThat(pattern).isNotNull();
    }

    @Test
    public void testCompileInvalidPattern() throws IOException {
        try {
            rosie.compile("[:foobar:]+");
            Assert.fail("Expected exception.");
        } catch (RosieException e) {
            ObjectMapper objectMapper = new ObjectMapper();
            List errors = objectMapper.readValue(e.getErrors(), List.class);

            assertThat(errors.size()).isGreaterThan(0);

            Map firstError = (Map) errors.get(0);
            assertThat(firstError.get("message")).isNotNull();
            assertThat(firstError.get("who")).isEqualTo("compiler");
        }
    }

    @Test
    public void testCompileInvalidPatternNumInt() throws IOException {
        try {
            rosie.compile("num.int");
            Assert.fail("Expected exception.");
        } catch (RosieException e) {
            ObjectMapper objectMapper = new ObjectMapper();
            List errors = objectMapper.readValue(e.getErrors(), List.class);

            assertThat(errors.size()).isGreaterThan(0);

            Map firstError = (Map) errors.get(0);
            assertThat(firstError.get("message")).isNotNull();
            assertThat(firstError.get("who")).isEqualTo("compiler");
        }
    }

    @Test
    public void testLoadInvalidPatternFoo() throws IOException {
        try {
            rosie.load("foo = \"");
            Assert.fail("Expected exception.");
        } catch (RosieException e) {
            ObjectMapper objectMapper = new ObjectMapper();
            List errors = objectMapper.readValue(e.getErrors(), List.class);

            assertThat(errors.size()).isGreaterThan(0);

            Map firstError = (Map) errors.get(0);
            assertThat(firstError.get("message")).isNotNull();
            assertThat(firstError.get("who")).isEqualTo("parser");
        }
    }
}
