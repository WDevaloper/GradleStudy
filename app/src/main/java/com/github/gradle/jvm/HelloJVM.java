package com.github.gradle.jvm;


/**
 * 测试  jvm 运行时内存数据区
 */
public class HelloJVM {
    public int add(int i, int j) {
        int result = 0;
        result = i + j;
        if (result > 10) {

            return result;
        }

        newInstance();

        return result + 10;
    }

    public void newInstance() {
        HelloJVM user = new HelloJVM();
    }

}

