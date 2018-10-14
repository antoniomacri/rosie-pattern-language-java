package com.github.antoniomacri.rosie;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;


public class RosieEngineInitTest {
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
    public void testInit() {
        assertThat("rosie engine", rosie, is(notNullValue()));

        String libpath = rosie.getLibpath();
        assertThat("libpath", libpath, is(notNullValue()));
        assertThat("libpath", libpath, is(not(emptyString())));

        System.out.println("Lib path: " + libpath);
    }
}
