package com.github.antoniomacri.rosie;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;


public class RosieEngineLoadfileTest {
    private RosieEngine rosie;

    @Before
    public void init() {
        rosie = new RosieEngine();
    }


    @Test
    public void testLoad() {
        LoadResult result = rosie.loadfile("../rosie-pattern-language-native/submodule/src/librosie/python/test.rpl");
        assertThat(result.ok, is(1));
        assertThat(result.packageName, is(equalTo("test")));
        assertThat(result.errors, is(nullValue()));
    }
}
