package com.github.antoniomacri;

public class Application {
    public static void main(String[] args) {
        TestObject obj = new TestObject();
        for (String arg : args) {
            double x = Double.valueOf(arg);
            obj.setX(x);
            System.out.println("f(" + obj.getX() + ") = " + obj.getY());
        }
    }
}
