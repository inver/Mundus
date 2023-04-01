package com.mbrlabs.mundus.commons.loader;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;
import com.mbrlabs.mundus.commons.model.ImportedModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModelImporter {

    //todo remove this hardcode, use DI
    private final AssimpModelLoader modelLoader = new AssimpModelLoader();

    public ImportedModel importAndConvertToTmpFolder(FileHandle tempFolder, FileHandle file) {
        if (file == null || !file.exists()) {
            log.warn("Try to import <null> file");
            return null;
        }

        var modelFile = modelLoader.importModel(file);
        modelFile.getMain().copyTo(tempFolder);
        modelFile.getDependencies().forEach(dep -> dep.copyTo(tempFolder));

        return modelFile;
    }

    public Model loadModel(FileHandle file) {
        return modelLoader.loadModel(file);
    }
}
