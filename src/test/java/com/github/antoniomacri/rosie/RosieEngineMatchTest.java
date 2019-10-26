package com.github.antoniomacri.rosie;

import com.github.antoniomacri.rosie.encoding.Decoder;
import com.github.antoniomacri.rosie.encoding.Decoders;
import com.jayway.jsonpath.Configuration;
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
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class RosieEngineMatchTest {
    private RosieEngine rosie;

    @BeforeAll
    static void beforeClass() {
        Configuration.setDefaults(new JacksonConfiguration());
    }

    @BeforeEach
    void init() {
        rosie = new RosieEngine();
    }

    @AfterEach
    void close() {
        rosie.close();
    }


    @CsvSource({
            "123,   0",
            "123a,  0",
            "a123,  1",
            "a123a, 1"
    })
    @ParameterizedTest
    void testMatchesSuccess(String input, int skip) {
        Pattern pattern = rosie.compile("[:digit:]+");

        boolean matches = pattern.matches(input, skip);
        assertThat(matches).isTrue();
    }

    @CsvSource({
            "a123,  0",
            "0a123, 1"
    })
    @ParameterizedTest
    void testMatchesFailure(String input, int skip) {
        Pattern pattern = rosie.compile("[:digit:]+");

        boolean matches = pattern.matches(input, skip);
        assertThat(matches).isFalse();
    }


    @ParameterizedTest
    @ValueSource(strings = {"BOOL", "JSON", "LINE", "COLOR"})
    void testMatchSuccessTransparentDecoders(@ConvertWith(TransparentDecoderConverter.class) Decoder<Match> decoder) {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("123", decoder);

        assertThat(match.isMatched()).isTrue();
        assertThat(match.isAborted()).isFalse();
        assertThat(match.getRemainingBytes()).isEqualTo(0);
    }

    @ParameterizedTest
    @ValueSource(strings = {"BOOL", "JSON", "LINE", "COLOR"})
    void testMatchSuccessWithRemainingTransparentDecoders(@ConvertWith(TransparentDecoderConverter.class) Decoder<Match> decoder) {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("123a", decoder);

        assertThat(match.isMatched()).isTrue();
        assertThat(match.isAborted()).isFalse();
        assertThat(match.getRemainingBytes()).isEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"BOOL", "JSON", "LINE", "COLOR"})
    void testMatchFailedTransparentDecoders(@ConvertWith(TransparentDecoderConverter.class) Decoder<Match> decoder) {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("a123", decoder);

        assertThat(match.isMatched()).isFalse();
        assertThat(match.isAborted()).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"BOOL", "JSON", "LINE", "COLOR"})
    void testMatchSuccessWithSkipTransparentDecoders(@ConvertWith(TransparentDecoderConverter.class) Decoder<Match> decoder) {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("a123", 1, decoder);

        assertThat(match.isMatched()).isTrue();
        assertThat(match.isAborted()).isFalse();
        assertThat(match.getRemainingBytes()).isEqualTo(0);
    }

    @ParameterizedTest
    @ValueSource(strings = {"BOOL", "JSON", "LINE", "COLOR"})
    void testMatchSuccessWithSkipWithRemainingTransparentDecoders(@ConvertWith(TransparentDecoderConverter.class) Decoder<Match> decoder) {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("a123a", 1, decoder);

        assertThat(match.isMatched()).isTrue();
        assertThat(match.isAborted()).isFalse();
        assertThat(match.getRemainingBytes()).isEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"BOOL", "JSON", "LINE", "COLOR"})
    void testMatchFailedWithSkipTransparentDecoders(@ConvertWith(TransparentDecoderConverter.class) Decoder<Match> decoder) {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match("0a123", 1, decoder);

        assertThat(match.isMatched()).isFalse();
        assertThat(match.isAborted()).isFalse();
    }


    @CsvSource({
            "123,   0",
            "123a,  0",
            "a123,  0",
            "a123,  1",
            "a123a, 1",
            "0a123, 1"
    })
    @ParameterizedTest
    void testBoolMatchDataAlwaysNull(String input, int skip) {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match(input, skip, Decoders.BOOL);

        assertThat(match.getData()).isNull();
    }


    @CsvSource({
            "123,   0",
            "123a,  0",
            "a123,  1",
            "a123a, 1"
    })
    @ParameterizedTest
    void testLineMatchSuccessDataSameAsInput(String input, int skip) {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match(input, skip, Decoders.LINE);

        assertThat(match.getData()).isEqualTo(input);
    }

    @CsvSource({
            "a123,  0",
            "0a123, 1"
    })
    @ParameterizedTest
    void testLineMatchFailedDataNull(String input, int skip) {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match(input, skip, Decoders.LINE);

        assertThat(match.getData()).isNull();
    }


    @CsvSource({
            "123,   0, '\u001b[39;1m123\u001b[0m'",
            "123a,  0, '\u001b[39;1m123\u001b[0ma'",
            "a123,  1, 'a\u001b[39;1m123\u001b[0m'",
            "a123a, 1, 'a\u001b[39;1m123\u001b[0ma'"
    })
    @ParameterizedTest
    void testColorMatchSuccess(String input, int skip, String expectedData) {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match(input, skip, Decoders.COLOR);

        assertThat(match.getData()).isEqualTo(expectedData);
    }

    @CsvSource({
            "a123,  0",
            "0a123, 1"
    })
    @ParameterizedTest
    void testColorMatchFailedDataNull(String input, int skip) {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match(input, skip, Decoders.COLOR);

        assertThat(match.getData()).isNull();
    }


    @CsvSource({
            "123,   0",
            "123a,  0",
            "a123,  1",
            "a123a, 1"
    })
    @ParameterizedTest
    void testJsonMatchSuccess(String input, int skip) {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match(input, skip, Decoders.JSON);

        assertThat(match.getData()).isNotNull();

        DocumentContext ctx = JsonPath.parse(match.getData());
        JsonPathAssert.assertThat(ctx).jsonPathAsString("$.data").isEqualTo("123");
    }

    @ParameterizedTest
    @CsvSource({
            "a123, 0",
            "0a123, 1"
    })
    void testJsonMatchFailedDataNull(String input, int skip) {
        Pattern pattern = rosie.compile("[:digit:]+");

        Match match = pattern.match(input, skip, Decoders.JSON);

        assertThat(match.getData()).isNull();
    }


    @Test
    void testJsonMatchSuccessNested() {
        rosie.importPackage("net");
        Pattern pattern = rosie.compile("net.any");

        Match match = pattern.match("1.2.3.4", Decoders.JSON);

        assertThat(match.getData()).isNotNull();

        DocumentContext ctx = JsonPath.parse(match.getData());
        JsonPathAssert.assertThat(ctx).jsonPathAsString("$.data").isEqualTo("1.2.3.4");
        JsonPathAssert.assertThat(ctx).jsonPathAsString("$.type").isEqualTo("net.any");
        JsonPathAssert.assertThat(ctx).jsonPathAsListOf("$.subs", Map.class).hasSize(1);
        JsonPathAssert.assertThat(ctx).jsonPathAsString("$.subs[0].subs[0].data").isEqualTo("1.2.3.4");
        JsonPathAssert.assertThat(ctx).jsonPathAsString("$.subs[0].subs[0].type").isEqualTo("net.ipv4");
    }


    @Test
    void testMatchInvalidEncoderName() {
        Pattern pattern = rosie.compile("[:digit:]+");
        assertThrows(IllegalArgumentException.class,
                () -> pattern.match("123", new Decoder<Match>("this_is_not_a_valid_encoder_name") {
                    @Override
                    public Match decode(Match match) {
                        return null;
                    }
                }));
    }


    static class JacksonConfiguration implements Configuration.Defaults {
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
    }

    static class TransparentDecoderConverter implements ArgumentConverter {
        public TransparentDecoderConverter() {
        }

        @Override
        public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
            try {
                return Decoders.class.getDeclaredField(((String) source)).get(null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new ArgumentConversionException("Illegal decoder", e);
            }
        }
    }
}
