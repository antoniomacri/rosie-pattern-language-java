package com.github.antoniomacri.rosie.lib;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class RosieLibTests {
    @Test
    void testAllocationByValueEmptyString() {
        String test = "";
        RosieString.ByValue byValue = RosieLib.rosie_new_string(test, test.length());
        assertThat(byValue).isNotNull();
        assertThat(byValue.len.intValue()).isEqualTo(test.length());
        assertThat(byValue.toString()).isEqualTo(test);
    }

    @Test
    void testAllocationByValueNonEmptyString() {
        String test = "ciao";
        RosieString.ByValue byValue = RosieLib.rosie_new_string(test, test.length());
        assertThat(byValue).isNotNull();
        assertThat(byValue.len.intValue()).isEqualTo(test.length());
        assertThat(byValue.toString()).isEqualTo(test);
    }

    @Test
    void testAllocationByReferenceEmptyString() {
        String test = "";
        RosieString byReference = RosieLib.rosie_new_string_ptr(test, test.length());
        assertThat(byReference).isNotNull();
        assertThat(byReference.len.intValue()).isEqualTo(test.length());
        assertThat(byReference.toString()).isEqualTo(test);
    }

    @Test
    void testAllocationByReferenceNonEmptyString() {
        String test = "ciao";
        RosieString byReference = RosieLib.rosie_new_string_ptr(test, test.length());
        assertThat(byReference).isNotNull();
        assertThat(byReference.len.intValue()).isEqualTo(test.length());
        assertThat(byReference.toString()).isEqualTo(test);
    }
}
