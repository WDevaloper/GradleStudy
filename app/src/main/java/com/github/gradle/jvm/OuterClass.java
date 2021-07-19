package com.github.gradle.jvm;


//编译器会在字节码中内部类的构造方法添加外部类的参数
public class OuterClass {

    private int outterA = 0;

    public static void main(String[] args) {

    }

    public int getOutterA() {
        return outterA;
    }

    class Inner {
        private int a = 0;

        public int getA() {
            return outterA;
        }
    }
}

