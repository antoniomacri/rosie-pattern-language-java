package com.github.antoniomacri.rosie;

import com.github.antoniomacri.rosie.lib.RosieLib;
import com.github.antoniomacri.rosie.lib.RosieMatch;
import com.github.antoniomacri.rosie.lib.RosieString;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import java.util.Objects;


public class Pattern {
    private final Pointer engine;
    private final int pat;


    Pattern(Pointer engine, int pat) {
        Objects.requireNonNull(engine);
        if (pat <= 0) {
            throw new RuntimeException("Invalid pattern");
        }
        this.engine = engine;
        this.pat = pat;
    }


    /**
     * @param start 0-based index
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
                if (Cmatch.data.len.intValue() == 0) {
                    return new Match(false, left, abend, ttotal, tmatch);
                } else if (Cmatch.data.len.intValue() == 1) {
                    return new Match(true, left, abend, ttotal, tmatch);
                } else if (Cmatch.data.len.intValue() == 2) {
                    throw new IllegalArgumentException("invalid output encoder");
                } else if (Cmatch.data.len.intValue() == 4) {
                    throw new IllegalStateException("invalid compiled pattern");
                }
            }
            return new Match(encoder, Cmatch.data.toString(), left, abend, ttotal, tmatch);
        }
    }

    public TraceResult trace(String input, int start, String style) {
        try (RosieString Cinput = RosieString.create(input); RosieString Ctrace = RosieString.create()) {
            IntByReference Cmatched = new IntByReference();
            int ok = RosieLib.rosie_trace(engine, pat, start, style, Cinput, Cmatched, Ctrace);
            if (ok != 0) {
                throw new RuntimeException("trace() failed (please report this as a bug): " + Ctrace.toString());
            }

            if (Ctrace.ptr == null) {
                if (Ctrace.len.intValue() == 2) {
                    throw new IllegalArgumentException("invalid trace style");
                } else if (Ctrace.len.intValue() == 1) {
                    throw new IllegalStateException("invalid compiled pattern");
                }
            }
            boolean matched = Cmatched.getValue() != 0;
            String trace = Ctrace.toString();
            return new TraceResult(matched, trace);
        }
    }
}
