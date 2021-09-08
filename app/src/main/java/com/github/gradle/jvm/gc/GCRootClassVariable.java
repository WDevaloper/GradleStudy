package com.github.gradle.jvm.gc;


import static com.github.gradle.jvm.gc.Util.printMemory;

public class GCRootClassVariable {
    private static int _10MB = 10 * 1024 * 1024;
    private byte[] memory;
    private GCRootClassVariable classVariable;

    public GCRootClassVariable(int size) {
        memory = new byte[size];
    }

    public static void main(String[] args) {
        System.out.println("Start:");

        printMemory();
        GCRootClassVariable g = new GCRootClassVariable(4 * _10MB);
        g.classVariable = new GCRootClassVariable(8 * _10MB);
        g = null;
        System.gc();
        System.out.println("GC Finish");
        printMemory();
    }
}
