package com.github.antoniomacri.rosie;

public class ParseResult {
    public final String result;
    public final String messages;

    public ParseResult(String result, String messages) {
        this.result = result;
        this.messages = messages;
    }
}
