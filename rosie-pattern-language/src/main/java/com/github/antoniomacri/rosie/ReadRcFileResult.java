package com.github.antoniomacri.rosie;

import java.util.List;

public class ReadRcFileResult {
    public final boolean fileFound;
    public final List<KeyValue> options;
    public final List<String> messages;

    public ReadRcFileResult(boolean fileFound, List<KeyValue> options, List<String> messages) {
        this.fileFound = fileFound;
        this.options = options;
        this.messages = messages;
    }

    public boolean success() {
        return options != null && !options.isEmpty();
    }
}