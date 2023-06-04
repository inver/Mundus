package com.mbrlabs.mundus.commons.utils;

import com.badlogic.gdx.files.FileHandle;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;
import java.util.List;

@Slf4j
public final class FileUtils {

    public static <T> T readFullFromFileSystem(ObjectMapper mapper, FileHandle handle, TypeReference<T> tr) {
        try (var fr = new FileReader(handle.file())) {
            return mapper.readValue(fr, tr);
        } catch (Exception e) {
            throw new RuntimeException("Fail to load file: " + handle, e);
        }
    }

    public static <T> T readFromFileSystem(ObjectMapper mapper, FileHandle handle, Class<T> clazz) {
        try (var fr = new FileReader(handle.file())) {
            return mapper.readValue(fr, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Fail to load file: " + handle, e);
        }
    }

    public static <T> T readFullFromClassPath(ObjectMapper mapper, FileHandle handle, TypeReference<T> tr) {
        try (var is = FileUtils.class.getClassLoader().getResourceAsStream(handle.path())) {
            return mapper.readValue(is, tr);
        } catch (Exception e) {
            throw new RuntimeException("Fail to load file: " + handle, e);
        }
    }

    public static <T> T readFromClassPath(ObjectMapper mapper, FileHandle handle, Class<T> clazz) {
        try (var is = clazz.getClassLoader().getResourceAsStream(handle.path())) {
            return mapper.readValue(is, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Fail to load file: " + handle, e);
        }
    }

    public static List<String> getResourceFiles(Class rootClass, String path, String suffix) {
        var codeSource = rootClass.getProtectionDomain().getCodeSource();
        var location = codeSource.getLocation();
        if (location.getPath().endsWith(".jar")) {
            var scanner = new JarResourceScanner();
            return scanner.getResourceFiles(location, path, suffix);
        }

        var scanner = new ClassPathResourceScanner();
        return scanner.getResourceFiles(path, suffix);
    }
}
