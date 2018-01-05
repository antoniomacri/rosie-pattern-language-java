package com.github.antoniomacri.rosie;

public class LoadResult {
    public final int ok;
    public final String packageName;
    public final String errors;

    public LoadResult(int ok, String packageName, String errors) {
        this.ok = ok;
        this.packageName = packageName;
        this.errors = errors;
    }
}
