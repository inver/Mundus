package com.mbrlabs.mundus.editor.core.shader;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mbrlabs.mundus.commons.shaders.ModelShader;
import com.mbrlabs.mundus.commons.shaders.SkyboxShader;
import com.mbrlabs.mundus.commons.shaders.TerrainShader;
import com.mbrlabs.mundus.editor.core.ProjectConstants;
import com.mbrlabs.mundus.editor.core.registry.ProjectRef;
import com.mbrlabs.mundus.editor.shader.MaterialPreviewShader;
import com.mbrlabs.mundus.editor.shader.WireframeShader;
import com.mbrlabs.mundus.editor.tools.picker.PickerShader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.mbrlabs.mundus.editor.core.ProjectConstants.SHADER_MATERIAL_PREVIEW_FRAG;
import static com.mbrlabs.mundus.editor.core.ProjectConstants.SHADER_MATERIAL_PREVIEW_VERT;

@Component
@Slf4j
public class ShaderStorage {

    //todo move to shader context
    private final Map<String, BaseShader> shaders = new HashMap<>();

    public ShaderStorage() {
        ShaderProgram.pedantic = false;

        shaders.put(ShaderConstants.WIREFRAME, new WireframeShader());
        shaders.put(ShaderConstants.TERRAIN, new TerrainShader());
        shaders.put(ShaderConstants.MODEL, new ModelShader());
        shaders.put(ShaderConstants.SKYBOX, new SkyboxShader());
        shaders.put(ShaderConstants.PICKER, new PickerShader());

        shaders.values().forEach(Shader::init);
    }

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

    public BaseShader get(String key) {
        return shaders.get(key);
    }
}
