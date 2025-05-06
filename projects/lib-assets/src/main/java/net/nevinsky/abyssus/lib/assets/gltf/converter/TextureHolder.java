package net.nevinsky.abyssus.lib.assets.gltf.converter;

import com.badlogic.gdx.graphics.Texture;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class TextureHolder {

    @Getter
    protected final Map<Integer, Texture> texturesSimple = new HashMap<>();
    @Getter
    protected final Map<Integer, Texture> texturesMipmap = new HashMap<>();
}
