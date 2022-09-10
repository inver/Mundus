package com.mbrlabs.mundus.commons.loader.ac3d;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.mbrlabs.mundus.commons.core.AppModelLoader;
import com.mbrlabs.mundus.commons.core.ModelFiles;
import com.mbrlabs.mundus.commons.loader.ac3d.dto.Ac3dModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;

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

        var res = new ModelFiles(handle);
        model.getObjects().forEach(obj -> {
            if (StringUtils.isNotEmpty(obj.getTexturePath())) {
                //todo skip duplicated files
                var depFile = new FileHandle(handle.parent().path() + '/' + obj.getTexturePath());
                if (depFile.exists()) {
                    res.getDependencies().add(depFile);
                }
            }
        });
        return res;
    }
}
