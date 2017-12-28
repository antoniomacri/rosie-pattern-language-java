package com.github.antoniomacri.rosie;

public class LoadResult {
    public final int ok;
    public final String pkgname;
    public final String errors;

    public LoadResult(int ok, String pkgname, String errors) {
        this.ok = ok;
        this.pkgname = pkgname;
        this.errors = errors;
    }
}
