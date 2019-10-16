package com.github.antoniomacri.rosie.encoding;

import com.github.antoniomacri.rosie.Match;
import lombok.Getter;


@Getter
public abstract class Decoder<T> {
    private final String encodingName;

    protected Decoder(String encodingName) {
        this.encodingName = encodingName;
    }

    public abstract T decode(Match match);
}