package net.nevinsky.abyssus.lib.assets.gltf.extension;

import lombok.Getter;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.Map;

public class Extensions {
    @Getter
    private final Map<String, Extension> values = new HashMap<>();

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public <T extends Extension> T get(Class<T> clazz) {
        var field = clazz.getField("NAME");
        String name = (String) field.get(clazz);
        return (T) values.get(name);
    }
}
