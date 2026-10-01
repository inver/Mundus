package net.nevinsky.abyssus.lib.assets.assimp;

import com.badlogic.gdx.files.FileHandle;
import lombok.extern.slf4j.Slf4j;
import net.nevinsky.abyssus.core.model.ModelData;
import net.nevinsky.abyssus.core.model.ModelMesh;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;

import java.util.HashMap;
import java.util.Map;

/**
 * Loads a model file through Assimp into a {@link ModelData}. No OpenGL context is needed.
 */
@Slf4j
public class AssimpModelDataLoader {

    private final boolean convertUnits;

    public AssimpModelDataLoader() {
        this(false);
    }

    /**
     * @param convertUnits scale models to meters if the file reports its unit (FBX). Off by default: the units of FBX
     *                     files are used inconsistently, so a conversion can make models a hundred times smaller.
     *                     The up axis is always converted to Y.
     */
    public AssimpModelDataLoader(boolean convertUnits) {
        this.convertUnits = convertUnits;
    }

    public ModelData load(String modelId, FileHandle file) {
        return load(modelId, file, AssimpFlags.DEFAULT);
    }

    public ModelData load(String modelId, FileHandle file, int flags) {
        return load(modelId, file, flags, null);
    }

    /**
     * @param embeddedTextureDir where embedded textures are written; embedded textures are skipped if {@code null}
     */
    public ModelData load(String modelId, FileHandle file, int flags, FileHandle embeddedTextureDir) {
        var start = System.currentTimeMillis();
        try (var imported = AssimpImporter.importScene(file.path(), flags)) {
            var data = convert(modelId, imported.scene(), parentDir(file), embeddedTextureDir, convertUnits);
            log.debug("Model {} loaded in {} ms", modelId, System.currentTimeMillis() - start);
            return data;
        }
    }

    static ModelData convert(String modelId, AIScene scene, String modelDir, FileHandle embeddedTextureDir,
                             boolean convertUnits) {
        var data = new ModelData();
        data.id = modelId;

        var materialProcessor = new MaterialProcessor(new TextureProcessor(scene, modelDir, embeddedTextureDir));
        for (int i = 0; i < scene.mNumMaterials(); i++) {
            data.materials.add(materialProcessor.process(AIMaterial.create(scene.mMaterials().get(i)), i));
        }

        var meshProcessor = new MeshProcessor();
        Map<Integer, ModelMesh> meshes = new HashMap<>();
        Map<Integer, String> materialIds = new HashMap<>();
        for (int i = 0; i < scene.mNumMeshes(); i++) {
            var aiMesh = AIMesh.create(scene.mMeshes().get(i));
            var mesh = meshProcessor.process(aiMesh, i);
            if (mesh == null) {
                log.warn("Skip non-triangle mesh {}", i);
                continue;
            }
            data.addMesh(mesh);
            meshes.put(i, mesh);
            materialIds.put(i, data.materials.get(aiMesh.mMaterialIndex()).id);
        }

        Map<Integer, MeshProcessor.Skin> skins = new HashMap<>();
        for (var meshIndex : meshes.keySet()) {
            var skin = meshProcessor.skinOf(meshIndex);
            if (skin != null) {
                skins.put(meshIndex, skin);
            }
        }

        var nodeProcessor = new NodeProcessor(meshes, materialIds, skins);
        var root = nodeProcessor.process(scene.mRootNode());
        SceneNormalizer.apply(root, SceneNormalizer.correction(scene, convertUnits));
        data.nodes.add(root);
        data.animations.addAll(new AnimationProcessor(nodeProcessor).process(scene));
        return data;
    }

    private static String parentDir(FileHandle file) {
        var parent = file.file().getParent();
        return parent == null ? "" : parent.replace('\\', '/');
    }
}
