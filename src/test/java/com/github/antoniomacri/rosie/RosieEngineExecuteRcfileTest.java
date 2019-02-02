package com.github.antoniomacri.rosie;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;


public class RosieEngineExecuteRcfileTest {
    private static final String TEST_DIR = "src/test/resources/tests";

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
    public void testExecuteRcFileMissing() throws IOException {
        ExecuteRcFileResult result = rosie.executeRcFile("This file does not exist");
        assertThat(result.success, is(false));
        assertThat(result.fileFound, is(false));
    }

    @Test
    public void testExecuteRcFileLoadError() throws IOException {
        ExecuteRcFileResult result = rosie.executeRcFile(Paths.get(TEST_DIR, "rcfile1").toString());
        assertThat(result.success, is(false));
        assertThat(result.fileFound, is(true));
        assertThat(result.messages, is(notNullValue()));
        assertThat(result.messages.get(0), containsString("Failed to load another-file"));
    }

    @Test
    public void testExecuteRcFileSyntaxError() throws IOException {
        ExecuteRcFileResult result = rosie.executeRcFile(Paths.get(TEST_DIR, "rcfile2").toString());
        assertThat(result.success, is(false));
        assertThat(result.fileFound, is(true));
        assertThat(result.messages, is(notNullValue()));
        assertThat(result.messages.get(0), containsString("Syntax errors in rcfile"));
    }

    @Test
    public void testExecuteRcFileLoadError2() throws IOException {
        ExecuteRcFileResult result = rosie.executeRcFile(Paths.get(TEST_DIR, "rcfile3").toString());
        assertThat(result.success, is(false));
        assertThat(result.fileFound, is(true));
        assertThat(result.messages, is(notNullValue()));
        assertThat(result.messages.get(0), containsString("Failed to load nofile_mod1.rpl"));
    }

    /*
    // Fails since the current path is not src/librosie/python as required
    @Test
    public void testExecuteRcFileOK() throws IOException {
        ExecuteRcFileResult result = rosie.executeRcFile(Paths.get(TEST_DIR, "rcfile5").toString());
        assertThat(result.success, is(true));
        assertThat(result.fileFound, is(true));
        assertThat(result.messages, is(notNullValue()));
        assertThat(result.messages.size(), is(0));
    }
    */
}
