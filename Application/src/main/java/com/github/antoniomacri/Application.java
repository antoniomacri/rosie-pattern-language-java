package com.github.antoniomacri;

public class Application {
    public static void main(String[] args) {
        for (String arg : args) {
            double x = Double.valueOf(arg);

            System.out.format("f(%s) = %s (JNI) = %s (JNA)\n", arg, invokeUsingJni(x), invokeUsingJna(x));
        }
    }

    private static double invokeUsingJni(double x) {
        TestObject obj = new TestObject();
        obj.setX(x);
        return obj.getY();
    }

    private static double invokeUsingJna(double x) {
        return ExampleLibrary.INSTANCE.sinsquare(x);
    }
}
