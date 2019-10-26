package com.github.antoniomacri.rosie;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;


/**
 * An immutable object describing a match result.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Match {
    private final Boolean bool;
    private final String data;
    private final int leftover;
    private final int abend;
    private final int ttotal;
    private final int tmatch;


    static Match failed(int leftover, int abend, int ttotal, int tmatch) {
        return builder()
                .bool(false)
                .leftover(leftover).abend(abend).ttotal(ttotal).tmatch(tmatch)
                .build();
    }

    static Match noData(int leftover, int abend, int ttotal, int tmatch) {
        return builder()
                .bool(true)
                .leftover(leftover).abend(abend).ttotal(ttotal).tmatch(tmatch)
                .build();
    }

    static Match withData(String data, int leftover, int abend, int ttotal, int tmatch) {
        return builder()
                .data(data)
                .leftover(leftover).abend(abend).ttotal(ttotal).tmatch(tmatch)
                .build();
    }


    /**
     * Returns {@code true} if the pattern matched.
     */
    public boolean isMatched() {
        return Boolean.TRUE.equals(bool) || data != null;
    }

    /**
     * Encoder-specific data of the match result.
     */
    public String getData() {
        return data;
    }

    /**
     * Indicates whether the match ended abnormally by encountering an RPL {@code error} macro.
     */
    public boolean isAborted() {
        return abend != 0;
    }

    /**
     * When the match succeeded, indicates the number of bytes left unmatched.
     */
    public int getRemainingBytes() {
        return leftover;
    }

    /**
     * The number of microseconds spent in the call.
     * <p>
     * Notice this is subject to the platform's clock resolution.
     */
    public int getTotalMillis() {
        return ttotal;
    }

    /**
     * The number of microseconds spent actually doing the matching.
     * <p>
     * The value returned by {@link #getTotalMillis()} includes also time spent encoding the results to produce data.
     */
    public int getMatchMillis() {
        return tmatch;
    }
}
