package com.github.antoniomacri.rosie;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.greaterThan;


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
        ImportResult importResult = rosie.importPackage("net");

        assertThat("import result", importResult.ok, is(not(equalTo(0))));
        assertThat("package name", importResult.packageName, is(equalTo("net")));
        assertThat("errors", importResult.errors, is(nullValue()));
    }

    @Test
    public void testImportWithAlias() {
        ImportResult importResult = rosie.importPackage("net", "foobar");

        assertThat("import result", importResult.ok, is(not(equalTo(0))));
        assertThat("package name", importResult.packageName, is(equalTo("net")));  // actual name inside the package
        assertThat("errors", importResult.errors, is(nullValue()));
    }

    @Test
    public void testImportAndCompile() {
        testImport();

        CompilationResult compiled = rosie.compile("net.any");

        assertThat("compiled.pat", compiled.pat, is(notNullValue()));
        assertThat("compiled.pat", compiled.pat, greaterThan(0));
        assertThat("errors", compiled.errors, is(nullValue()));
    }

    @Test
    public void testImportAndCompileWithAlias() {
        testImportWithAlias();

        CompilationResult compiled = rosie.compile("foobar.any");

        assertThat("compiled.pat", compiled.pat, is(notNullValue()));
        assertThat("compiled.pat", compiled.pat, greaterThan(0));
        assertThat("errors", compiled.errors, is(nullValue()));
    }

    @Test
    public void testMatchOk() {
        rosie.importPackage("net");
        CompilationResult compiled = rosie.compile("net.any");
        int net_any = compiled.pat;

        MatchResult matchResult = rosie.match(net_any, "1.2.3.4", 1, "color");

        assertThat("match result", matchResult.data, is(notNullValue()));
    }

    @Test
    public void testMatchKo() {
        rosie.importPackage("net");
        CompilationResult compiled = rosie.compile("net.any");
        int net_any = compiled.pat;
        MatchResult matchResult = rosie.match(net_any, "Hello, world!", 1, "color");

        assertThat("match result", matchResult.data, is(nullValue()));
    }

    @Test
    public void testImportFailure() {
        ImportResult importResult = rosie.importPackage("THISPACKAGEDOESNOTEXIST");

        assertThat("import result", importResult.ok, is(equalTo(0)));
        assertThat("errors", importResult.errors, is(notNullValue()));
    }
}
