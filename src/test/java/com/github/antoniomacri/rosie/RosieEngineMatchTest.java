package com.github.antoniomacri.rosie;

import com.github.antoniomacri.rosie.encoding.Decoder;
import com.github.antoniomacri.rosie.encoding.Decoders;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import com.revinate.assertj.json.JsonPathAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class RosieEngineMatchTest {
    private RosieEngine rosie;

    @BeforeAll
    public static void beforeClass() {
        com.jayway.jsonpath.Configuration.setDefaults(new com.jayway.jsonpath.Configuration.Defaults() {
            private final JsonProvider jsonProvider = new JacksonJsonProvider();
            private final MappingProvider mappingProvider = new JacksonMappingProvider();

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }

            @Override
            public Set<Option> options() {
                return EnumSet.noneOf(Option.class);
            }
        });
    }

    @BeforeEach
    public void init() {
        rosie = new RosieEngine();
    }

    @AfterEach
    public void close() {
        rosie.close();
    }


    @Test
    public void testMatchesSuccess() {
        Pattern pattern = rosie.compile("[:digit:]+");

        boolean matches = pattern.matches("123");
        assertThat(matches).isTrue();
    }

    @Test
    public void testMatchesNotFull() {
        Pattern pattern = rosie.compile("[:digit:]+");

        boolean matches = pattern.matches("123a");
        assertThat(matches).isTrue();
    }

    @Test
    public void testMatchesFailure() {
        Pattern pattern = rosie.compile("[:digit:]+");

        boolean matches = pattern.matches("a123");
        assertThat(matches).isFalse();
    }

    @Test
    public void testMatchesSuccessWithSkip() {
        Pattern pattern = rosie.compile("[:digit:]+");

        boolean matches = pattern.matches("a123", 1);
        assertThat(matches).isTrue();
    }

    @Test
    public void testMatchesNotFullWithSkip() {
        Pattern pattern = rosie.compile("[:digit:]+");

        boolean matches = pattern.matches("a123a", 1);
        assertThat(matches).isTrue();
    }

    @Test
    public void testMatchesFailureWithSkip() {
        Pattern pattern = rosie.compile("[:digit:]+");

        boolean matches = pattern.matches("0a123", 1);
        assertThat(matches).isFalse();
    }


    @Test
    public void testBoolMatchSuccess() {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("123", Decoders.BOOL);

        assertThat(match.matches()).isTrue();
        assertThat(match.getData()).isNull();
        assertThat(match.isAborted()).isFalse();
        assertThat(match.getRemainingBytes()).isEqualTo(0);
    }

    @Test
    public void testBoolMatchSuccessWithRemaining() {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("123a", Decoders.BOOL);

        assertThat(match.matches()).isTrue();
        assertThat(match.getData()).isNull();
        assertThat(match.isAborted()).isFalse();
        assertThat(match.getRemainingBytes()).isEqualTo(1);
    }

    @Test
    public void testBoolMatchFailed() {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("a123", Decoders.BOOL);

        assertThat(match.matches()).isFalse();
        assertThat(match.getData()).isNull();
        assertThat(match.isAborted()).isFalse();
    }

    @Test
    public void testBoolMatchSuccessWithSkip() {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("a123", 1, Decoders.BOOL);

        assertThat(match.matches()).isTrue();
        assertThat(match.getData()).isNull();
        assertThat(match.isAborted()).isFalse();
        assertThat(match.getRemainingBytes()).isEqualTo(0);
    }

    @Test
    public void testBoolMatchSuccessWithSkipWithRemaining() {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("a123a", 1, Decoders.BOOL);

        assertThat(match.matches()).isTrue();
        assertThat(match.getData()).isNull();
        assertThat(match.isAborted()).isFalse();
        assertThat(match.getRemainingBytes()).isEqualTo(1);
    }

    @Test
    public void testBoolMatchFailedWithSkip() {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("0a123", 1, Decoders.BOOL);

        assertThat(match.matches()).isFalse();
        assertThat(match.getData()).isNull();
        assertThat(match.isAborted()).isFalse();
    }


    @Test
    public void testLineMatchSuccess() {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("123", Decoders.LINE);

        assertThat(match.matches()).isTrue();
        assertThat(match.getData()).isEqualTo("123");
        assertThat(match.isAborted()).isFalse();
        assertThat(match.getRemainingBytes()).isEqualTo(0);
    }

    @Test
    public void testLineMatchSuccessWithRemaining() {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("123a", Decoders.LINE);

        assertThat(match.matches()).isTrue();
        assertThat(match.getData()).isEqualTo("123a");
        assertThat(match.isAborted()).isFalse();
        assertThat(match.getRemainingBytes()).isEqualTo(1);
    }

    @Test
    public void testLineMatchFailed() {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("a123", Decoders.LINE);

        assertThat(match.matches()).isFalse();
        assertThat(match.getData()).isNull();
        assertThat(match.isAborted()).isFalse();
    }

    @Test
    public void testLineMatchSuccessWithSkip() {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("a123", 1, Decoders.LINE);

        assertThat(match.matches()).isTrue();
        assertThat(match.getData()).isEqualTo("a123");
        assertThat(match.isAborted()).isFalse();
        assertThat(match.getRemainingBytes()).isEqualTo(0);
    }

    @Test
    public void testLineMatchSuccessWithSkipWithRemaining() {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("a123a", 1, Decoders.LINE);

        assertThat(match.matches()).isTrue();
        assertThat(match.getData()).isEqualTo("a123a");
        assertThat(match.isAborted()).isFalse();
        assertThat(match.getRemainingBytes()).isEqualTo(1);
    }

    @Test
    public void testLineMatchFailedWithSkip() {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("0a123", 1, Decoders.LINE);

        assertThat(match.matches()).isFalse();
        assertThat(match.getData()).isNull();
        assertThat(match.isAborted()).isFalse();
    }


    @Test
    public void testColorMatchSuccess() {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("123", Decoders.COLOR);

        assertThat(match.matches()).isTrue();
        assertThat(match.getData()).isEqualTo("\u001b[39;1m123\u001b[0m");
        assertThat(match.isAborted()).isFalse();
        assertThat(match.getRemainingBytes()).isEqualTo(0);
    }

    @Test
    public void testColorMatchSuccessWithRemaining() {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("123a", Decoders.COLOR);

        assertThat(match.matches()).isTrue();
        assertThat(match.getData()).isEqualTo("\u001b[39;1m123\u001b[0ma");
        assertThat(match.isAborted()).isFalse();
        assertThat(match.getRemainingBytes()).isEqualTo(1);
    }

    @Test
    public void testColorMatchFailed() {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("a123", Decoders.COLOR);

        assertThat(match.matches()).isFalse();
        assertThat(match.getData()).isNull();
        assertThat(match.isAborted()).isFalse();
    }

    @Test
    public void testColorMatchSuccessWithSkip() {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("a123", 1, Decoders.COLOR);

        assertThat(match.matches()).isTrue();
        assertThat(match.getData()).isEqualTo("a\u001b[39;1m123\u001b[0m");
        assertThat(match.isAborted()).isFalse();
        assertThat(match.getRemainingBytes()).isEqualTo(0);
    }

    @Test
    public void testColorMatchSuccessWithSkipWithRemaining() {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("a123a", 1, Decoders.COLOR);

        assertThat(match.matches()).isTrue();
        assertThat(match.getData()).isEqualTo("a\u001b[39;1m123\u001b[0ma");
        assertThat(match.isAborted()).isFalse();
        assertThat(match.getRemainingBytes()).isEqualTo(1);
    }

    @Test
    public void testColorMatchFailedWithSkip() {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("0a123", 1, Decoders.COLOR);

        assertThat(match.matches()).isFalse();
        assertThat(match.getData()).isNull();
        assertThat(match.isAborted()).isFalse();
    }


    @Test
    public void testJsonMatchSuccess() {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("123", Decoders.JSON);

        assertThat(match.matches()).isTrue();
        assertThat(match.getData()).isNotNull();
        assertThat(match.isAborted()).isFalse();
        assertThat(match.getRemainingBytes()).isEqualTo(0);

        DocumentContext ctx = JsonPath.parse(match.getData());
        JsonPathAssert.assertThat(ctx).jsonPathAsString("$.data").isEqualTo("123");
    }

    @Test
    public void testJsonMatchSuccessWithRemaining() {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("123a", Decoders.JSON);

        assertThat(match.matches()).isTrue();
        assertThat(match.getData()).isNotNull();
        assertThat(match.isAborted()).isFalse();
        assertThat(match.getRemainingBytes()).isEqualTo(1);

        DocumentContext ctx = JsonPath.parse(match.getData());
        JsonPathAssert.assertThat(ctx).jsonPathAsString("$.data").isEqualTo("123");
    }

    @Test
    public void testJsonMatchFailed() {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("a123", Decoders.JSON);

        assertThat(match.matches()).isFalse();
        assertThat(match.getData()).isNull();
        assertThat(match.isAborted()).isFalse();
    }

    @Test
    public void testJsonMatchSuccessWithSkip() {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("a123", 1, Decoders.JSON);

        assertThat(match.matches()).isTrue();
        assertThat(match.getData()).isNotNull();
        assertThat(match.isAborted()).isFalse();
        assertThat(match.getRemainingBytes()).isEqualTo(0);

        DocumentContext ctx = JsonPath.parse(match.getData());
        JsonPathAssert.assertThat(ctx).jsonPathAsString("$.data").isEqualTo("123");
    }

    @Test
    public void testJsonMatchSuccessWithSkipWithRemaining() {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("a123a", 1, Decoders.JSON);

        assertThat(match.matches()).isTrue();
        assertThat(match.getData()).isNotNull();
        assertThat(match.isAborted()).isFalse();
        assertThat(match.getRemainingBytes()).isEqualTo(1);

        DocumentContext ctx = JsonPath.parse(match.getData());
        JsonPathAssert.assertThat(ctx).jsonPathAsString("$.data").isEqualTo("123");
    }

    @Test
    public void testJsonMatchFailedWithSkip() {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("0a123", 1, Decoders.JSON);

        assertThat(match.matches()).isFalse();
        assertThat(match.getData()).isNull();
        assertThat(match.isAborted()).isFalse();
    }


    @Test
    public void testJsonMatchSuccessNested() {
        rosie.importPackage("net");
        Pattern pattern = rosie.compile("net.any");

        Match match = pattern.match("1.2.3.4", Decoders.JSON);

        assertThat(match.matches()).isTrue();
        assertThat(match.getData()).isNotNull();
        assertThat(match.isAborted()).isFalse();
        assertThat(match.getRemainingBytes()).isEqualTo(0);

        DocumentContext ctx = JsonPath.parse(match.getData());
        JsonPathAssert.assertThat(ctx).jsonPathAsString("$.data").isEqualTo("1.2.3.4");
        JsonPathAssert.assertThat(ctx).jsonPathAsString("$.type").isEqualTo("net.any");
        JsonPathAssert.assertThat(ctx).jsonPathAsListOf("$.subs", Map.class).hasSize(1);
        JsonPathAssert.assertThat(ctx).jsonPathAsString("$.subs[0].subs[0].data").isEqualTo("1.2.3.4");
        JsonPathAssert.assertThat(ctx).jsonPathAsString("$.subs[0].subs[0].type").isEqualTo("net.ipv4");
    }


    @Test
    public void testMatchInvalidEncoderName() {
        assertThrows(IllegalArgumentException.class, () -> {
            Pattern pattern = rosie.compile("[:digit:]+");

            String input = "889900112233445566778899100101102103104105106107108109110xyz";
            pattern.match(input, new Decoder<Match>("this_is_not_a_valid_encoder_name") {
                @Override
                public Match decode(Match match) {
                    return null;
                }
            });
        });
    }
}
