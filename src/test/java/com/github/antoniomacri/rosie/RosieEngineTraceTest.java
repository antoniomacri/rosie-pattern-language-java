package com.github.antoniomacri.rosie;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


class RosieEngineTraceTest {
    private RosieEngine rosie;
    private Pattern netAnyPattern;
    private Pattern netIpPattern;

    @BeforeEach
    void init() {
        rosie = new RosieEngine();
        rosie.importPackage("net");
        netAnyPattern = rosie.compile("net.any");
        netIpPattern = rosie.compile("net.ip");
    }

    @AfterEach
    void close() {
        rosie.close();
    }


    @Test
    void testTraceNetAny() {
        TraceResult traceResult = netAnyPattern.trace("1.2.3.4", 1, Pattern.TraceStyle.CONDENSED);
        assertThat(traceResult.matched()).isTrue();
        assertThat(traceResult.getTrace()).isNotNull();
        assertThat(traceResult.getTrace()).hasSizeGreaterThan(0);
    }

    @Test
    void testTraceNetIp() {
        TraceResult traceResult = netIpPattern.trace("1.2.3", 1, Pattern.TraceStyle.CONDENSED);
        assertThat(traceResult.matched()).isFalse();
        assertThat(traceResult.getTrace()).isNotNull();
        assertThat(traceResult.getTrace()).hasSizeGreaterThan(0);
    }

    @Test
    void testTraceNetAnyFull() {
        TraceResult traceResult = netAnyPattern.trace("1.2.3.4", 1, Pattern.TraceStyle.FULL);
        assertThat(traceResult.matched()).isTrue();
        assertThat(traceResult.getTrace()).isNotNull();
        assertThat(traceResult.getTrace()).hasSizeGreaterThan(0);
        assertThat(traceResult.getTrace()).contains("Matched 6 chars");
    }

    @Test
    void testInvalidTraceStyle() {
        Throwable throwable = catchThrowable(() -> netAnyPattern.trace("1.2.3", 1, "no_such_trace_style"));
        assertThat(throwable.getMessage()).contains("invalid trace style");
    }

    @Test
    void testTraceNetAnyJson() throws IOException {
        TraceResult traceResult = netAnyPattern.trace("1.2.3.4", 1, Pattern.TraceStyle.JSON);
        assertThat(traceResult.matched()).isTrue();
        assertThat(traceResult.getTrace()).isNotNull();
        assertThat(traceResult.getTrace()).hasSizeGreaterThan(0);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<?, ?> m = objectMapper.readValue(traceResult.getTrace(), Map.class);

        assertThat(m.get("match")).isNotNull();
        assertThat(m.get("nextpos")).isEqualTo(8);
    }
}
