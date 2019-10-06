package com.github.antoniomacri.rosie;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class PatternTest {
    private RosieEngine rosie;

    @Before
    public void init() {
        rosie = new RosieEngine();
    }

    @After
    public void close() {
        rosie.close();
    }


    @Test
    public void testCompilePattern() {
        String expression = "[:digit:]+";
        Pattern pattern = rosie.compile(expression);
        assertThat(String.valueOf(pattern)).isEqualTo(expression);
    }
}
