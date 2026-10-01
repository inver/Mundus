package net.nevinsky.abyssus.lib.assets.assimp;

import com.badlogic.gdx.files.FileHandle;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.Assimp;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.lwjgl.assimp.Assimp.aiGetMaterialTexture;
import static org.lwjgl.assimp.Assimp.aiGetMaterialTextureCount;
import static org.lwjgl.assimp.Assimp.aiReturn_SUCCESS;
import static org.lwjgl.assimp.Assimp.aiTextureType_UNKNOWN;

/**
 * Converts a model file of any Assimp supported format to glTF 2.0 and copies the referenced texture files next to
 * it, keeping their relative paths. Embedded textures are packed into the exported {@code .bin} by Assimp.
 */
@Slf4j
public class AssimpModelExporter {

    private static final String GLTF2 = "gltf2";

    /**
     * @param from source model file
     * @param to   target {@code .gltf} file; the {@code .bin} and the textures are written next to it
     * @throws AssimpImportException if import or export fails
     */
    public void exportGltf(FileHandle from, FileHandle to) {
        try (var imported = AssimpImporter.importScene(from.path(), AssimpFlags.DEFAULT)) {
            var scene = imported.scene();
            to.parent().mkdirs();
            copyTextures(scene, from.parent(), to.parent());

            var result = Assimp.aiExportScene(scene, GLTF2, to.path(), 0);
            if (result != aiReturn_SUCCESS) {
                throw new AssimpImportException("Error exporting model [from: " + from + ", to: " + to + "]: "
                        + Assimp.aiGetErrorString());
            }
        }
    }

    private void copyTextures(AIScene scene, FileHandle fromDir, FileHandle toDir) {
        for (var relative : collectTexturePaths(scene)) {
            var source = fromDir.child(relative);
            if (!source.exists()) {
                log.warn("Texture file not found, skipped: {}", source);
                continue;
            }
            source.copyTo(toDir.child(relative));
        }
    }

    /** All referenced, non embedded texture paths of all materials and all texture types (all indices). */
    static Set<String> collectTexturePaths(AIScene scene) {
        var result = new LinkedHashSet<String>();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            var path = AIString.calloc(stack);
            for (int m = 0; m < scene.mNumMaterials(); m++) {
                var material = AIMaterial.create(scene.mMaterials().get(m));
                for (int type = 1; type <= aiTextureType_UNKNOWN; type++) {
                    int count = aiGetMaterialTextureCount(material, type);
                    for (int i = 0; i < count; i++) {
                        var res = aiGetMaterialTexture(material, type, i, path, (IntBuffer) null, null, null, null,
                                null, null);
                        if (res != aiReturn_SUCCESS) {
                            continue;
                        }
                        var relative = TextureProcessor.normalizePath(path.dataString());
                        if (!relative.isEmpty() && !relative.startsWith("*")) {
                            result.add(relative);
                        }
                    }
                }
            }
        }
        return result;
    }
}
