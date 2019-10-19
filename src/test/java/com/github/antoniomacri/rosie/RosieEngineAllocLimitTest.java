package com.github.antoniomacri.rosie;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class RosieEngineAllocLimitTest {
    private RosieEngine rosie;

    @BeforeEach
    public void init() {
        rosie = new RosieEngine();
    }

    @AfterEach
    public void close() {
        rosie.close();
    }


    @Test
    public void testNoInitialLimit() {
        AllocLimitResult limits = rosie.getAllocLimit();
        assertThat(limits.getLimit()).isEqualTo(0);
    }

    @Test
    public void testSetAndGetLimit() {
        AllocLimitResult limits;

        rosie.setAllocLimit(0);
        limits = rosie.getAllocLimit();
        assertThat(limits.getLimit()).isEqualTo(0);

        rosie.setAllocLimit(8199);
        limits = rosie.getAllocLimit();
        assertThat(limits.getLimit()).isEqualTo(8199);
    }

    @Test
    public void testSetLimitResult() {
        AllocLimitResult limits;

        limits = rosie.setAllocLimit(0);
        assertThat(limits.getLimit()).isEqualTo(0);

        limits = rosie.setAllocLimit(8199);
        assertThat(limits.getLimit()).isEqualTo(8199);
    }

    @Test
    public void testAllocBelowThreadholdRaisesException() {
        assertThrows(IllegalArgumentException.class, () -> rosie.setAllocLimit(8191));
    }

    @Test
    public void testAllocBelowThreadholdLeavesLimitUnchanged() {
        AllocLimitResult limits;

        limits = rosie.setAllocLimit(8199);
        assertThat(limits.getLimit()).isEqualTo(8199);

        try {
            rosie.setAllocLimit(8191);
        } catch (IllegalArgumentException e) {
            // expected
        }

        limits = rosie.getAllocLimit();
        assertThat(limits.getLimit()).isEqualTo(8199);
    }
}
