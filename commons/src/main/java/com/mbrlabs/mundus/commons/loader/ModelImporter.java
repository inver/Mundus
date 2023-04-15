package com.mbrlabs.mundus.commons.loader;

import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.commons.model.ImportedModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nevinsky.abyssus.core.model.Model;

@Slf4j
@RequiredArgsConstructor
public class ModelImporter {

    //todo remove this hardcode, use DI
    private final AssimpModelLoader assimpModelLoader;

    public ImportedModel importAndConvertToTmpFolder(FileHandle tempFolder, FileHandle file) {
        if (file == null || !file.exists()) {
            log.warn("Try to import <null> file");
            return null;
        }

        var modelFile = assimpModelLoader.importModel(file);
        modelFile.getMain().copyTo(tempFolder);
        modelFile.getDependencies().forEach(dep -> dep.copyTo(tempFolder));

        return modelFile;
    }

    public Model loadModel(FileHandle file) {
        return assimpModelLoader.loadModel(file);
    }
}
