package com.github.antoniomacri.rosie.lib;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;


/**
 * Original definitions:
 * <pre>
 * // rpeg.h
 * typedef uint8_t * byte_ptr;
 * typedef struct rosie_string {
 *     uint32_t len;
 *     byte_ptr ptr;
 * } rstr;
 * </pre>
 * <pre>
 * // librosie.h
 * typedef struct rosie_string str;
 * </pre>
 */
public class RosieString extends Structure {
    public static class ByValue extends RosieString implements Structure.ByValue {
    }


    public UnsignedInt len;
    public Pointer ptr;

    protected List<String> getFieldOrder() {
        return Arrays.asList("len", "ptr");
    }

    @Override
    public String toString() {
        return new String(ptr.getByteArray(0, len.intValue()));
    }
}