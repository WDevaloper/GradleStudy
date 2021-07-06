package com.github.gradle.jvm;

public class VMStack {
    public int add(int i, int j) {
        long longType = 2;
        double doubleType = 1;
        int result = 3;
        result = i + j;
        return result + 100;
    }


    public static int staticAdd(int i, int j) {
        long longType = 6;
        double doubleType = 8;
        int result = 3;
        result = i + j;
        return result + 100;
    }
}

