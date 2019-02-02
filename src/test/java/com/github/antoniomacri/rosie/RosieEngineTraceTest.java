package com.github.antoniomacri.rosie;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.collection.IsMapContaining;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

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
        TraceResult traceResult = rosie.trace(net_any, "1.2.3.4", 1, "condensed");
        assertThat(traceResult.matched, is(true));
        assertThat(traceResult.trace, is(notNullValue()));
        assertThat(traceResult.trace.length(), is(greaterThan(0)));
    }

    @Test
    public void testTraceNetIp() {
        TraceResult traceResult = rosie.trace(net_ip, "1.2.3", 1, "condensed");
        assertThat(traceResult.matched, is(false));
        assertThat(traceResult.trace, is(notNullValue()));
        assertThat(traceResult.trace.length(), is(greaterThan(0)));
    }

    @Test
    public void testTraceNetAnyFull() {
        TraceResult traceResult = rosie.trace(net_any, "1.2.3.4", 1, "full");
        assertThat(traceResult.matched, is(true));
        assertThat(traceResult.trace, is(notNullValue()));
        assertThat(traceResult.trace.length(), is(greaterThan(0)));
        assertThat(traceResult.trace, containsString("Matched 6 chars"));
    }

    @Test
    public void testInvalidTraceStyle() {
        try {
            rosie.trace(net_any, "1.2.3", 1, "no_such_trace_style");
            assertThat("Do not reach here", false);
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), containsString("invalid trace style"));
        }
    }

    @Test
    public void testTraceNetAnyJson() throws IOException {
        TraceResult traceResult = rosie.trace(net_any, "1.2.3.4", 1, "json");
        assertThat(traceResult.matched, is(true));
        assertThat(traceResult.trace, is(notNullValue()));
        assertThat(traceResult.trace.length(), is(greaterThan(0)));

        ObjectMapper objectMapper = new ObjectMapper();
        Map<?, ?> m = objectMapper.readValue(traceResult.trace, Map.class);

        assertThat(m, IsMapContaining.hasEntry(is("match"), is(notNullValue())));
        assertThat(m, IsMapContaining.hasEntry(is("nextpos"), is(8)));
    }
}
