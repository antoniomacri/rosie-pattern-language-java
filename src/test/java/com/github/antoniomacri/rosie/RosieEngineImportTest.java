package com.github.antoniomacri.rosie;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class RosieEngineImportTest {
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
    void testImport() {
        String packageName = rosie.importPackage("net");

        assertThat(packageName).isEqualTo("net");
    }

    @Test
    void testImportWithAlias() {
        String packageName = rosie.importPackage("net", "foobar");

        assertThat(packageName).isEqualTo("net");  // actual name inside the package
    }

    @Test
    void testImportAndCompile() {
        testImport();

        rosie.compile("net.any");
    }

    @Test
    void testImportAndCompileWithAlias() {
        testImportWithAlias();

        rosie.compile("foobar.any");
    }


    @Test
    void testImportFailure() {
        assertThrows(RosieException.class, () -> rosie.importPackage("THISPACKAGEDOESNOTEXIST"));
    }
}
