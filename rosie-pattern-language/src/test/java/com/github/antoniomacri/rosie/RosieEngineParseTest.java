package com.github.antoniomacri.rosie;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.collection.IsMapContaining;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;


public class RosieEngineParseTest {
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
    public void parseExpressionA() throws IOException {
        ParseResult result = rosie.parse_expression("A");
        assertThat(result.result, is(notNullValue()));
        assertThat(result.messages, is(nullValue()));

        ObjectMapper objectMapper = new ObjectMapper();
        Map<?, ?> m = objectMapper.readValue(result.result, Map.class);

        assertThat(m, IsMapContaining.hasEntry(is("ref"), is(notNullValue())));
        assertThat(m, IsMapContaining.hasEntry(is("ref"), instanceOf(Map.class)));

        Map<?, ?> ref = (Map) m.get("ref");
        assertThat(ref, IsMapContaining.hasEntry(is("localname"), is(notNullValue())));
        assertThat(ref, IsMapContaining.hasEntry("localname", "A"));
    }

    @Test
    public void parseExpressionB() throws IOException {
        ParseResult result = rosie.parse_expression("A / B.c");
        assertThat(result.result, is(notNullValue()));
        assertThat(result.messages, is(nullValue()));

        ObjectMapper objectMapper = new ObjectMapper();
        Map<?, ?> m = objectMapper.readValue(result.result, Map.class);

        assertThat(m, IsMapContaining.hasEntry(is("choice"), is(notNullValue())));
        assertThat(m, IsMapContaining.hasEntry(is("choice"), instanceOf(Map.class)));

        Map<?, ?> choice = (Map) m.get("choice");
        assertThat(choice, IsMapContaining.hasEntry(is("exps"), is(notNullValue())));
        assertThat(choice, IsMapContaining.hasEntry(is("exps"), instanceOf(List.class)));

        List exps = (List) choice.get("exps");
        Map<?, ?> exp2 = (Map) exps.get(1);
        assertThat(exp2, IsMapContaining.hasEntry(is("ref"), is(notNullValue())));
        assertThat(exp2, IsMapContaining.hasEntry(is("ref"), instanceOf(Map.class)));

        Map<?, ?> ref = (Map) exp2.get("ref");
        assertThat(ref, IsMapContaining.hasEntry("packagename", "B"));
        assertThat(ref, IsMapContaining.hasEntry("localname", "c"));
    }

    @Test
    public void parseExpressionError() {
        ParseResult result = rosie.parse_expression("A // B.c");  // syntax error
        assertThat(result.result, is(nullValue()));
        assertThat(result.messages, is(notNullValue()));
    }

    @Test
    public void parseBlockOk() throws IOException {
        ParseResult result = rosie.parse_block("x = A / B.c; y=[:alpha:]");
        assertThat(result.result, is(notNullValue()));
        assertThat(result.messages, is(nullValue()));

        ObjectMapper objectMapper = new ObjectMapper();
        Map<?, ?> m = objectMapper.readValue(result.result, Map.class);

        assertThat(m, IsMapContaining.hasEntry(is("block"), is(notNullValue())));
        assertThat(m, IsMapContaining.hasEntry(is("block"), instanceOf(Map.class)));

        Map<?, ?> block = (Map) m.get("block");
        assertThat(block, IsMapContaining.hasEntry(is("stmts"), is(notNullValue())));
        assertThat(block, IsMapContaining.hasEntry(is("stmts"), instanceOf(List.class)));

        List stmts = (List) block.get("stmts");

        {
            Map<?, ?> stmts0 = (Map) stmts.get(0);
            assertThat(stmts0, IsMapContaining.hasEntry(is("binding"), is(notNullValue())));
            assertThat(stmts0, IsMapContaining.hasEntry(is("binding"), instanceOf(Map.class)));

            Map<?, ?> binding = (Map) stmts0.get("binding");
            assertThat(binding, IsMapContaining.hasEntry(is("ref"), is(notNullValue())));
            assertThat(binding, IsMapContaining.hasEntry(is("ref"), instanceOf(Map.class)));

            Map<?, ?> ref = (Map) binding.get("ref");
            assertThat(ref, IsMapContaining.hasEntry(is("ref"), is(notNullValue())));
            assertThat(ref, IsMapContaining.hasEntry(is("ref"), instanceOf(Map.class)));

            ref = (Map) ref.get("ref");
            assertThat(ref, is(notNullValue()));
            assertThat(ref, IsMapContaining.hasEntry(is("localname"), is(notNullValue())));
            assertThat(ref, IsMapContaining.hasEntry("localname", "x"));
        }

        {
            Map<?, ?> stmts1 = (Map) stmts.get(1);
            assertThat(stmts1, IsMapContaining.hasEntry(is("binding"), is(notNullValue())));
            assertThat(stmts1, IsMapContaining.hasEntry(is("binding"), instanceOf(Map.class)));

            Map<?, ?> binding = (Map) stmts1.get("binding");
            assertThat(binding, IsMapContaining.hasEntry(is("ref"), is(notNullValue())));
            assertThat(binding, IsMapContaining.hasEntry(is("ref"), instanceOf(Map.class)));

            Map<?, ?> ref = (Map) binding.get("ref");
            assertThat(ref, IsMapContaining.hasEntry(is("ref"), is(notNullValue())));
            assertThat(ref, IsMapContaining.hasEntry(is("ref"), instanceOf(Map.class)));

            ref = (Map) ref.get("ref");
            assertThat(ref, is(notNullValue()));
            assertThat(ref, IsMapContaining.hasEntry(is("localname"), is(notNullValue())));
            assertThat(ref, IsMapContaining.hasEntry("localname", "y"));
        }
    }

    @Test
    public void parseBlockError() {
        ParseResult result = rosie.parse_block(" = A / B.c; y=[:alpha:]");  // syntax error
        assertThat(result.result, is(nullValue()));
        assertThat(result.messages, is(notNullValue()));
    }
}
