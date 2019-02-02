package com.github.antoniomacri.rosie;

public class MatchFileResult {
    public final int cin, cout, cerr;

    public MatchFileResult(int cin, int cout, int cerr) {
        this.cin = cin;
        this.cout = cout;
        this.cerr = cerr;
    }
}