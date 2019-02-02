package com.github.antoniomacri.rosie;

public class TraceResult {
    public final boolean matched;
    public final String trace;

    public TraceResult(boolean matched, String trace) {
        this.matched = matched;
        this.trace = trace;
    }
}
