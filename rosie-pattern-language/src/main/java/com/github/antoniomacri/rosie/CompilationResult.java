package com.github.antoniomacri.rosie;

import com.sun.jna.ptr.IntByReference;

public class CompilationResult {
    public final IntByReference pat;
    public final String errors;

    public CompilationResult(IntByReference pat, String errors) {
        this.pat = pat;
        this.errors = errors;
    }
}
