package com.github.antoniomacri.rosie;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class RosieEngineImportTest {
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
    public void testImport() {
        String packageName = rosie.importPackage("net");

        assertThat(packageName).isEqualTo("net");
    }

    @Test
    public void testImportWithAlias() {
        String packageName = rosie.importPackage("net", "foobar");

        assertThat(packageName).isEqualTo("net");  // actual name inside the package
    }

    @Test
    public void testImportAndCompile() {
        testImport();

        rosie.compile("net.any");
    }

    @Test
    public void testImportAndCompileWithAlias() {
        testImportWithAlias();

        rosie.compile("foobar.any");
    }


    @Test(expected = RosieException.class)
    public void testImportFailure() {
        rosie.importPackage("THISPACKAGEDOESNOTEXIST");
    }
}
