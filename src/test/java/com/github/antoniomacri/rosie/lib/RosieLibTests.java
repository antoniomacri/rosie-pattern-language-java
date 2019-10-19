package com.github.antoniomacri.rosie.lib;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class RosieLibTests {
    @Test
    public void testAllocationByValueEmptyString() {
        String test = "";
        RosieString.ByValue byValue = RosieLib.rosie_new_string(test, test.length());
        assertThat(byValue).isNotNull();
        assertThat(byValue.len.intValue()).isEqualTo(test.length());
        assertThat(byValue.toString()).isEqualTo(test);
    }

    @Test
    public void testAllocationByValueNonEmptyString() {
        String test = "ciao";
        RosieString.ByValue byValue = RosieLib.rosie_new_string(test, test.length());
        assertThat(byValue).isNotNull();
        assertThat(byValue.len.intValue()).isEqualTo(test.length());
        assertThat(byValue.toString()).isEqualTo(test);
    }

    @Test
    public void testAllocationByReferenceEmptyString() {
        String test = "";
        RosieString byReference = RosieLib.rosie_new_string_ptr(test, test.length());
        assertThat(byReference).isNotNull();
        assertThat(byReference.len.intValue()).isEqualTo(test.length());
        assertThat(byReference.toString()).isEqualTo(test);
    }

    @Test
    public void testAllocationByReferenceNonEmptyString() {
        String test = "ciao";
        RosieString byReference = RosieLib.rosie_new_string_ptr(test, test.length());
        assertThat(byReference).isNotNull();
        assertThat(byReference.len.intValue()).isEqualTo(test.length());
        assertThat(byReference.toString()).isEqualTo(test);
    }
}
