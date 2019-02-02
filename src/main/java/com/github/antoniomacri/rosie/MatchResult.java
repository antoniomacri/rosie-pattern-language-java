package com.github.antoniomacri.rosie;

public class MatchResult {
    public final Boolean bool;
    public final String data;
    public final int leftover;
    public final int abend;
    public final int ttotal;
    public final int tmatch;

    public MatchResult(Boolean bool, int leftover, int abend, int ttotal, int tmatch) {
        this.bool = bool;
        this.data = null;
        this.leftover = leftover;
        this.abend = abend;
        this.ttotal = ttotal;
        this.tmatch = tmatch;
    }

    public MatchResult(String data, int leftover, int abend, int ttotal, int tmatch) {
        this.bool = null;
        this.data = data;
        this.leftover = leftover;
        this.abend = abend;
        this.ttotal = ttotal;
        this.tmatch = tmatch;
    }
}
