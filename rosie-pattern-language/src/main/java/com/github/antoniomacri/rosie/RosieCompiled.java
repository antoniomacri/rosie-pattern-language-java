package com.github.antoniomacri.rosie;

import com.sun.jna.ptr.IntByReference;

public class RosieCompiled {
    public final IntByReference pat;
    public final String errors;

    public RosieCompiled(IntByReference pat, String errors) {
        this.pat = pat;
        this.errors = errors;
    }
}
