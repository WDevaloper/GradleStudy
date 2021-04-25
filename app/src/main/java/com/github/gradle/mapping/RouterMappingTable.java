package com.github.gradle.mapping;

import java.util.HashMap;
import java.util.Map;

public class RouterMappingTable {
    public static Map<String, Class<?>> get() {
        Map<String, Class<?>> mapping = new HashMap<>();

        mapping.putAll(RouterMapping_1.get());
        mapping.putAll(RouterMapping_2.get());

        return mapping;
    }
}
