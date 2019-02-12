package com.github.antoniomacri.rosie;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.greaterThan;


public class RosieEngineMatchTest {
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
    public void testMatchNumberOk() {
        Pattern pattern = rosie.compile("[:digit:]+");

        String input = "321";
        int skip = 1;
        Match match = rosie.match(pattern, input, skip, "json");

        assertThat("match result", match, is(notNullValue()));
        assertThat("matched?", match.matches(), is(true));
        assertThat("matched string", match.match(), is("21"));

        assertThat("leftover", match.leftover, is(equalTo(0)));
        assertThat("abend", match.abend, is(equalTo(0)));
        assertThat("ttotal", match.ttotal, is(greaterThan(0)));
        assertThat("tmatch", match.tmatch, is(greaterThan(0)));

        assertThat("matched json", match.jsonMatchResult(), is(notNullValue()));
        assertThat("matched type", match.jsonMatchResult().type(), is("*"));
        assertThat("match start", match.jsonMatchResult().start(), is(1));
        assertThat("match end", match.jsonMatchResult().end(), is(3));
        assertThat("matched string", input.substring(match.jsonMatchResult().start(), match.jsonMatchResult().end()), is(match.match()));
        assertThat("submatches", match.jsonMatchResult().subs(), is(nullValue()));
    }

    @Test
    public void testMatchNumberKo() {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = rosie.match(pattern, "xyz", 0, "json");

        assertThat("match result", match, is(notNullValue()));
        assertThat("matched?", match.matches(), is(false));
        assertThat("matched string", match.match(), is(nullValue()));

        assertThat("leftover", match.leftover, is(equalTo(3)));
        assertThat("abend", match.abend, is(equalTo(0)));
        assertThat("ttotal", match.ttotal, is(greaterThan(0)));
        assertThat("tmatch", match.tmatch, is(greaterThan(0)));
    }

    @Test
    public void testMatchNumberMixed() {
        Pattern pattern = rosie.compile("[:digit:]+");

        String input = "889900112233445566778899100101102103104105106107108109110xyz";
        Match match = rosie.match(pattern, input, 0, "json");

        assertThat("match result", match, is(notNullValue()));
        assertThat("matched?", match.matches(), is(true));
        assertThat("matched string", match.match(), is(equalTo(input.substring(0, input.length() - 3))));

        assertThat("leftover", match.leftover, is(equalTo(3)));
        assertThat("abend", match.abend, is(equalTo(0)));
        assertThat("ttotal", match.ttotal, is(greaterThan(0)));
        assertThat("tmatch", match.tmatch, is(greaterThan(0)));

        assertThat("matched json", match.jsonMatchResult(), is(notNullValue()));
        assertThat("matched string", input.substring(match.jsonMatchResult().start(), match.jsonMatchResult().end()), is(match.match()));
        assertThat("matched type", match.jsonMatchResult().type(), is("*"));
        assertThat("match start", match.jsonMatchResult().start(), is(0));
        assertThat("match end", match.jsonMatchResult().end(), is(equalTo(input.length() - 3)));  // due to the "xyz" at the end
        assertThat("submatches", match.jsonMatchResult().subs(), is(nullValue()));
    }

    @Test
    public void testMatchNumberMixed2() {
        Pattern pattern = rosie.compile("[:digit:]+");

        String input = "889900112233445566778899100101102103104105106107108109110xyz";
        Match match = rosie.match(pattern, input, 9, "json");

        assertThat("match result", match, is(notNullValue()));
        assertThat("matched?", match.matches(), is(true));
        assertThat("matched string", match.match(), is(equalTo(input.substring(9, input.length() - 3))));

        assertThat("leftover", match.leftover, is(equalTo(3)));
        assertThat("abend", match.abend, is(equalTo(0)));
        assertThat("ttotal", match.ttotal, is(greaterThan(0)));
        assertThat("tmatch", match.tmatch, is(greaterThan(0)));

        assertThat("matched json", match.jsonMatchResult(), is(notNullValue()));
        assertThat("matched string", input.substring(match.jsonMatchResult().start(), match.jsonMatchResult().end()), is(match.match()));
        assertThat("matched type", match.jsonMatchResult().type(), is("*"));
        assertThat("match start", match.jsonMatchResult().start(), is(9));
        assertThat("match end", match.jsonMatchResult().end(), is(equalTo(input.length() - 3)));  // due to the "xyz" at the end
        assertThat("submatches", match.jsonMatchResult().subs(), is(nullValue()));
    }

    @Test
    public void testMatchNumberMixedLine() {
        Pattern pattern = rosie.compile("[:digit:]+");

        String input = "889900112233445566778899100101102103104105106107108109110xyz";
        Match match = rosie.match(pattern, input, 0, "line");

        assertThat("match result", match, is(notNullValue()));
        assertThat("matched?", match.matches(), is(true));
        assertThat("matched string", match.match(), is(equalTo(input)));

        assertThat("leftover", match.leftover, is(equalTo(3)));
        assertThat("abend", match.abend, is(equalTo(0)));
        assertThat("ttotal", match.ttotal, is(greaterThan(0)));
        assertThat("tmatch", match.tmatch, is(greaterThan(0)));
    }

    @Test
    public void testMatchNumberMixedBool() {
        Pattern pattern = rosie.compile("[:digit:]+");

        String input = "889900112233445566778899100101102103104105106107108109110xyz";
        Match match = rosie.match(pattern, input, 0, "bool");
        assertThat("matched?", match.matches(), is(true));

        assertThat("leftover", match.leftover, is(equalTo(3)));
        assertThat("abend", match.abend, is(equalTo(0)));
        assertThat("ttotal", match.ttotal, is(greaterThan(0)));
        assertThat("tmatch", match.tmatch, is(greaterThan(0)));
    }

    @Test
    public void testMatchNumberMixedColor() {
        Pattern pattern = rosie.compile("[:digit:]+");

        String input = "889900112233445566778899100101102103104105106107108109110xyz";
        Match match = rosie.match(pattern, input, 0, "color");

        assertThat("match result", match, is(notNullValue()));
        assertThat("matched?", match.matches(), is(true));
        assertThat("matched string", match.match(), startsWith(new String(new byte[]{0x1B, '['})));

        assertThat("leftover", match.leftover, is(equalTo(3)));
        assertThat("abend", match.abend, is(equalTo(0)));
        assertThat("ttotal", match.ttotal, is(greaterThan(0)));
        assertThat("tmatch", match.tmatch, is(greaterThan(0)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMatchInvalidEncoderName() {
        Pattern pattern = rosie.compile("[:digit:]+");

        String input = "889900112233445566778899100101102103104105106107108109110xyz";
        rosie.match(pattern, input, 0, "this_is_not_a_valid_encoder_name");
    }
}
