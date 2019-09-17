package com.github.antoniomacri.rosie;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.greaterThan;


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
        assertThat(packageName, is(equalTo("x")));
    }

    @Test
    public void testLoadNoPackage() {
        String packageName = rosie.load("foo = \"foo\"");
        assertThat(packageName, is(nullValue()));
    }

    @Test
    public void testCompileFoo() {
        testLoad();

        Pattern pattern = rosie.compile("x.foo");
        assertThat("pattern", pattern, is(notNullValue()));
    }

    @Test
    public void testCompilePattern() {
        Pattern pattern = rosie.compile("[:digit:]+");
        assertThat("pattern", pattern, is(notNullValue()));
    }

    @Test
    public void testCompileInvalidPattern() throws IOException {
        try {
            rosie.compile("[:foobar:]+");
            Assert.fail("Expected exception.");
        } catch (RosieException e) {
            ObjectMapper objectMapper = new ObjectMapper();
            List errors = objectMapper.readValue(e.getErrors(), List.class);

            assertThat("errors size", errors.size(), is(greaterThan(0)));

            Map firstError = (Map) errors.get(0);
            assertThat("first error message", firstError.get("message"), is(notNullValue()));
            assertThat("first error who", firstError.get("who"), is(equalTo("compiler")));
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

            assertThat("errors size", errors.size(), is(greaterThan(0)));

            Map firstError = (Map) errors.get(0);
            assertThat("first error message", firstError.get("message"), is(notNullValue()));
            assertThat("first error who", firstError.get("who"), is(equalTo("compiler")));
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

            assertThat("errors size", errors.size(), is(greaterThan(0)));

            Map firstError = (Map) errors.get(0);
            assertThat("first error message", firstError.get("message"), is(notNullValue()));
            assertThat("first error who", firstError.get("who"), is(equalTo("parser")));
        }
    }
}
