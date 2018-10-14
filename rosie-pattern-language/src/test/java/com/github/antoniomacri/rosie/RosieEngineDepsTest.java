package com.github.antoniomacri.rosie;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.hamcrest.collection.IsMapContaining;
import org.hamcrest.collection.IsMapWithSize;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;


public class RosieEngineDepsTest {
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
    public void expressionDepsA() throws IOException {
        ParseResult result = rosie.expression_deps("A");
        assertThat(result.result, is(notNullValue()));
        assertThat(result.messages, is(nullValue()));

        ObjectMapper objectMapper = new ObjectMapper();
        Map<?, ?> map = objectMapper.readValue(result.result, Map.class);

        assertThat(map, is(notNullValue()));
        assertThat(map, IsMapWithSize.anEmptyMap());
    }


    @Test
    public void expressionDepsB() throws IOException {
        ParseResult result = rosie.expression_deps("A / \"hello\" / B.c [:digit:]+ p.mac:#hi");
        assertThat(result.result, is(notNullValue()));
        assertThat(result.messages, is(nullValue()));

        ObjectMapper objectMapper = new ObjectMapper();
        List<?> list = objectMapper.readValue(result.result, List.class);

        assertThat(list, is(notNullValue()));
        assertThat(list, IsCollectionWithSize.hasSize(2));
        assertThat(list, IsIterableContainingInOrder.contains("B", "p"));
    }

    @Test
    public void expressionRefsError() {
        ParseResult result = rosie.expression_deps("A // B.c");  // syntax error
        assertThat(result.result, is(nullValue()));
        assertThat(result.messages, is(notNullValue()));
    }


    @Test
    public void blockDeps() throws IOException {
        ParseResult result = rosie.block_deps("x = A / B.c; y=[:alpha:] p.mac:#tagname");
        assertThat(result.result, is(notNullValue()));
        assertThat(result.messages, is(nullValue()));

        ObjectMapper objectMapper = new ObjectMapper();
        Map<?, ?> map = objectMapper.readValue(result.result, Map.class);

        assertThat(map, is(notNullValue()));
        assertThat(map, IsMapContaining.hasEntry(is("implicit"), instanceOf(List.class)));

        List<?> list = (List) map.get("implicit");
        assertThat(list, IsCollectionWithSize.hasSize(2));
        assertThat(list, IsIterableContainingInOrder.contains("B", "p"));
    }

    @Test
    public void blockDepsB() throws IOException {
        ParseResult result = rosie.block_deps("import F as G, H; x = A / B.c; y=[:alpha:] p.mac:#tagname");
        assertThat(result.result, is(notNullValue()));
        assertThat(result.messages, is(nullValue()));

        ObjectMapper objectMapper = new ObjectMapper();
        Map<?, ?> map = objectMapper.readValue(result.result, Map.class);

        assertThat(map, is(notNullValue()));

        assertThat(map, IsMapContaining.hasEntry(is("implicit"), instanceOf(List.class)));

        List<?> implicit = (List) map.get("implicit");
        assertThat(implicit, IsCollectionWithSize.hasSize(2));
        assertThat(implicit, IsIterableContainingInOrder.contains("B", "p"));

        assertThat(map, IsMapContaining.hasEntry(is("explicit"), instanceOf(List.class)));

        List<?> explicit = (List) map.get("explicit");
        assertThat(explicit, IsCollectionWithSize.hasSize(2));

        assertThat(explicit.get(0), instanceOf(Map.class));
        Map<?, ?> explicit0 = (Map) explicit.get(0);
        assertThat(explicit0, IsMapContaining.hasEntry("as_name", "G"));
        assertThat(explicit0, IsMapContaining.hasEntry("importpath", "F"));

        assertThat(explicit.get(1), instanceOf(Map.class));
        Map<?, ?> explicit1 = (Map) explicit.get(1);
        assertThat(explicit1, not(IsMapContaining.hasKey("as_name")));
        assertThat(explicit1, IsMapContaining.hasEntry("importpath", "H"));
    }

    @Test
    public void blockDepsError() {
        ParseResult result = rosie.block_deps(" = A / B.c; y=[:alpha:]");  // syntax error
        assertThat(result.result, is(nullValue()));
        assertThat(result.messages, is(notNullValue()));
    }
}
