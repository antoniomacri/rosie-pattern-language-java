package com.github.antoniomacri.rosie;

public class TraceResult {
    private final boolean matched;
    private final String trace;

    public TraceResult(boolean matched, String trace) {
        this.matched = matched;
        this.trace = trace;
    }


    public boolean matched() {
        return matched;
    }

    public String getTrace() {
        return trace;
    }
}
