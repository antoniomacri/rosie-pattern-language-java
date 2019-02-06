package com.github.antoniomacri.rosie;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class RosieEngineMatchFileTest {
    private RosieEngine rosie;
    private Pattern netAnyPattern;
    private Pattern findallNetAnyPattern;

    @Before
    public void init() {
        rosie = new RosieEngine();
        rosie.importPackage("net");
        netAnyPattern = rosie.compile("net.any");
        findallNetAnyPattern = rosie.compile("findall:net.any");
    }

    @After
    public void close() {
        rosie.close();
    }


    @Test
    public void testMatchFile() {
        MatchFileResult result = rosie.matchfile(findallNetAnyPattern, "json", "src/test/resources/resolv.conf", "/tmp/resolv.out", "/tmp/resolv.err");

        assertThat("cin", result.cin, is(equalTo(10)));
        assertThat("cout", result.cout, is(equalTo(5)));
        assertThat("cerr", result.cerr, is(equalTo(5)));
    }

    @Test
    public void testMatchFileColor() {
        MatchFileResult result = rosie.matchfile(netAnyPattern, "color", "src/test/resources/resolv.conf", null, "/dev/null", true);

        assertThat("cin", result.cin, is(equalTo(1)));
        assertThat("cout", result.cout, is(equalTo(0)));
        assertThat("cerr", result.cerr, is(equalTo(1)));
    }
}
