package com.github.antoniomacri.rosie;

import com.sun.jna.ptr.IntByReference;
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


    @Test
    public void testImport() {
        ImportResult importResult = rosie.import_pkg("net");

        assertThat("import result", importResult.ok, is(not(equalTo(0))));
        assertThat("package name", importResult.packageName, is(equalTo("net")));
        assertThat("errors", importResult.errors, is(nullValue()));
    }

    @Test
    public void testImportWithAlias() {
        ImportResult importResult = rosie.import_pkg("net", "foobar");

        assertThat("import result", importResult.ok, is(not(equalTo(0))));
        assertThat("package name", importResult.packageName, is(equalTo("net")));  // actual name inside the package
        assertThat("errors", importResult.errors, is(nullValue()));
    }

    @Test
    public void testImportAndCompile() {
        testImport();

        RosieCompiled compiled = rosie.compile("net.any");

        assertThat("compiled.pat", compiled.pat, is(notNullValue()));
        assertThat("compiled.pat", compiled.pat.getValue(), greaterThan(0));
        assertThat("errors", compiled.errors, is(nullValue()));
    }

    @Test
    public void testImportAndCompileWithAlias() {
        testImportWithAlias();

        RosieCompiled compiled = rosie.compile("foobar.any");

        assertThat("compiled.pat", compiled.pat, is(notNullValue()));
        assertThat("compiled.pat", compiled.pat.getValue(), greaterThan(0));
        assertThat("errors", compiled.errors, is(nullValue()));
    }

    @Test
    public void testMatchOk() {
        rosie.import_pkg("net");
        RosieCompiled compiled = rosie.compile("net.any");
        IntByReference net_any = compiled.pat;

        MatchResult matchResult = rosie.match(net_any, "1.2.3.4", 1, "color");

        assertThat("match result", matchResult.data, is(notNullValue()));
    }

    @Test
    public void testMatchKo() {
        rosie.import_pkg("net");
        RosieCompiled compiled = rosie.compile("net.any");
        IntByReference net_any = compiled.pat;
        MatchResult matchResult = rosie.match(net_any, "Hello, world!", 1, "color");

        assertThat("match result", matchResult.data, is(nullValue()));
    }

    @Test
    public void testImportFailure() {
        ImportResult importResult = rosie.import_pkg("THISPACKAGEDOESNOTEXIST");

        assertThat("import result", importResult.ok, is(equalTo(0)));
        assertThat("errors", importResult.errors, is(notNullValue()));
    }
}
