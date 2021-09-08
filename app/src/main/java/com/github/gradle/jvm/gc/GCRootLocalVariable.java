package com.github.gradle.jvm.gc;

import static com.github.gradle.jvm.gc.Util.printMemory;

public class GCRootLocalVariable {
    private byte[] memory = new byte[100 * 1024 * 1024];

    public static void main(String[] args) {
        System.out.println("Start:");
        printMemory();
        method();
        System.gc();
        System.out.println("Second GC finish");
        printMemory();
    }

    public static void method() {
        GCRootLocalVariable g = new GCRootLocalVariable();
        System.gc();
        System.out.println("First GC finish");
        printMemory();
    }
}
