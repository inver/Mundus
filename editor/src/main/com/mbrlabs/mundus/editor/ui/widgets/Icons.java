package com.mbrlabs.mundus.editor.ui.widgets;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Icons {
    HIERARCHY("tree.png"),
    SHADER("shader.png"),
    TERRAIN("terrain.png"),
    MATERIAL("material.png"),
    TEXTURE("texture.png"),
    MODEL("model.png"),
    CAMERA("camera.png"),
    LIGHT("light.png"),
    SCENE("scene.png"),
    SKYBOX("scene.png");

    private final String fileName;

    public String getPath() {
        return "ui/icons/" + fileName;
    }
}
