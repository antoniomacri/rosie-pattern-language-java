package com.github.antoniomacri.rosie;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.greaterThan;


public class RosieEngineTraceTest {
    private RosieEngine rosie;
    private int net_any;
    private int net_ip;

    @Before
    public void init() {
        rosie = new RosieEngine();
        rosie.importPackage("net");
        CompilationResult compiledNetAny = rosie.compile("net.any");
        net_any = compiledNetAny.pat;
        CompilationResult compiledNetIp = rosie.compile("net.ip");
        net_ip = compiledNetIp.pat;
    }

    @After
    public void close() {
        rosie.close();
    }


    @Test
    public void testTraceNetAny() {
        TraceResult traceResult = rosie.trace(net_any, "1.2.3", 1, "condensed");
        assertThat(traceResult.matched, is(equalTo(true)));
        assertThat(traceResult.trace, is(notNullValue()));
        assertThat(traceResult.trace.length(), is(greaterThan(0)));
    }

    @Test
    public void testTraceNetIp() {
        TraceResult traceResult = rosie.trace(net_ip, "1.2.3", 1, "condensed");
        assertThat(traceResult.matched, is(equalTo(false)));
        assertThat(traceResult.trace, is(notNullValue()));
        assertThat(traceResult.trace.length(), is(greaterThan(0)));
    }
}
