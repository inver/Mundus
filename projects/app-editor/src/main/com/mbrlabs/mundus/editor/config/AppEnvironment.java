package com.mbrlabs.mundus.editor.config;

import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AppEnvironment {
    private final String homeDir = FileUtils.getUserDirectoryPath();
    private final String configDir = FilenameUtils.concat(getHomeDir(), ".mundus/");
    private final String logsDir = FilenameUtils.concat(getConfigDir(), "logs/");
    private final String tempDir = FilenameUtils.concat(getConfigDir(), "temp/");

    private final String registryFile = FilenameUtils.concat(getConfigDir(), "registry.json");
}
