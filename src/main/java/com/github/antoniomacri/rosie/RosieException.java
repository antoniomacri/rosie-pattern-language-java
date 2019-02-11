package com.github.antoniomacri.rosie;

public class RosieException extends RuntimeException {
    private String errors;

    public RosieException(String message) {
        super(message);
    }

    public RosieException(String message, String errors) {
        super(message);
        this.errors = errors;
    }

    public String getErrors() {
        return errors;
    }
}
