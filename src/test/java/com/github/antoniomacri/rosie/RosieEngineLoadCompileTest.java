package com.github.antoniomacri.rosie;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
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
        LoadResult result = rosie.load("package x; foo = \"foo\"");
        assertThat(result.ok, is(1));
        assertThat(result.packageName, is(equalTo("x")));
        assertThat(result.errors, is(nullValue()));
    }

    @Test
    public void testCompileFoo() {
        testLoad();

        CompilationResult compiled = rosie.compile("x.foo");
        assertThat("compiled.pat", compiled.pat, is(notNullValue()));
        assertThat("compiled.pat", compiled.pat, greaterThan(0));
        assertThat("compiled.errors", compiled.errors, is(nullValue()));
    }

    @Test
    public void testCompilePattern() {
        CompilationResult compiled = rosie.compile("[:digit:]+");
        assertThat("compiled.pat", compiled.pat, is(notNullValue()));
        assertThat("compiled.pat", compiled.pat, greaterThan(0));
        assertThat("compiled.errors", compiled.errors, is(nullValue()));
    }

    @Test
    public void testCompileInvalidPattern() throws IOException {
        CompilationResult compiled = rosie.compile("[:foobar:]+");

        assertThat("compiled.pat", compiled.pat, is(nullValue()));

        ObjectMapper objectMapper = new ObjectMapper();
        List errors = objectMapper.readValue(compiled.errors, List.class);

        assertThat("errors size", errors.size(), is(greaterThan(0)));

        Map firstError = (Map) errors.get(0);
        assertThat("first error message", firstError.get("message"), is(notNullValue()));
        assertThat("first error who", firstError.get("who"), is(equalTo("compiler")));
    }

    @Test
    public void testCompileInvalidPatternNumInt() throws IOException {
        CompilationResult compiled = rosie.compile("num.int");

        assertThat("compiled.pat", compiled.pat, is(nullValue()));

        ObjectMapper objectMapper = new ObjectMapper();
        List errors = objectMapper.readValue(compiled.errors, List.class);

        assertThat("errors size", errors.size(), is(greaterThan(0)));

        Map firstError = (Map) errors.get(0);
        assertThat("first error message", firstError.get("message"), is(notNullValue()));
        assertThat("first error who", firstError.get("who"), is(equalTo("compiler")));
    }

    @Test
    public void testLoadInvalidPatternFoo() throws IOException {
        LoadResult result = rosie.load("foo = \"");

        assertThat("result.ok", result.ok, is(0));

        ObjectMapper objectMapper = new ObjectMapper();
        List errors = objectMapper.readValue(result.errors, List.class);

        assertThat("errors size", errors.size(), is(greaterThan(0)));

        Map firstError = (Map) errors.get(0);
        assertThat("first error message", firstError.get("message"), is(notNullValue()));
        assertThat("first error who", firstError.get("who"), is(equalTo("parser")));
    }
}
