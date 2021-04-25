package com.github.gradle.mapping.sample;

import com.github.gradle.MainActivity;

import java.util.HashMap;
import java.util.Map;

public class app$$Module$$RouterMapping {

    public static Map<String, Class<?>> get() {
        Map<String, Class<?>> mapping = new HashMap<>();
        mapping.put("/app/MainActivity", MainActivity.class);

        return mapping;
    }
}
