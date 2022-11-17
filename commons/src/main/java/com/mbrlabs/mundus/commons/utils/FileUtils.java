package com.mbrlabs.mundus.commons.utils;

import com.badlogic.gdx.files.FileHandle;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

    public static List<String> getResourceFiles(String path) {
        var filenames = new ArrayList<String>();

        try (var in = FileUtils.class.getClassLoader().getResourceAsStream(path);
             var br = new BufferedReader(new InputStreamReader(in))) {
            String resource;

            while ((resource = br.readLine()) != null) {
                filenames.add(resource);
            }
        } catch (Exception e) {
            log.error("ERROR", e);
        }

        return filenames;
    }
}
