package com.mbrlabs.mundus.commons.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;

public class ParentBasedTextureProvider implements TextureProvider {

    private final FileHandle mainFile;
    private final Texture.TextureFilter minFilter;
    private final Texture.TextureFilter magFilter;
    private final Texture.TextureWrap uWrap;
    private final Texture.TextureWrap vWrap;
    private final boolean useMipMaps;

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
        Texture result;
        if (fileName.startsWith("/")) {
            result = new Texture(Gdx.files.internal(fileName), useMipMaps);
        } else {
            result = new Texture(Gdx.files.internal(mainFile.parent().child(fileName).path()), useMipMaps);
        }

        result.setFilter(minFilter, magFilter);
        result.setWrap(uWrap, vWrap);
        return result;
    }
}