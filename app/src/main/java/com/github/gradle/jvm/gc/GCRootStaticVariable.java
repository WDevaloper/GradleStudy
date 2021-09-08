package com.github.gradle.jvm.gc;


import static com.github.gradle.jvm.gc.Util.printMemory;

public class GCRootStaticVariable {
    private static int _10MB = 10 * 1024 * 1024;
    private byte[] memory;
    private static GCRootStaticVariable staticVariable;
    GCRootStaticVariable memberVariable;

    public GCRootStaticVariable(int size) {
        memory = new byte[size];
    }

    public static void main(String[] args) {
        System.out.println("Start:");
        printMemory();
        GCRootStaticVariable g = new GCRootStaticVariable(4 * _10MB);
        g.memberVariable = new GCRootStaticVariable(2 * _10MB);
        GCRootStaticVariable.staticVariable = new GCRootStaticVariable(8 * _10MB);
        // 将g置为null, 调用GC时可以回收此对象内存
        g = null;
        System.out.println("Start GC before");
        printMemory();
        System.gc();
        System.out.println("GC Finish after");
        printMemory();
    }
}
