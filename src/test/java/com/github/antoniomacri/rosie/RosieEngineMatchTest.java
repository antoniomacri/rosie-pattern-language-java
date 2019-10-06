package com.github.antoniomacri.rosie;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


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

        assertThat(match).isNotNull();
        assertThat(match.matches()).isTrue();
        assertThat(match.match()).isEqualTo("21");

        assertThat(match.isAborted()).isFalse();
        assertThat(match.getRemainingBytes()).isEqualTo(0);
        assertThat(match.getTotalMillis()).isGreaterThan(0);
        assertThat(match.getMatchMillis()).isGreaterThan(0);

        assertThat(match.jsonMatchResult()).isNotNull();
        assertThat(match.jsonMatchResult().type()).isEqualTo("*");
        assertThat(match.jsonMatchResult().start()).isEqualTo(1);
        assertThat(match.jsonMatchResult().end()).isEqualTo(3);
        assertThat(input.substring(match.jsonMatchResult().start(), match.jsonMatchResult().end())).isEqualTo(match.match());
        assertThat(match.jsonMatchResult().subs()).isNull();
    }

    @Test
    public void testMatchNumberKo() {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("xyz", "json");

        assertThat(match).isNotNull();
        assertThat(match.matches()).isFalse();
        assertThat(match.match()).isNull();

        assertThat(match.isAborted()).isFalse();
        assertThat(match.getRemainingBytes()).isEqualTo(3);
        assertThat(match.getTotalMillis()).isGreaterThan(0);
        assertThat(match.getMatchMillis()).isGreaterThan(0);
    }

    @Test
    public void testMatchNumberMixed() {
        Pattern pattern = rosie.compile("[:digit:]+");

        String input = "889900112233445566778899100101102103104105106107108109110xyz";
        Match match = pattern.match(input, "json");

        assertThat(match).isNotNull();
        assertThat(match.matches()).isTrue();
        assertThat(match.match()).isEqualTo(input.substring(0, input.length() - 3));

        assertThat(match.isAborted()).isFalse();
        assertThat(match.getRemainingBytes()).isEqualTo(3);
        assertThat(match.getTotalMillis()).isGreaterThan(0);
        assertThat(match.getMatchMillis()).isGreaterThan(0);

        assertThat(match.jsonMatchResult()).isNotNull();
        assertThat(input.substring(match.jsonMatchResult().start(), match.jsonMatchResult().end())).isEqualTo(match.match());
        assertThat(match.jsonMatchResult().type()).isEqualTo("*");
        assertThat(match.jsonMatchResult().start()).isEqualTo(0);
        assertThat(match.jsonMatchResult().end()).isEqualTo(input.length() - 3);  // due to the "xyz" at the end
        assertThat(match.jsonMatchResult().subs()).isNull();
    }

    @Test
    public void testMatchNumberMixed2() {
        Pattern pattern = rosie.compile("[:digit:]+");

        String input = "889900112233445566778899100101102103104105106107108109110xyz";
        Match match = pattern.match(input, 9, "json");

        assertThat(match).isNotNull();
        assertThat(match.matches()).isTrue();
        assertThat(match.match()).isEqualTo(input.substring(9, input.length() - 3));

        assertThat(match.isAborted()).isFalse();
        assertThat(match.getRemainingBytes()).isEqualTo(3);
        assertThat(match.getTotalMillis()).isGreaterThan(0);
        assertThat(match.getMatchMillis()).isGreaterThan(0);

        assertThat(match.jsonMatchResult()).isNotNull();
        assertThat(input.substring(match.jsonMatchResult().start(), match.jsonMatchResult().end())).isEqualTo(match.match());
        assertThat(match.jsonMatchResult().type()).isEqualTo("*");
        assertThat(match.jsonMatchResult().start()).isEqualTo(9);
        assertThat(match.jsonMatchResult().end()).isEqualTo(input.length() - 3);  // due to the "xyz" at the end
        assertThat(match.jsonMatchResult().subs()).isNull();
    }

    @Test
    public void testMatchNumberMixedLine() {
        Pattern pattern = rosie.compile("[:digit:]+");

        String input = "889900112233445566778899100101102103104105106107108109110xyz";
        Match match = pattern.match(input, "line");

        assertThat(match).isNotNull();
        assertThat(match.matches()).isTrue();
        assertThat(match.match()).isEqualTo(input);

        assertThat(match.isAborted()).isFalse();
        assertThat(match.getRemainingBytes()).isEqualTo(3);
        assertThat(match.getTotalMillis()).isGreaterThan(0);
        assertThat(match.getMatchMillis()).isGreaterThan(0);
    }

    @Test
    public void testMatchNumberMixedBool() {
        Pattern pattern = rosie.compile("[:digit:]+");

        String input = "889900112233445566778899100101102103104105106107108109110xyz";
        Match match = pattern.match(input, "bool");
        assertThat(match.matches()).isTrue();

        assertThat(match.isAborted()).isFalse();
        assertThat(match.getRemainingBytes()).isEqualTo(3);
        assertThat(match.getTotalMillis()).isGreaterThan(0);
        assertThat(match.getMatchMillis()).isGreaterThan(0);
    }

    @Test
    public void testMatchNumberMixedColor() {
        Pattern pattern = rosie.compile("[:digit:]+");

        String input = "889900112233445566778899100101102103104105106107108109110xyz";
        Match match = pattern.match(input, "color");

        assertThat(match).isNotNull();
        assertThat(match.matches()).isTrue();
        assertThat(match.match()).startsWith(new String(new byte[]{0x1B, '['}));

        assertThat(match.isAborted()).isFalse();
        assertThat(match.getRemainingBytes()).isEqualTo(3);
        assertThat(match.getTotalMillis()).isGreaterThan(0);
        assertThat(match.getMatchMillis()).isGreaterThan(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMatchInvalidEncoderName() {
        Pattern pattern = rosie.compile("[:digit:]+");

        String input = "889900112233445566778899100101102103104105106107108109110xyz";
        pattern.match(input, "this_is_not_a_valid_encoder_name");
    }
}
