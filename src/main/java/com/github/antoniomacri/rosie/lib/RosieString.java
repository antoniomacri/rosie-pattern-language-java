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
public class RosieString extends Structure implements AutoCloseable {
    public static class ByValue extends RosieString implements Structure.ByValue {
    }


    public UnsignedInt len;
    public Pointer ptr;

    protected List<String> getFieldOrder() {
        return Arrays.asList("len", "ptr");
    }


    public static RosieString create() {
        RosieString str = new RosieString.ByValue();
        return str;
    }

    public static RosieString create(String expression) {
        RosieString str = RosieLib.rosie_new_string(expression, expression.length());
        return str;
    }


    /**
     * Properly dispose of native memory when this object is closed.
     */
    @Override
    public void close() {
        if (ptr != null) {
            RosieLib.rosie_free_string((RosieString.ByValue) this);
            ptr = null;
        }
    }


    @Override
    public String toString() {
        return ptr == null ? null : new String(ptr.getByteArray(0, len.intValue()));
    }
}