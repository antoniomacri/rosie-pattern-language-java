package com.github.antoniomacri.rosie;

public class AllocLimitResult {
    private final int limit;
    private final int usage;


    AllocLimitResult(int limit, int usage) {
        this.limit = limit;
        this.usage = usage;
    }


    public int getLimit() {
        return limit;
    }

    public int getUsage() {
        return usage;
    }
}