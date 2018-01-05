package com.github.antoniomacri.rosie;

import com.sun.jna.ptr.IntByReference;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class RosieEngineMatchFileTest {
    private RosieEngine rosie;
    private IntByReference net_any;
    private IntByReference findall_net_any;

    @Before
    public void init() {
        rosie = new RosieEngine();
        rosie.importPackage("net");
        CompilationResult compiledNetAny = rosie.compile("net.any");
        net_any = compiledNetAny.pat;
        CompilationResult compiledNetAllAny = rosie.compile("findall:net.any");
        findall_net_any = compiledNetAllAny.pat;
    }


    @Test
    public void testMatchFile() {
        MatchFileResult result = rosie.matchfile(findall_net_any, "json", "src/test/resources/resolv.conf", "/tmp/resolv.out", "/tmp/resolv.err");

        assertThat("cin", result.cin, is(equalTo(10)));
        assertThat("cout", result.cout, is(equalTo(5)));
        assertThat("cerr", result.cerr, is(equalTo(5)));
    }

    @Test
    public void testMatchFileColor() {
        MatchFileResult result = rosie.matchfile(net_any, "color", "src/test/resources/resolv.conf", null, "/dev/null", true);

        assertThat("cin", result.cin, is(equalTo(1)));
        assertThat("cout", result.cout, is(equalTo(0)));
        assertThat("cerr", result.cerr, is(equalTo(1)));
    }
}
