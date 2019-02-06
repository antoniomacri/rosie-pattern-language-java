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

        MatchResult matchResult = rosie.match(pattern, "1.2.3.4", 1, "color");

        assertThat("match result", matchResult.data, is(notNullValue()));
    }

    @Test
    public void testMatchOkJson() throws IOException {
        rosie.importPackage("net");
        Pattern pattern = rosie.compile("net.any");

        MatchResult matchResult = rosie.match(pattern, "1.2.3.4", 1, "json");

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
        Pattern pattern = rosie.compile("net.any");
        MatchResult matchResult = rosie.match(pattern, "Hello, world!", 1, "color");

        assertThat("match result", matchResult.data, is(nullValue()));
    }

    @Test(expected = RosieException.class)
    public void testImportFailure() {
        rosie.importPackage("THISPACKAGEDOESNOTEXIST");
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
