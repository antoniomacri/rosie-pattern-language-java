package com.github.antoniomacri.rosie;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class RosieEngineAllocLimitTest {
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

    @Test(expected = IllegalArgumentException.class)
    public void testAllocBelowThreadholdRaisesException() {
        rosie.setAllocLimit(8191);  // too low
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
