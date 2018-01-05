package com.github.antoniomacri.rosie;

public class ImportResult {
    public final int ok;
    public final String packageName;
    public final String errors;

    public ImportResult(int ok, String packageName, String errors) {
        this.ok = ok;
        this.packageName = packageName;
        this.errors = errors;
    }
}
