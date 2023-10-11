package com.mbrlabs.mundus.commons.loader;

import com.badlogic.gdx.files.FileHandle;
import com.mbrlabs.mundus.commons.model.ImportedModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ModelImporter {
    private final AssimpWorker assimpWorker;
    private final G3dModelLoader g3dModelLoader;

    public ImportedModel importAndConvertToTmpFolder(FileHandle tempFolder, FileHandle file) {
        if (file == null || !file.exists()) {
            log.warn("Try to import <null> file");
            return null;
        }

        ImportedModel modelFile;
        if (file.name().endsWith("g3db")) {
            modelFile = g3dModelLoader.importModel(file);
        } else {
            modelFile = assimpWorker.importModel(file);
        }

        modelFile.getMain().copyTo(tempFolder);
        modelFile.getDependencies().forEach(dep -> dep.copyTo(tempFolder));

        return modelFile;
    }

    public void loadModelAndSaveForAsset(FileHandle from, FileHandle to) {
        assimpWorker.loadModelAndSaveForAsset(from, to);
    }
}
