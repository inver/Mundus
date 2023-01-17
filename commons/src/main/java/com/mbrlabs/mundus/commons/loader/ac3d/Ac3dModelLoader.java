package com.mbrlabs.mundus.commons.loader.ac3d;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.mbrlabs.mundus.commons.core.AppModelLoader;
import com.mbrlabs.mundus.commons.loader.ac3d.dto.Ac3dModel;
import com.mbrlabs.mundus.commons.loader.ac3d.dto.Ac3dObject;
import com.mbrlabs.mundus.commons.model.ModelFiles;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.util.List;

@Slf4j
public class Ac3dModelLoader extends ModelLoader<ModelLoader.ModelParameters> implements AppModelLoader {
    private final Ac3dParser parser;
    private final Ac3dToLibGdxConverter converter = new Ac3dToLibGdxConverter();

    public Ac3dModelLoader(FileHandleResolver resolver, Ac3dParser parser) {
        super(resolver);
        this.parser = parser;
    }

    public Ac3dModelLoader(Ac3dParser parser) {
        this(null, parser);
    }

    @Override
    public ModelData loadModelData(FileHandle fileHandle, ModelParameters parameters) {
        var model = load(fileHandle);
        return converter.convert(model);
    }


    private Ac3dModel load(FileHandle fileHandle) {
        try (var br = new BufferedReader(fileHandle.reader())) {
            return parser.parse(br);
        } catch (Exception e) {
            log.error("ERROR", e);
        }
        return null;
    }

    @Override
    public ModelFiles getFileWithDependencies(FileHandle handle) {
        var model = load(handle);
        if (model == null) {
            return null;
        }

        var res = new ModelFiles(handle);
        if (CollectionUtils.isNotEmpty(model.getObjects())) {
            model.getObjects().forEach(obj -> getDependencies(handle, obj, res.getDependencies()));
        }

        return res;
    }

    private void getDependencies(FileHandle parent, Ac3dObject object, List<FileHandle> dependencies) {
        if (StringUtils.isNotEmpty(object.getTexturePath())) {
            //todo skip duplicated files
            var depFile = new FileHandle(parent.parent().path() + '/' + object.getTexturePath());
            if (depFile.exists() && !dependencies.contains(depFile)) {
                dependencies.add(depFile);
            }
        }

        if (CollectionUtils.isNotEmpty(object.getChildren())) {
            object.getChildren().forEach(obj -> getDependencies(parent, obj, dependencies));
        }
    }
}
