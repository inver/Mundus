package com.mbrlabs.mundus.editor.core;

import com.mbrlabs.mundus.editor.core.shader.ShaderConstants;

public final class ProjectConstants {


    public static final String DEFAULT_SCENE_NAME = "Main Scene";
    public static final String PROJECT_ASSETS_DIR = "assets/";
    public static final String PROJECT_SCENES_DIR = "scenes/";
    public static final String PROJECT_SHADERS_DIR = "shaders/";
    public static final String PROJECT_SCENE_EXTENSION = "json";
    public static final String PROJECT_EXTENSION = "json";
    public static final String BUNDLED_FOLDER = "bundled";
    public static final String DEFAULT_SHADERS_DIR = BUNDLED_FOLDER + "/shaders";
    public static final String SHADER_DEFAULT_PATH = DEFAULT_SHADERS_DIR + "/" + ShaderConstants.DEFAULT;
    public static final String SHADER_WIREFRAME_PATH = DEFAULT_SHADERS_DIR + "/" + ShaderConstants.WIREFRAME;
    public static final String DEFAULT_SKYBOX_NAME = "skybox_default";
    public static final String DEFAULT_SKYBOX_PATH = BUNDLED_FOLDER + "/textures/" + DEFAULT_SKYBOX_NAME;
    public static final String SHADER_SKYBOX_PATH = DEFAULT_SHADERS_DIR + "/" + ShaderConstants.SKYBOX;
    public static final String SHADER_MATERIAL_PREVIEW = "materialPreview";
    public static final String SHADER_MATERIAL_PREVIEW_FRAG = DEFAULT_SHADERS_DIR + "default.frag.glsl";
    public static final String SHADER_MATERIAL_PREVIEW_VERT = DEFAULT_SHADERS_DIR + "default.vert.glsl";

}
