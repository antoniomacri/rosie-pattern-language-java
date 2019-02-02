package com.github.antoniomacri.rosie;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;


public class RosieEngineLoadfileTest {
    private static final String TEST_DIR = "src/test/resources/tests";

    private RosieEngine rosie;

    @Before
    public void init() {
        rosie = new RosieEngine();
    }


    @Test
    public void testLoad() {
        LoadResult result = rosie.loadfile(Paths.get(TEST_DIR, "test.rpl").toString());
        assertThat(result.ok, is(1));
        assertThat(result.packageName, is(equalTo("test")));
        assertThat(result.errors, is(nullValue()));
    }
}
