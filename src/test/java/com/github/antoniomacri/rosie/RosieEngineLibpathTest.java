package com.github.antoniomacri.rosie;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;


public class RosieEngineLibpathTest {
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
    public void testGetSetLibpath() {
        String libpath = rosie.getLibpath();
        assertThat(libpath, is(notNullValue()));

        String newpath = "foo bar baz";
        rosie.setLibpath(newpath);
        String testpath = rosie.getLibpath();
        assertThat(testpath, is(equalTo(newpath)));
    }
}
