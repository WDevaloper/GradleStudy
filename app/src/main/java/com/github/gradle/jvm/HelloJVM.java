package com.github.gradle.jvm;


import android.util.Log;

/**
 * 测试  jvm 运行时内存数据区
 */
public class HelloJVM implements IHello {
    int param;
    String param1;

    public int add(int i, int j) {
        int result = 0;
        result = i + j;
        if (result > 10) {

            return result;
        }

        newInstance();
        sayHello();
        return result + 10;
    }

    public void newInstance() {
        HelloJVM helloJVM = new HelloJVM();
    }

    @Override
    public void sayHello() {
        int hello = 0;
        Log.e("TAG", "sayHello: ");
    }
}

