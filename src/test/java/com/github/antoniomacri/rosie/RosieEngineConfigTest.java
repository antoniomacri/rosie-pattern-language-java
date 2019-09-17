package com.github.antoniomacri.rosie;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;


public class RosieEngineConfigTest {
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
    public void testConfig() {
        Configuration configuration = rosie.config();
        assertThat(configuration, is(notNullValue()));

        assertThat(configuration.getRosieVersion(), is(notNullValue()));
        assertThat(configuration.getRosieHome(), is(notNullValue()));
        assertThat(configuration.getLibdir(), is(notNullValue()));
        assertThat(configuration.getRplVersion(), is(notNullValue()));
        assertThat(configuration.getLibpath(), is(notNullValue()));
    }
}
