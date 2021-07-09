package com.github.gradle.jvm;

public class DynamicLinking {

    public void dynamicLinking() {
        DynamicLinking2 dl = new DynamicLinking2();
        dl.dLinking();
    }


    public void dynamicLinking2() {
        int a = 23;
        dynamicLinking();
    }
}
