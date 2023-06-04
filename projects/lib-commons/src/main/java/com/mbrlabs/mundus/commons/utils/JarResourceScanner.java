package com.mbrlabs.mundus.commons.utils;

import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class JarResourceScanner {
    public List<String> getResourceFiles(URL baseURL, String path, String suffix) {
        var res = new ArrayList<String>();

        try (var zip = new ZipInputStream(baseURL.openStream())) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                var name = entry.getName();
                if (name.startsWith(path) && name.endsWith(suffix)) {
                    res.add(name);
                }
            }
        } catch (Exception e) {
            log.error("ERROR", e);
            throw new RuntimeException(e);
        }
        return res;
    }
}
