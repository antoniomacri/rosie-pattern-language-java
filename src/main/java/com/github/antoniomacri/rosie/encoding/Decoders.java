package com.github.antoniomacri.rosie.encoding;

import com.github.antoniomacri.rosie.Match;


/**
 * Contains the decoders that can be used to read Rosie match results.
 * <p>
 * When a pattern matches its input data, Rosie produces a parse tree internally.
 * The tree represents the relationship between the pattern being matched (at the
 * root) and the sub-matches, which are the named patterns inside the pattern
 * being matched. An output encoder is a function from this internal data structure
 * (the parse tree) to a useful representation.
 */
public class Decoders {
    /**
     * Produces a {@link Match} with a proper value for {@link Match#matches()}.
     * <p>
     * Notice that the matched string is not extracted and {@link Match#getData()}
     * is therefore null. This is faster than {@link #JSON} for when you want to know
     * only if there was a match.
     */
    public static final Decoder<Match> BOOL = new TransparentDecoder("bool");

    /**
     * Produces a {@link Match} with the entire input line, if the pattern matched.
     */
    public static final Decoder<Match> LINE = new TransparentDecoder("line");

    /**
     * Produces a {@link Match} with the entire input string and the matched substrings
     * highlighted using ANSI escape sequences.
     * <p>
     * For human-readable output at the command line, the color encoder renders the
     * input using a customizable map from pattern names to colors.
     */
    public static final Decoder<Match> COLOR = new TransparentDecoder("color");

    /**
     * Produces a {@link Match} with a string encoding the match result as JSON.
     */
    public static final Decoder<Match> JSON = new TransparentDecoder("json");

    /**
     * Produces a boolean {@code true} if the pattern matched.
     */
    public static final Decoder<Boolean> BOOL_VALUE = new Decoder<Boolean>("bool") {
        @Override
        public Boolean decode(Match match) {
            return match.matches();
        }
    };


    public static class TransparentDecoder extends Decoder<Match> {
        TransparentDecoder(String encodingName) {
            super(encodingName);
        }

        @Override
        public Match decode(Match match) {
            return match;
        }
    }
}