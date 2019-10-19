package com.github.antoniomacri.rosie;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class RosieEngineImportTest {
    private RosieEngine rosie;

    @BeforeEach
    public void init() {
        rosie = new RosieEngine();
    }

    @AfterEach
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


    @Test
    public void testImportFailure() {
        assertThrows(RosieException.class, () -> rosie.importPackage("THISPACKAGEDOESNOTEXIST"));
    }
}
