package com.github.antoniomacri.rosie;

public class AllocLimitResult {
    private final int limit;
    private final int usage;


    AllocLimitResult(int limit, int usage) {
        this.limit = limit;
        this.usage = usage;
    }


    /**
     * Limit (in kilobytes, 1024 bytes) of the Lua heap size.
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Usage (in kilobytes, 1024 bytes) of the current Lua heap usage.
     */
    public int getUsage() {
        return usage;
    }
}