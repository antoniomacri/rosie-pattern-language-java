package com.github.antoniomacri.rosie;

public class CompilationResult {
    public final Integer pat;
    public final String errors;

    public CompilationResult(Integer pat, String errors) {
        this.pat = pat;
        this.errors = errors;
    }
}
