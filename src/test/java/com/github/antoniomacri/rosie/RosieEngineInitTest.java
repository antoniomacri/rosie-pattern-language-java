package com.github.antoniomacri.rosie;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


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
        assertThat(rosie).isNotNull();

        String libpath = rosie.getLibpath();
        assertThat(libpath).isNotBlank();
    }
}
