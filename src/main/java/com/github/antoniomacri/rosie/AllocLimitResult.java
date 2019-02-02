package com.github.antoniomacri.rosie;

public class AllocLimitResult {
    public final int limit;
    public final int usage;

    public AllocLimitResult(int limit, int usage) {
        this.limit = limit;
        this.usage = usage;
    }
}