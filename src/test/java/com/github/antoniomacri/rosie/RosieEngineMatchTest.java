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
        Match match = pattern.match(input, skip, "json");

        assertThat("match result", match, is(notNullValue()));
        assertThat("matched?", match.matches(), is(true));
        assertThat("matched string", match.match(), is("21"));

        assertThat("skipped", match.skipped(), is(equalTo(0)));
        assertThat("remaining", match.remaining(), is(equalTo(0)));
        assertThat("totalMillis", match.totalMillis(), is(greaterThan(0)));
        assertThat("matchMillis", match.matchMillis(), is(greaterThan(0)));

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

        Match match = pattern.match("xyz", "json");

        assertThat("match result", match, is(notNullValue()));
        assertThat("matched?", match.matches(), is(false));
        assertThat("matched string", match.match(), is(nullValue()));

        assertThat("skipped", match.skipped(), is(equalTo(0)));
        assertThat("remaining", match.remaining(), is(equalTo(3)));
        assertThat("totalMillis", match.totalMillis(), is(greaterThan(0)));
        assertThat("matchMillis", match.matchMillis(), is(greaterThan(0)));
    }

    @Test
    public void testMatchNumberMixed() {
        Pattern pattern = rosie.compile("[:digit:]+");

        String input = "889900112233445566778899100101102103104105106107108109110xyz";
        Match match = pattern.match(input, "json");

        assertThat("match result", match, is(notNullValue()));
        assertThat("matched?", match.matches(), is(true));
        assertThat("matched string", match.match(), is(equalTo(input.substring(0, input.length() - 3))));

        assertThat("skipped", match.skipped(), is(equalTo(0)));
        assertThat("remaining", match.remaining(), is(equalTo(3)));
        assertThat("totalMillis", match.totalMillis(), is(greaterThan(0)));
        assertThat("matchMillis", match.matchMillis(), is(greaterThan(0)));

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
        Match match = pattern.match(input, 9, "json");

        assertThat("match result", match, is(notNullValue()));
        assertThat("matched?", match.matches(), is(true));
        assertThat("matched string", match.match(), is(equalTo(input.substring(9, input.length() - 3))));

        assertThat("skipped", match.skipped(), is(equalTo(0)));
        assertThat("remaining", match.remaining(), is(equalTo(3)));
        assertThat("totalMillis", match.totalMillis(), is(greaterThan(0)));
        assertThat("matchMillis", match.matchMillis(), is(greaterThan(0)));

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
        Match match = pattern.match(input, "line");

        assertThat("match result", match, is(notNullValue()));
        assertThat("matched?", match.matches(), is(true));
        assertThat("matched string", match.match(), is(equalTo(input)));

        assertThat("skipped", match.skipped(), is(equalTo(0)));
        assertThat("remaining", match.remaining(), is(equalTo(3)));
        assertThat("totalMillis", match.totalMillis(), is(greaterThan(0)));
        assertThat("matchMillis", match.matchMillis(), is(greaterThan(0)));
    }

    @Test
    public void testMatchNumberMixedBool() {
        Pattern pattern = rosie.compile("[:digit:]+");

        String input = "889900112233445566778899100101102103104105106107108109110xyz";
        Match match = pattern.match(input, "bool");
        assertThat("matched?", match.matches(), is(true));

        assertThat("skipped", match.skipped(), is(equalTo(0)));
        assertThat("remaining", match.remaining(), is(equalTo(3)));
        assertThat("totalMillis", match.totalMillis(), is(greaterThan(0)));
        assertThat("matchMillis", match.matchMillis(), is(greaterThan(0)));
    }

    @Test
    public void testMatchNumberMixedColor() {
        Pattern pattern = rosie.compile("[:digit:]+");

        String input = "889900112233445566778899100101102103104105106107108109110xyz";
        Match match = pattern.match(input, "color");

        assertThat("match result", match, is(notNullValue()));
        assertThat("matched?", match.matches(), is(true));
        assertThat("matched string", match.match(), startsWith(new String(new byte[]{0x1B, '['})));

        assertThat("skipped", match.skipped(), is(equalTo(0)));
        assertThat("remaining", match.remaining(), is(equalTo(3)));
        assertThat("totalMillis", match.totalMillis(), is(greaterThan(0)));
        assertThat("matchMillis", match.matchMillis(), is(greaterThan(0)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMatchInvalidEncoderName() {
        Pattern pattern = rosie.compile("[:digit:]+");

        String input = "889900112233445566778899100101102103104105106107108109110xyz";
        pattern.match(input, "this_is_not_a_valid_encoder_name");
    }
}
