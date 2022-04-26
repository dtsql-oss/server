package com.tsdl.implementation.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PropertyFile {
    private final Map<String, String> properties = new HashMap<>();

    public String get(String key) {
        return properties.get(key);
    }

    public void add(String key, String value) {
        properties.put(key, value);
    }

    public Set<Map.Entry<String, String>> getProperties() {
        return properties.entrySet();
    }
}
