package com.mbrlabs.mundus.editor.config;

import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AppEnvironment {
    private final String homeDir = FilenameUtils.concat(FileUtils.getUserDirectoryPath(), ".mundus/");
    private final String logsDir = FilenameUtils.concat(getHomeDir(), "logs/");
    private final String tempDir = FilenameUtils.concat(getHomeDir(), "temp/");

    private final String homeDataFile = FilenameUtils.concat(getHomeDir(), "mundus.registry");
}
