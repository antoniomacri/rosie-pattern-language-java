package com.github.antoniomacri.rosie;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


public class RosieEngineTraceTest {
    private RosieEngine rosie;
    private Pattern netAnyPattern;
    private Pattern netIpPattern;

    @Before
    public void init() {
        rosie = new RosieEngine();
        rosie.importPackage("net");
        netAnyPattern = rosie.compile("net.any");
        netIpPattern = rosie.compile("net.ip");
    }

    @After
    public void close() {
        rosie.close();
    }


    @Test
    public void testTraceNetAny() {
        TraceResult traceResult = netAnyPattern.trace("1.2.3.4", 1, "condensed");
        assertThat(traceResult.matched()).isTrue();
        assertThat(traceResult.getTrace()).isNotNull();
        assertThat(traceResult.getTrace()).hasSizeGreaterThan(0);
    }

    @Test
    public void testTraceNetIp() {
        TraceResult traceResult = netIpPattern.trace("1.2.3", 1, "condensed");
        assertThat(traceResult.matched()).isFalse();
        assertThat(traceResult.getTrace()).isNotNull();
        assertThat(traceResult.getTrace()).hasSizeGreaterThan(0);
    }

    @Test
    public void testTraceNetAnyFull() {
        TraceResult traceResult = netAnyPattern.trace("1.2.3.4", 1, "full");
        assertThat(traceResult.matched()).isTrue();
        assertThat(traceResult.getTrace()).isNotNull();
        assertThat(traceResult.getTrace()).hasSizeGreaterThan(0);
        assertThat(traceResult.getTrace()).contains("Matched 6 chars");
    }

    @Test
    public void testInvalidTraceStyle() {
        Throwable throwable = catchThrowable(() -> netAnyPattern.trace("1.2.3", 1, "no_such_trace_style"));
        assertThat(throwable.getMessage()).contains("invalid trace style");
    }

    @Test
    public void testTraceNetAnyJson() throws IOException {
        TraceResult traceResult = netAnyPattern.trace("1.2.3.4", 1, "json");
        assertThat(traceResult.matched()).isTrue();
        assertThat(traceResult.getTrace()).isNotNull();
        assertThat(traceResult.getTrace()).hasSizeGreaterThan(0);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<?, ?> m = objectMapper.readValue(traceResult.getTrace(), Map.class);

        assertThat(m.get("match")).isNotNull();
        assertThat(m.get("nextpos")).isEqualTo(8);
    }
}
