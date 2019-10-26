package com.github.antoniomacri.rosie;

public class TraceResult {
    private final boolean matched;
    private final String trace;


    TraceResult(boolean matched, String trace) {
        this.matched = matched;
        this.trace = trace;
    }


    /**
     * {@code true} if the input matched.
     */
    public boolean isMatched() {
        return matched;
    }

    /**
     * The trace output as a string.
     */
    public String getTrace() {
        return trace;
    }
}
