package com.github.gradle.jvm;

public class OpStack {
    public int add(int i, int j) {
        int result = 3;
        result = i + j;
        return result + 100;
    }

    public void add2() {
        int a = 8;
        int b = 6;
        add(a, b);
    }
}

