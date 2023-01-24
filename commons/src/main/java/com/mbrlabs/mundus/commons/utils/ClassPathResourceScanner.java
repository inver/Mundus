package com.mbrlabs.mundus.commons.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ClassPathResourceScanner {

    @SneakyThrows
    public List<String> getResourceFiles(String root, String suffix) {
        var res = new ArrayList<String>();

        for (var resource : getResourceFolders(root)) {
            var resourcePath = root + "/" + resource;
            var file = new File(getClass().getClassLoader().getResource(resourcePath).toURI());
            if (file.isDirectory()) {
                res.addAll(getResourceFiles(resourcePath, suffix));
            } else {
                if (resourcePath.endsWith(suffix)) {
                    res.add(resourcePath);
                }
            }
        }

        return res;
    }

    private List<String> getResourceFolders(String path) {
        var filenames = new ArrayList<String>();

        try (var in = getResourceAsStream(path);
             var br = new BufferedReader(new InputStreamReader(in))) {
            String resource;

            while ((resource = br.readLine()) != null) {
                filenames.add(resource);
            }
        } catch (Exception e) {
            log.error("ERROR", e);
            throw new RuntimeException(e);
        }
        return filenames;
    }


    private static InputStream getResourceAsStream(String resource) {
        final InputStream in = getContextClassLoader().getResourceAsStream(resource);

        return in == null ? FileUtils.class.getResourceAsStream(resource) : in;
    }

    private static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
