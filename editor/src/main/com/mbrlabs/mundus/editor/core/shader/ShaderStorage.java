package com.mbrlabs.mundus.editor.core.shader;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.mbrlabs.mundus.editor.core.ProjectConstants;
import com.mbrlabs.mundus.editor.core.registry.ProjectRef;
import com.mbrlabs.mundus.editor.shader.MaterialPreviewShader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.mbrlabs.mundus.editor.core.ProjectConstants.SHADER_MATERIAL_PREVIEW_FRAG;
import static com.mbrlabs.mundus.editor.core.ProjectConstants.SHADER_MATERIAL_PREVIEW_VERT;

@Component
@Slf4j
public class ShaderStorage {
    public void addShader(FileHandle handle) {

    }

    public Map<String, BaseShader> load(ProjectRef ref) {
        var path = ref.getPath() + ProjectConstants.PROJECT_SHADERS_DIR;
        log.warn("Loaded only bundled shaders!");
        //todo load from folder
        return loadDefault();
    }

    public Map<String, BaseShader> loadDefault() {
        var res = new HashMap<String, BaseShader>();
        res.put(ProjectConstants.SHADER_MATERIAL_PREVIEW, new MaterialPreviewShader(
                SHADER_MATERIAL_PREVIEW_VERT, SHADER_MATERIAL_PREVIEW_FRAG
        ));
        return res;
    }
}
