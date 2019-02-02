package com.github.antoniomacri.rosie;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.collection.IsMapContaining;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;


public class RosieEngineRefsTest {
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
    public void expressionRefsA() throws IOException {
        ParseResult result = rosie.expression_refs("A");
        assertThat(result.result, is(notNullValue()));
        assertThat(result.messages, is(nullValue()));

        ObjectMapper objectMapper = new ObjectMapper();
        List<?> list = objectMapper.readValue(result.result, List.class);

        assertThat(list, is(notNullValue()));
        assertThat(list, IsCollectionWithSize.hasSize(1));

        Map map = (Map) list.get(0);
        Map<?, ?> ref = (Map) map.get("ref");
        assertThat(ref, is(notNullValue()));
        assertThat(ref, IsMapContaining.hasEntry(is("localname"), is(notNullValue())));
        assertThat(ref, IsMapContaining.hasEntry("localname", "A"));
    }

    @Test
    public void expressionRefsB() throws IOException {
        ParseResult result = rosie.expression_refs("A / \"hello\" / B.c [:digit:]+ mac:#hi");
        assertThat(result.result, is(notNullValue()));
        assertThat(result.messages, is(nullValue()));

        ObjectMapper objectMapper = new ObjectMapper();
        List<?> list = objectMapper.readValue(result.result, List.class);

        assertThat(list, is(notNullValue()));
        assertThat(list, IsCollectionWithSize.hasSize(3));

        Map<?, ?> i0 = (Map) list.get(0);
        assertThat(i0, is(notNullValue()));
        assertThat(i0, IsMapContaining.hasEntry(is("ref"), is(notNullValue())));

        Map<?, ?> ref0 = (Map) i0.get("ref");
        assertThat(ref0, IsMapContaining.hasEntry("localname", "A"));

        Map<?, ?> i1 = (Map) list.get(1);
        assertThat(i1, is(notNullValue()));
        assertThat(i1, IsMapContaining.hasEntry(is("ref"), is(notNullValue())));

        Map<?, ?> ref1 = (Map) i1.get("ref");
        assertThat(ref1, IsMapContaining.hasEntry("packagename", "B"));
        assertThat(ref1, IsMapContaining.hasEntry("localname", "c"));

        Map<?, ?> i2 = (Map) list.get(2);
        assertThat(i2, is(notNullValue()));
        assertThat(i2, IsMapContaining.hasEntry(is("ref"), is(notNullValue())));

        Map<?, ?> ref2 = (Map) i2.get("ref");
        assertThat(ref2, IsMapContaining.hasEntry("localname", "mac"));
    }

    @Test
    public void expressionRefsError() {
        ParseResult result = rosie.expression_refs("A // B.c");  // syntax error
        assertThat(result.result, is(nullValue()));
        assertThat(result.messages, is(notNullValue()));
    }


    @Test
    public void blockRefs() throws IOException {
        ParseResult result = rosie.block_refs("x = A / B.c; y=[:alpha:] mac:#tagname");
        assertThat(result.result, is(notNullValue()));
        assertThat(result.messages, is(nullValue()));

        ObjectMapper objectMapper = new ObjectMapper();
        List<?> list = objectMapper.readValue(result.result, List.class);

        assertThat(list, is(notNullValue()));
        assertThat(list, IsCollectionWithSize.hasSize(3));

        Map<?, ?> i0 = (Map) list.get(0);
        assertThat(i0, is(notNullValue()));
        assertThat(i0, IsMapContaining.hasEntry(is("ref"), is(notNullValue())));

        Map<?, ?> ref0 = (Map) i0.get("ref");
        assertThat(ref0, IsMapContaining.hasEntry("localname", "A"));

        Map<?, ?> i1 = (Map) list.get(1);
        assertThat(i1, is(notNullValue()));
        assertThat(i1, IsMapContaining.hasEntry(is("ref"), is(notNullValue())));

        Map<?, ?> ref1 = (Map) i1.get("ref");
        assertThat(ref1, IsMapContaining.hasEntry("packagename", "B"));
        assertThat(ref1, IsMapContaining.hasEntry("localname", "c"));

        Map<?, ?> i2 = (Map) list.get(2);
        assertThat(i2, is(notNullValue()));
        assertThat(i2, IsMapContaining.hasEntry(is("ref"), is(notNullValue())));

        Map<?, ?> ref2 = (Map) i2.get("ref");
        assertThat(ref2, IsMapContaining.hasEntry("localname", "mac"));
    }

    @Test
    public void blockRefsError() {
        ParseResult result = rosie.block_refs(" = A / B.c; y=[:alpha:]");  // syntax error
        assertThat(result.result, is(nullValue()));
        assertThat(result.messages, is(notNullValue()));
    }
}
