package com.github.antoniomacri.rosie;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


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
        assertThat("pattern", String.valueOf(pattern), is(equalTo(expression)));
    }
}
