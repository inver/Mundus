package com.mbrlabs.mundus.editor.core;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public final class ProjectConstants {
    public static final String HOME_DIR = FilenameUtils.concat(FileUtils.getUserDirectoryPath(), ".mundus/");
    public static final String LOGS_DIR = FilenameUtils.concat(HOME_DIR, "logs/");

    public static final String TEMP_DIR = FilenameUtils.concat(HOME_DIR, "temp/");
    public static final String HOME_DATA_FILE = FilenameUtils.concat(HOME_DIR, "mundus.registry");


    public static final String DEFAULT_SCENE_NAME = "Main Scene";
    public static final String PROJECT_ASSETS_DIR = "assets/";
    public static final String PROJECT_SCENES_DIR = "scenes/";
    public static final String PROJECT_SCENE_EXTENSION = "mundus";
    public static final String PROJECT_EXTENSION = "pro";
    public static final String BUNDLED_FOLDER = "bundled/";
    public static final String PROJECT_SHADERS_DIR = "shaders/";
    public static final String DEFAULT_SHADERS_DIR = BUNDLED_FOLDER + "shaders/";
    public static final String SHADER_MATERIAL_PREVIEW = "materialPreview";
    public static final String SHADER_MATERIAL_PREVIEW_FRAG = DEFAULT_SHADERS_DIR + "material_preview.frag.glsl";
    public static final String SHADER_MATERIAL_PREVIEW_VERT = DEFAULT_SHADERS_DIR + "material_preview.vert.glsl";

}
