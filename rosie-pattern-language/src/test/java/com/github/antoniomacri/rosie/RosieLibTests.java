package com.github.antoniomacri.rosie;

import com.sun.jna.Native;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;


public class RosieLibTests {
    private RosieLib LIB;


    @Before
    public void load() {
        LIB = Native.loadLibrary("rosie", RosieLib.class);
    }


    @Test
    public void testAllocationByValueEmptyString() {
        String test = "";
        RosieString.ByValue byValue = LIB.rosie_new_string(test, test.length());
        assertThat(byValue, is(notNullValue()));
        assertThat(byValue.len.intValue(), is(equalTo(test.length())));
        assertThat(byValue.toString(), is(equalTo(test)));
    }

    @Test
    public void testAllocationByValueNonEmptyString() {
        String test = "ciao";
        RosieString.ByValue byValue = LIB.rosie_new_string(test, test.length());
        assertThat(byValue, is(notNullValue()));
        assertThat(byValue.len.intValue(), is(equalTo(test.length())));
        assertThat(byValue.toString(), is(equalTo(test)));
    }

    @Test
    public void testAllocationByReferenceEmptyString() {
        String test = "";
        RosieString.ByReference byReference = LIB.rosie_new_string_ptr(test, test.length());
        assertThat(byReference, is(notNullValue()));
        assertThat(byReference.len.intValue(), is(equalTo(test.length())));
        assertThat(byReference.toString(), is(equalTo(test)));
    }

    @Test
    public void testAllocationByReferenceNonEmptyString() {
        String test = "ciao";
        RosieString.ByReference byReference = LIB.rosie_new_string_ptr(test, test.length());
        assertThat(byReference, is(notNullValue()));
        assertThat(byReference.len.intValue(), is(equalTo(test.length())));
        assertThat(byReference.toString(), is(equalTo(test)));
    }
}
