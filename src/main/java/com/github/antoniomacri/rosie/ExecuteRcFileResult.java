package com.github.antoniomacri.rosie;

import java.util.List;

public class ExecuteRcFileResult {
    public final boolean success;
    public final boolean fileFound;
    public final List<String> messages;

    public ExecuteRcFileResult(boolean success, boolean fileFound, List<String> messages) {
        this.success = success;
        this.fileFound = fileFound;
        this.messages = messages;
    }
}