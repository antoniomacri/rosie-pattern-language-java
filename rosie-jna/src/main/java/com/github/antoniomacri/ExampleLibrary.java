package com.github.antoniomacri;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface ExampleLibrary extends Library {
    ExampleLibrary INSTANCE = (ExampleLibrary) Native.loadLibrary("example", ExampleLibrary.class);

    double sinsquare(double x);
}
