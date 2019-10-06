package com.github.antoniomacri.rosie;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;


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

        assertThat("package name", packageName, is(equalTo("net")));
    }

    @Test
    public void testImportWithAlias() {
        String packageName = rosie.importPackage("net", "foobar");

        assertThat("package name", packageName, is(equalTo("net")));  // actual name inside the package
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

        assertThat("match result", match, is(notNullValue()));
        assertThat("matched?", match.matches(), is(true));
        assertThat("matched string", match.match(), is(notNullValue()));
    }

    @Test
    public void testMatchOkJson() {
        rosie.importPackage("net");
        Pattern pattern = rosie.compile("net.any");

        Match match = pattern.match("1.2.3.4", 0, "json");

        assertThat("match result", match, is(notNullValue()));
        assertThat("matched?", match.matches(), is(true));
        assertThat("matched string", match.match(), is("1.2.3.4"));

        assertThat("matched json", match.jsonMatchResult(), is(notNullValue()));
        assertThat("matched type", match.jsonMatchResult().type(), is("net.any"));
        assertThat("subs size", match.jsonMatchResult().subs(), hasSize(1));
        assertThat("subs^2[0] matched string", match.jsonMatchResult().subs().get(0).subs().get(0).match(), is("1.2.3.4"));
        assertThat("subs^2[0] matched type", match.jsonMatchResult().subs().get(0).subs().get(0).type(), is("net.ipv4"));
        assertThat("subs^3", match.jsonMatchResult().subs().get(0).subs().get(0).subs(), is(nullValue()));
    }

    @Test
    public void testMatchKo() {
        rosie.importPackage("net");
        Pattern pattern = rosie.compile("net.any");
        Match match = pattern.match("Hello, world!", "color");

        assertThat("match result", match, is(notNullValue()));
        assertThat("matched?", match.matches(), is(false));
        assertThat("matched string", match.match(), is(nullValue()));
    }

    @Test(expected = RosieException.class)
    public void testImportFailure() {
        rosie.importPackage("THISPACKAGEDOESNOTEXIST");
    }
}
