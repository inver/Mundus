package com.mbrlabs.mundus.commons.loader;

import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.commons.model.ImportedModel;
import lombok.RequiredArgsConstructor;
import net.nevinsky.abyssus.core.model.Model;
import net.nevinsky.abyssus.lib.assets.assimp.AssimpFlags;
import net.nevinsky.abyssus.lib.assets.assimp.AssimpModelDataLoader;
import net.nevinsky.abyssus.lib.assets.assimp.AssimpModelExporter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;

/**
 * {@link ModelLoader} on top of the Assimp based loader of {@code lib-assets}.
 */
@RequiredArgsConstructor
public class AssimpModelLoader implements ModelLoader {

    private final AssimpModelDataLoader dataLoader;
    private final AssimpModelExporter exporter;

    public AssimpModelLoader() {
        this(new AssimpModelDataLoader(), new AssimpModelExporter());
    }

    /**
     * Loads a previously exported asset. Embedded textures are extracted next to the model file.
     */
    @Override
    public Model loadModel(FileHandle fileHandle) {
        return load(fileHandle, fileHandle.parent().child("embedded"));
    }

    /**
     * Loads a model for preview before it is imported. Embedded textures go to a temporary directory so the source
     * folder is not modified.
     */
    @Override
    public ImportedModel importModel(FileHandle fileHandle) {
        try {
            var tmp = new FileHandle(Files.createTempDirectory("mundus_import_").toFile());
            return new ImportedModel(load(fileHandle, tmp), fileHandle);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void loadModelAndSaveForAsset(FileHandle from, FileHandle to) {
        exporter.exportGltf(from, to);
    }

    private Model load(FileHandle file, FileHandle embeddedDir) {
        var data = dataLoader.load(file.name(), file, AssimpFlags.DEFAULT, embeddedDir);
        return new Model(data, new ParentBasedTextureProvider(file));
    }
}
