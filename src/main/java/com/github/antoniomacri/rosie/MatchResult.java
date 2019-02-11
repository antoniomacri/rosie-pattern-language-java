package com.github.antoniomacri.rosie;

import java.util.List;


public interface MatchResult {
    String match();

    int start();

    int end();

    String type();

    List<MatchResult> subs();
}
