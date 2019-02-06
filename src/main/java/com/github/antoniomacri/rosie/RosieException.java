package com.github.antoniomacri.rosie;

public class RosieException extends RuntimeException {
    private String errors;

    public RosieException(String errors) {
        this.errors = errors;
    }

    public String getErrors() {
        return errors;
    }
}
