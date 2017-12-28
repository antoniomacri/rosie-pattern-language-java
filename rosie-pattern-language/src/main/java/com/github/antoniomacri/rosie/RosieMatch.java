package com.github.antoniomacri.rosie;

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

    public static class ByReference extends RosieMatch implements Structure.ByReference {
    }


    public RosieString data;
    public int leftover;
    public int abend;
    public int ttotal;
    public int tmatch;

    protected List<String> getFieldOrder() {
        return Arrays.asList("data", "leftover", "abend", "ttotal", "tmatch");
    }
}
