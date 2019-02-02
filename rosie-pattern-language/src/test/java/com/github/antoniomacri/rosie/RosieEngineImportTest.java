package com.github.antoniomacri.rosie;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
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
    public void testMatchOkJson() throws IOException {
        rosie.importPackage("net");
        CompilationResult compiled = rosie.compile("net.any");
        int net_any = compiled.pat;

        MatchResult matchResult = rosie.match(net_any, "1.2.3.4", 1, "json");

        assertThat("match result", matchResult.data, is(notNullValue()));

        ObjectMapper objectMapper = new ObjectMapper();
        Map m = objectMapper.readValue(matchResult.data, Map.class);

        System.out.println("  Json:");
        System.out.println("    " + m);
        printTree(m, 1);

        assertThat("data", m.get("data"), is("1.2.3.4"));
        assertThat("type", m.get("type"), is("net.any"));

        List<?> subs = (List) m.get("subs");
        assertThat("subs size", subs, hasSize(1));
        m = (Map) subs.get(0);
        assertThat("subs[0].data", m.get("data"), is("1.2.3.4"));
        assertThat("subs[0].type", m.get("type"), is("net.ip"));

        subs = (List) m.get("subs");
        assertThat("subs^2 size", subs, hasSize(1));
        m = (Map) subs.get(0);
        assertThat("subs^2[0].data", m.get("data"), is("1.2.3.4"));
        assertThat("subs^2[0].type", m.get("type"), is("net.ipv4"));

        assertThat("subs^3", m.get("subs"), is(nullValue()));
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


    private void printTree(Map m, int indent) {
        System.out.format("%" + indent + "s Tree:\n", " ");
        System.out.format("%" + indent + "s   type: %s\n", " ", m.get("type"));
        System.out.format("%" + indent + "s   data: %s\n", " ", m.get("data"));
        if (m.get("subs") != null) {
            List s = (List) m.get("subs");
            for (Object ss : s) {
                printTree((Map) ss, indent + 2);
            }
        }
    }
}
