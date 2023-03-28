package com.mbrlabs.mundus.commons.loader.gltf;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;
import net.mgsx.gltf.data.GLTF;
import net.nevinsky.mundus.core.mesh.Mesh;
import net.nevinsky.mundus.core.node.Animation;

import java.util.ArrayList;
import java.util.List;

public class SceneAsset implements Disposable {
    /**
     * underlying GLTF data structure, null if loaded without "withData" option.
     */
    public GLTF data;

    public List<SceneModel> scenes;
    public SceneModel scene;

    public List<Animation> animations;
    public int maxBones;

    /**
     * Keep track of loaded texture in order to dispose them. Textures handled by AssetManager are excluded.
     */
    public final List<Texture> textures = new ArrayList<>();

    /**
     * Keep track of loaded meshes in order to dispose them.
     */
    public List<Mesh> meshes;

    @Override
    public void dispose() {
        if (scenes != null) {
            for (SceneModel scene : scenes) {
                scene.dispose();
            }
        }
        if (textures != null) {
            for (Texture texture : textures) {
                texture.dispose();
            }
        }
        if (meshes != null) {
            for (Mesh mesh : meshes) {
                mesh.dispose();
            }
        }
    }
}
