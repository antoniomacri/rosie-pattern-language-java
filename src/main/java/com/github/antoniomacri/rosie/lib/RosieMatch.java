package com.github.antoniomacri.rosie.lib;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;


/**
 * Original definition:
 * <pre>
 * // librosie.h
 * typedef struct rosie_matchresult {
 *     str data;
 *     int leftover;
 *     int abend;
 *     int ttotal;
 *     int tmatch;
 * } match;
 * </pre>
 */
public class RosieMatch extends Structure {
    public static class ByValue extends RosieMatch implements Structure.ByValue {
    }


    public UnsignedInt dataLen;

    /**
     * Not a RosieString because it must not be free'd!
     * See {@link RosieLib#rosie_match(Pointer, int, int, String, RosieString, RosieMatch)}.
     */
    public Pointer dataPtr;

    public int leftover;
    public int abend;
    public int ttotal;
    public int tmatch;

    protected List<String> getFieldOrder() {
        return Arrays.asList("dataLen", "dataPtr", "leftover", "abend", "ttotal", "tmatch");
    }


    @Override
    public String toString() {
        return dataPtr == null ? null : new String(dataPtr.getByteArray(0, dataLen.intValue()));
    }
}
