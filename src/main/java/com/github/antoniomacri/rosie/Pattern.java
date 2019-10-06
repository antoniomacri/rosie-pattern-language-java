package com.github.antoniomacri.rosie;

import com.github.antoniomacri.rosie.lib.RosieLib;
import com.github.antoniomacri.rosie.lib.RosieMatch;
import com.github.antoniomacri.rosie.lib.RosieString;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import java.io.Closeable;
import java.util.Objects;


public class Pattern implements Closeable {
    /**
     * Pointer to the rosie engine.
     */
    private Pointer engine;

    /**
     * An integer handle to the rplx object into which the expressions is compiled by Rosie.
     */
    private int pat;


    Pattern(Pointer engine, int pat) {
        Objects.requireNonNull(engine);
        if (pat <= 0) {
            throw new RuntimeException("Invalid pattern");
        }
        this.engine = engine;
        this.pat = pat;
    }


    /**
     * Matches the pattern against an input string using the JSON output encoder.
     *
     * @param input the input string
     */
    public Match match(String input) {
        return match(input, 0, "json");
    }

    /**
     * Matches the pattern against an input string using the JSON output encoder.
     *
     * @param input the input string
     * @param start 0-based index
     */
    public Match match(String input, int start) {
        return match(input, start, "json");
    }

    /**
     * Matches the pattern against an input string using the specified output encoder.
     *
     * @param input   the input string
     * @param encoder the output encoder
     */
    public Match match(String input, String encoder) {
        return match(input, 0, encoder);
    }

    /**
     * Matches the pattern against an input string using the specified output encoder.
     *
     * @param input   the input string
     * @param start   0-based index
     * @param encoder the output encoder
     */
    public Match match(String input, int start, String encoder) {
        try (RosieString Cinput = RosieString.create(input)) {
            RosieMatch Cmatch = new RosieMatch();
            int ok = RosieLib.rosie_match(engine, pat, start + 1, encoder, Cinput, Cmatch);
            if (ok != 0) {
                throw new RuntimeException("match() failed (please report this as a bug)");
            }

            int left = Cmatch.leftover;
            int abend = Cmatch.abend;
            int ttotal = Cmatch.ttotal;
            int tmatch = Cmatch.tmatch;
            if (Cmatch.data.ptr == null) {
                if (Cmatch.data.len.intValue() == MatchStatus.NO_MATCH) {
                    return Match.failed(left, abend, ttotal, tmatch);
                } else if (Cmatch.data.len.intValue() == MatchStatus.MATCH_WITHOUT_DATA) {
                    return Match.noData(left, abend, ttotal, tmatch);
                } else if (Cmatch.data.len.intValue() == MatchStatus.ERR_NO_ENCODER) {
                    throw new IllegalArgumentException("invalid output encoder");
                } else if (Cmatch.data.len.intValue() == MatchStatus.ERR_NO_PATTERN) {
                    throw new IllegalStateException("invalid compiled pattern");
                }
            }
            return Match.withData(encoder, Cmatch.data.toString(), left, abend, ttotal, tmatch);
        }
    }


    /**
     * Matches the pattern against an input string, tracing with the specified style.
     *
     * @param input the input string
     * @param start 0-based index
     * @param style the tracing style
     */
    public TraceResult trace(String input, int start, String style) {
        try (RosieString Cinput = RosieString.create(input); RosieString Ctrace = RosieString.create()) {
            IntByReference Cmatched = new IntByReference();
            int ok = RosieLib.rosie_trace(engine, pat, start, style, Cinput, Cmatched, Ctrace);
            if (ok != 0) {
                throw new RuntimeException("trace() failed (please report this as a bug): " + Ctrace.toString());
            }

            if (Ctrace.ptr == null) {
                if (Ctrace.len.intValue() == MatchStatus.ERR_NO_ENCODER) {
                    throw new IllegalArgumentException("invalid trace style");
                } else if (Ctrace.len.intValue() == MatchStatus.MATCH_WITHOUT_DATA) {
                    throw new IllegalStateException("invalid compiled pattern");
                } else if (Ctrace.len.intValue() != 0) {
                    throw new RuntimeException("unexpected error");
                }
            }
            boolean matched = Cmatched.getValue() != 0;
            String trace = Ctrace.toString();
            return new TraceResult(matched, trace);
        }
    }


    @Override
    public void close() {
        if (pat != 0) {
            RosieLib.rosie_free_rplx(engine, pat);
            engine = Pointer.NULL;
            pat = 0;  // the same integer value may be reused by the engine after freed, therefore set to zero
        }
    }


    interface MatchStatus {
        int NO_MATCH = 0;

        /**
         * The output encoder produced no output data.
         */
        int MATCH_WITHOUT_DATA = 1;

        /**
         * The output encoder or trace style passed to librosie is invalid.
         */
        int ERR_NO_ENCODER = 2;

        /**
         * The pattern handle passed to librosie is invalid.
         */
        int ERR_NO_PATTERN = 4;
    }
}
