package com.mbrlabs.mundus.editor.core.shader;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.mbrlabs.mundus.commons.assets.shader.ShaderAsset;
import com.mbrlabs.mundus.commons.shaders.DefaultBaseShader;
import com.mbrlabs.mundus.commons.shaders.SkyboxShader;
import com.mbrlabs.mundus.editor.core.ProjectConstants;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.registry.ProjectRef;
import com.mbrlabs.mundus.editor.shader.DefaultAppShader;
import com.mbrlabs.mundus.editor.shader.MaterialPreviewShader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.mbrlabs.mundus.editor.core.ProjectConstants.SHADER_MATERIAL_PREVIEW_FRAG;
import static com.mbrlabs.mundus.editor.core.ProjectConstants.SHADER_MATERIAL_PREVIEW_VERT;

@Component
@Slf4j
@RequiredArgsConstructor
public class ShaderStorage {

    private final EditorCtx ctx;

    //todo move to shader context
    private final Map<String, DefaultBaseShader> shaders = new HashMap<>();

    //    public ShaderStorage() {
//        ShaderProgram.pedantic = false;
//
//        shaders.put(ShaderConstants.WIREFRAME, new WireframeShader());
//        shaders.put(ShaderConstants.TERRAIN, new TerrainShader());
//        shaders.put(ShaderConstants.MODEL, new ModelShader());
//        shaders.put(ShaderConstants.SKYBOX, new SkyboxShader());
//        shaders.put(ShaderConstants.PICKER, new PickerShader());
//
//        shaders.values().forEach(Shader::init);
//    }
//
    public void addShader(FileHandle handle) {
//        shaders.put(ShaderConstants.WIREFRAME, new WireframeShader());
//        shaders.put(ShaderConstants.TERRAIN, new TerrainShader());
//        shaders.put(ShaderConstants.MODEL, new ModelShader());
//        shaders.put(ShaderConstants.SKYBOX, new SkyboxShader());
//        shaders.put(ShaderConstants.PICKER, new PickerShader());
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

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        var res = shaders.get(key);
        if (res != null) {
            return (T) res;
        }

        res = loadShader(key);
        if (res != null) {
            res.init();
            shaders.put(key, res);
        }
        return (T) res;
    }

    private DefaultBaseShader loadShader(String key) {
        switch (key) {
            case ShaderConstants.DEFAULT: {
                //todo remove path hardcode
                var asset = (ShaderAsset) ctx.getAssetLibrary().get(ProjectConstants.SHADER_DEFAULT_PATH);
                return new DefaultAppShader(asset.getVertexShader(), asset.getFragmentShader());
            }
            case ShaderConstants.SKYBOX: {
                var asset = (ShaderAsset) ctx.getAssetLibrary().get(ProjectConstants.SHADER_SKYBOX_PATH);
                return new SkyboxShader(asset.getVertexShader(), asset.getFragmentShader());
            }
        }

        return null;
    }
}
