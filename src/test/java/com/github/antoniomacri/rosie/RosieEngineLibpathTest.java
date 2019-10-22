package com.github.antoniomacri.rosie;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class RosieEngineLibpathTest {
    private RosieEngine rosie;

    @BeforeEach
    void init() {
        rosie = new RosieEngine();
    }

    @AfterEach
    void close() {
        rosie.close();
    }


    @Test
    void testGetSetLibpath() {
        String libpath = rosie.getLibpath();
        assertThat(libpath).isNotNull();

        String newpath = "foo bar baz";
        rosie.setLibpath(newpath);
        String testpath = rosie.getLibpath();
        assertThat(testpath).isEqualTo(newpath);
    }

    @Test
    void testGetSetLibpathOverwrite() {
        String libpath = rosie.getLibpath();
        assertThat(libpath).isNotNull();

        String path1 = "foo";
        rosie.setLibpath(path1);
        String testpath = rosie.getLibpath();
        assertThat(testpath).isEqualTo(path1);

        String path2 = "bar";
        rosie.setLibpath(path2);
        testpath = rosie.getLibpath();
        assertThat(testpath).isEqualTo(path2);
    }

    @Test
    void testSetLibpathGetFromConfig() {
        String libpath = rosie.getConfiguration().getLibpath();
        assertThat(libpath).isNotNull();

        String newpath = "foo bar baz";
        rosie.setLibpath(newpath);
        String testpath = rosie.getConfiguration().getLibpath();
        assertThat(testpath).isEqualTo(newpath);
    }
}
