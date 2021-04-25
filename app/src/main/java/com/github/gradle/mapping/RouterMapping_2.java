package com.github.gradle.mapping;

import com.github.gradle.MainActivity;

import java.util.HashMap;
import java.util.Map;

public class RouterMapping_2 {

    public static Map<String, Class<?>> get() {
        Map<String, Class<?>> mapping = new HashMap<>();
        mapping.put("/app/MainActivity", MainActivity.class);

        return mapping;
    }
}
