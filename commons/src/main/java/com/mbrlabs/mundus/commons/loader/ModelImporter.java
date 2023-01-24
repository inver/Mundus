package com.mbrlabs.mundus.commons.loader;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.UBJsonReader;
import com.mbrlabs.mundus.commons.core.AppModelLoader;
import com.mbrlabs.mundus.commons.loader.ac3d.Ac3dModelLoader;
import com.mbrlabs.mundus.commons.loader.ac3d.Ac3dParser;
import com.mbrlabs.mundus.commons.loader.g3d.MG3dModelLoader;
import com.mbrlabs.mundus.commons.loader.gltf.GltfLoaderWrapper;
import com.mbrlabs.mundus.commons.loader.obj.ObjModelLoader;
import com.mbrlabs.mundus.commons.model.ModelFiles;
import com.mbrlabs.mundus.commons.utils.FileFormatUtils;
import lombok.extern.slf4j.Slf4j;
import net.mgsx.gltf.exporters.GLTFExporter;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ModelImporter {

    private final Map<String, AppModelLoader> loaders = new HashMap<>();

    private final GLTFExporter gltfExporter = new GLTFExporter();

    public ModelImporter() {
        loaders.put(FileFormatUtils.FORMAT_3D_AC3D, new Ac3dModelLoader(new Ac3dParser()));
        loaders.put(FileFormatUtils.FORMAT_3D_WAVEFONT, new ObjModelLoader());
        loaders.put(FileFormatUtils.FORMAT_3D_G3DB, new MG3dModelLoader(new UBJsonReader()));
        loaders.put(FileFormatUtils.FORMAT_3D_GLTF, new GltfLoaderWrapper(new Json()));
    }

    public ModelFiles importAndConvertToTmpFolder(FileHandle tempFolder, FileHandle file) {
        if (file == null || !file.exists()) {
            log.warn("Try to import <null> file");
            return null;
        }

        var modelFile = loaders.get(FileFormatUtils.getFileExtension(file)).getFileWithDependencies(file);
        modelFile.getMain().copyTo(tempFolder);
        modelFile.getDependencies().forEach(dep -> dep.copyTo(tempFolder));

        return modelFile;
    }

    public Model loadModel(FileHandle file) {
        return loaders.get(FileFormatUtils.getFileExtension(file)).loadModel(file);
    }
}
