package com.github.antoniomacri.rosie;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.greaterThan;


public class RosieEngineMatchTest {
    private RosieEngine rosie;

    @Before
    public void init() {
        rosie = new RosieEngine();
    }


    @Test
    public void testMatchNumberOk() throws IOException {
        CompilationResult compiled = rosie.compile("[:digit:]+");

        MatchResult result = rosie.match(compiled.pat, "321", 2, "json");
        assertThat("matched string", result.data, is(notNullValue()));

        ObjectMapper objectMapper = new ObjectMapper();
        Map m = objectMapper.readValue(result.data, Map.class);

        assertThat("type", m.get("type"), is(equalTo("*")));
        assertThat("s", m.get("s"), is(equalTo(2))); // match started at char 2
        assertThat("e", m.get("e"), is(equalTo(4)));
        assertThat("data", m.get("data"), is(equalTo("21")));
        assertThat("leftover", result.leftover, is(equalTo(0)));
        assertThat("abend", result.abend, is(equalTo(0)));
        assertThat("ttotal", result.ttotal, is(greaterThan(0)));
        assertThat("tmatch", result.tmatch, is(greaterThan(0)));
    }

    @Test
    public void testMatchNumberKo() {
        CompilationResult compiled = rosie.compile("[:digit:]+");

        MatchResult result = rosie.match(compiled.pat, "xyz", 1, "json");
        assertThat("matched string", result.data, is(nullValue()));

        assertThat("leftover", result.leftover, is(equalTo(3)));
        assertThat("abend", result.abend, is(equalTo(0)));
        assertThat("ttotal", result.ttotal, is(greaterThan(0)));
        assertThat("tmatch", result.tmatch, is(greaterThan(0)));
    }

    @Test
    public void testMatchNumberMixed() throws IOException {
        CompilationResult compiled = rosie.compile("[:digit:]+");

        String input = "889900112233445566778899100101102103104105106107108109110xyz";
        MatchResult result = rosie.match(compiled.pat, input, 1, "json");
        assertThat("matched string", result.data, is(notNullValue()));

        ObjectMapper objectMapper = new ObjectMapper();
        Map m = objectMapper.readValue(result.data, Map.class);

        assertThat("type", m.get("type"), is(equalTo("*")));
        assertThat("s", m.get("s"), is(equalTo(1)));
        assertThat("e", m.get("e"), is(equalTo(input.length() - 3 + 1)));  // due to the "xyz" at the end
        assertThat("data", m.get("data"), is(equalTo(input.substring(0, input.length() - 3))));
        assertThat("leftover", result.leftover, is(equalTo(3)));
        assertThat("abend", result.abend, is(equalTo(0)));
        assertThat("ttotal", result.ttotal, is(greaterThan(0)));
        assertThat("tmatch", result.tmatch, is(greaterThan(0)));
    }

    @Test
    public void testMatchNumberMixed2() throws IOException {
        CompilationResult compiled = rosie.compile("[:digit:]+");

        String input = "889900112233445566778899100101102103104105106107108109110xyz";
        MatchResult result = rosie.match(compiled.pat, input, 10, "json");
        assertThat("matched string", result.data, is(notNullValue()));

        ObjectMapper objectMapper = new ObjectMapper();
        Map m = objectMapper.readValue(result.data, Map.class);

        assertThat("type", m.get("type"), is(equalTo("*")));
        assertThat("s", m.get("s"), is(equalTo(10)));
        assertThat("e", m.get("e"), is(equalTo(input.length() - 3 + 1)));  // due to the "xyz" at the end
        assertThat("data", m.get("data"), is(equalTo(input.substring(9, input.length() - 3))));
        assertThat("leftover", result.leftover, is(equalTo(3)));
        assertThat("abend", result.abend, is(equalTo(0)));
        assertThat("ttotal", result.ttotal, is(greaterThan(0)));
        assertThat("tmatch", result.tmatch, is(greaterThan(0)));
    }

    @Test
    public void testMatchNumberMixedLine() {
        CompilationResult compiled = rosie.compile("[:digit:]+");

        String input = "889900112233445566778899100101102103104105106107108109110xyz";
        MatchResult result = rosie.match(compiled.pat, input, 1, "line");
        assertThat("matched string", result.data, is(notNullValue()));
        assertThat("matched string", result.data, is(equalTo(input)));

        assertThat("leftover", result.leftover, is(equalTo(3)));
        assertThat("abend", result.abend, is(equalTo(0)));
        assertThat("ttotal", result.ttotal, is(greaterThan(0)));
        assertThat("tmatch", result.tmatch, is(greaterThan(0)));
    }

    @Test
    public void testMatchNumberMixedColor() {
        CompilationResult compiled = rosie.compile("[:digit:]+");

        String input = "889900112233445566778899100101102103104105106107108109110xyz";
        MatchResult result = rosie.match(compiled.pat, input, 1, "color");
        assertThat("matched string", result.data, is(notNullValue()));

        // only checking the first two chars, looking for the start of ANSI color sequence
        assertThat("matched string", result.data, startsWith(new String(new byte[]{0x1B, '['})));

        assertThat("leftover", result.leftover, is(equalTo(3)));
        assertThat("abend", result.abend, is(equalTo(0)));
        assertThat("ttotal", result.ttotal, is(greaterThan(0)));
        assertThat("tmatch", result.tmatch, is(greaterThan(0)));
    }
}
