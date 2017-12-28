package com.github.antoniomacri.rosie;

public class MatchResult {
    public final String data;
    public final int leftover;
    public final int abend;
    public final int ttotal;
    public final int tmatch;

    public MatchResult(String data, int leftover, int abend, int ttotal, int tmatch) {
        this.data = data;
        this.leftover = leftover;
        this.abend = abend;
        this.ttotal = ttotal;
        this.tmatch = tmatch;
    }
}
