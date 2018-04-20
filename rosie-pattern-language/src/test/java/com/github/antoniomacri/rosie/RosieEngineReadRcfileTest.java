package com.github.antoniomacri.rosie;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;


public class RosieEngineReadRcfileTest {
    private static final String TEST_DIR = "../rosie-pattern-language-native/submodule/test";

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
    public void testReadRcFileOK() throws IOException {
        ReadRcFileResult result = rosie.readRcFile(Paths.get(TEST_DIR, "rcfile1").toString());
        assertThat(result.success(), is(true));
        assertThat(result.fileFound, is(true));
        assertThat(result.options, is(notNullValue()));
        assertThat(result.options.size(), greaterThan(0));
        assertThat(result.messages.size(), is(0));
    }

    @Test
    public void testReadRcFileSyntaxError() throws IOException {
        ReadRcFileResult result = rosie.readRcFile(Paths.get(TEST_DIR, "rcfile2").toString());
        assertThat(result.success(), is(false));
        assertThat(result.fileFound, is(true));
        assertThat(result.options, is(nullValue()));
        assertThat(result.messages.get(0), containsString("Syntax errors in rcfile"));
    }

    @Test
    public void testReadRcFileMissing() throws IOException {
        ReadRcFileResult result = rosie.readRcFile("This file does not exist");
        assertThat(result.success(), is(false));
        assertThat(result.fileFound, is(false));
        assertThat(result.options, is(nullValue()));
        assertThat(result.messages.get(0), containsString("Could not open rcfile"));
    }
}
