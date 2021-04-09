package com.github.gradle.mapping;

import java.util.HashMap;
import java.util.Map;

public class RouterMappingTest {

    public static Map<String, String> get() {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("/app/MainActivity", "com.xxxx.MainActivity");

        return mapping;
    }
}
