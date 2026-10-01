package net.nevinsky.abyssus.lib.assets.assimp;

import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.Assimp;

/**
 * Owns the native lifecycle of an imported Assimp scene.
 */
public final class AssimpImporter {

    private AssimpImporter() {
    }

    /**
     * Imports the scene. The caller must close the result, which releases the native memory.
     *
     * @throws AssimpImportException if Assimp fails or the scene is incomplete
     */
    public static ImportedScene importScene(String path, int flags) {
        AIScene scene = Assimp.aiImportFile(path, flags);
        if (scene == null || scene.mRootNode() == null) {
            var reason = Assimp.aiGetErrorString();
            if (scene != null) {
                Assimp.aiReleaseImport(scene);
            }
            throw new AssimpImportException("Error loading model [path: " + path + "]: " + reason);
        }
        return new ImportedScene(scene);
    }

    public static final class ImportedScene implements AutoCloseable {
        private final AIScene scene;
        private boolean closed;

        private ImportedScene(AIScene scene) {
            this.scene = scene;
        }

        public AIScene scene() {
            if (closed) {
                throw new IllegalStateException("Scene already released");
            }
            return scene;
        }

        @Override
        public void close() {
            if (!closed) {
                closed = true;
                Assimp.aiReleaseImport(scene);
            }
        }
    }
}
