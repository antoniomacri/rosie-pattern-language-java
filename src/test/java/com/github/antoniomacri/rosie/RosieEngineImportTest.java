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

    @Test
    public void testMatchOk() {
        rosie.importPackage("net");
        Pattern pattern = rosie.compile("net.any");

        Match match = pattern.match("1.2.3.4", 0, "color");

        assertThat(match).isNotNull();
        assertThat(match.matches()).isTrue();
        assertThat(match.match()).isNotNull();
    }

    @Test
    public void testMatchOkJson() {
        rosie.importPackage("net");
        Pattern pattern = rosie.compile("net.any");

        Match match = pattern.match("1.2.3.4", 0, "json");

        assertThat(match).isNotNull();
        assertThat(match.matches()).isTrue();
        assertThat(match.match()).isEqualTo("1.2.3.4");

        assertThat(match.jsonMatchResult()).isNotNull();
        assertThat(match.jsonMatchResult().type()).isEqualTo("net.any");
        assertThat(match.jsonMatchResult().subs()).as("subs size").hasSize(1);
        assertThat(match.jsonMatchResult().subs().get(0).subs().get(0).match()).as("subs^2[0] matched string").isEqualTo("1.2.3.4");
        assertThat(match.jsonMatchResult().subs().get(0).subs().get(0).type()).as("subs^2[0] matched type").isEqualTo("net.ipv4");
        assertThat(match.jsonMatchResult().subs().get(0).subs().get(0).subs()).as("subs^3").isNull();
    }

    @Test
    public void testMatchKo() {
        rosie.importPackage("net");
        Pattern pattern = rosie.compile("net.any");
        Match match = pattern.match("Hello, world!", "color");

        assertThat(match).isNotNull();
        assertThat(match.matches()).isFalse();
        assertThat(match.match()).isNull();
    }

    @Test(expected = RosieException.class)
    public void testImportFailure() {
        rosie.importPackage("THISPACKAGEDOESNOTEXIST");
    }
}
