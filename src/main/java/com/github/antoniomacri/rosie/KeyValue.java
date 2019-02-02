package com.github.antoniomacri.rosie;

import com.fasterxml.jackson.annotation.JsonAnySetter;


public class KeyValue<T> {
    private String key;
    private T value;

    @JsonAnySetter
    public void set(String key, T value) {
        this.key = key;
        this.value = value;
    }

    public String key() {
        return key;
    }

    public T value() {
        return value;
    }

    public String toString() {  // just for nice printing
        return key + " = " + value.toString();
    }
}