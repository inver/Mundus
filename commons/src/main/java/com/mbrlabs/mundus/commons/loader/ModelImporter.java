package com.mbrlabs.mundus.commons.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import net.nevinsky.mundus.core.model.Model;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.UBJsonReader;
import com.mbrlabs.mundus.commons.loader.ac3d.Ac3dModelLoader;
import com.mbrlabs.mundus.commons.loader.ac3d.Ac3dParser;
import com.mbrlabs.mundus.commons.loader.g3d.MG3dModelLoader;
import com.mbrlabs.mundus.commons.loader.gltf.GltfLoaderWrapper;
import com.mbrlabs.mundus.commons.loader.obj.ObjModelLoader;
import com.mbrlabs.mundus.commons.model.ImportedModel;
import com.mbrlabs.mundus.commons.utils.FileFormatUtils;
import lombok.extern.slf4j.Slf4j;
import net.mgsx.gltf.exporters.GLTFExporter;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ModelImporter {

    private final Map<String, ModelLoader> loaders = new HashMap<>();

    private final GLTFExporter gltfExporter = new GLTFExporter();

    public ModelImporter() {
//        loaders.put(FileFormatUtils.FORMAT_3D_AC3D, new Ac3dModelLoader(new Ac3dParser()));
//        loaders.put(FileFormatUtils.FORMAT_3D_WAVEFONT, new ObjModelLoader());
//        loaders.put(FileFormatUtils.FORMAT_3D_G3DB, new MG3dModelLoader(new UBJsonReader()));
//        loaders.put(FileFormatUtils.FORMAT_3D_GLTF, new GltfLoaderWrapper(new Json()));
    }

    public ImportedModel importAndConvertToTmpFolder(FileHandle tempFolder, FileHandle file) {
        if (file == null || !file.exists()) {
            log.warn("Try to import <null> file");
            return null;
        }

        return null;
//        var modelFile = loaders.get(FileFormatUtils.getFileExtension(file)).importModel(file);
//        modelFile.getMain().copyTo(tempFolder);
//        modelFile.getDependencies().forEach(dep -> dep.copyTo(tempFolder));
//
//        return modelFile;
    }

    public Model loadModel(FileHandle file) {
        return null;
//        return loaders.get(FileFormatUtils.getFileExtension(file)).loadModel(file, new ParentBasedTextureProvider(file));
    }

    private static class ParentBasedTextureProvider implements TextureProvider {

        private final FileHandle mainFile;
        private Texture.TextureFilter minFilter, magFilter;
        private Texture.TextureWrap uWrap, vWrap;
        private boolean useMipMaps;

        public ParentBasedTextureProvider(FileHandle mainFile) {
            this(mainFile, Texture.TextureFilter.Linear, Texture.TextureFilter.Linear, Texture.TextureWrap.Repeat,
                    Texture.TextureWrap.Repeat, false);
        }

        public ParentBasedTextureProvider(FileHandle mainFile, Texture.TextureFilter minFilter,
                                          Texture.TextureFilter magFilter, Texture.TextureWrap uWrap,
                                          Texture.TextureWrap vWrap, boolean useMipMaps) {
            this.mainFile = mainFile;
            this.minFilter = minFilter;
            this.magFilter = magFilter;
            this.uWrap = uWrap;
            this.vWrap = vWrap;
            this.useMipMaps = useMipMaps;
        }

        @Override
        public Texture load(String fileName) {
            Texture result = new Texture(Gdx.files.internal(mainFile.parent().child(fileName).path()), useMipMaps);
            result.setFilter(minFilter, magFilter);
            result.setWrap(uWrap, vWrap);
            return result;
        }
    }
}
