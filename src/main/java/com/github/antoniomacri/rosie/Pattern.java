package com.github.antoniomacri.rosie;

public class Pattern {
    private final int pat;

    Pattern(int pat) {
        if (pat <= 0) {
            throw new RuntimeException("Invalid pattern");
        }
        this.pat = pat;
    }

    int getPat() {
        return pat;
    }
}
