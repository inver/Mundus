package net.nevinsky.abyssus.lib.assets.gltf.converter;

import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import lombok.RequiredArgsConstructor;
import net.nevinsky.abyssus.lib.assets.gltf.GlTFException;
import net.nevinsky.abyssus.lib.assets.gltf.attribute.PBRTextureAttribute;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFDto;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFSamplerDto;
import net.nevinsky.abyssus.lib.assets.gltf.dto.material.GlTFTextureInfoDto;
import net.nevinsky.abyssus.lib.assets.gltf.extension.KHRTextureTransform;

@RequiredArgsConstructor
public class GlTFTextureConverter {

    private final TextureHolder textureHolder;

    public PBRTextureAttribute getTextureMap(GlTFDto root, long type, GlTFTextureInfoDto textureInfo) {
        TextureDescriptor<Texture> textureDescriptor = getTexture(root, textureInfo);

        PBRTextureAttribute attribute = new PBRTextureAttribute(type, textureDescriptor);
        attribute.uvIndex = textureInfo.getTexCoord();

        var ext = textureInfo.getExtension(KHRTextureTransform.class);
        if (ext != null) {
            attribute.offsetU = ext.getOffset()[0];
            attribute.offsetV = ext.getOffset()[1];
            attribute.scaleU = ext.getScale()[0];
            attribute.scaleV = ext.getScale()[1];
            attribute.rotationUV = ext.getRotation();
            if (ext.getTexCoord() != null) {
                attribute.uvIndex = ext.getTexCoord();
            }
        }

        return attribute;
    }

    public TextureDescriptor<Texture> getTexture(GlTFDto root, GlTFTextureInfoDto textureInfoDto) {
        var glTexture = root.getTextures().get(textureInfoDto.getIndex());

        var textureDescriptor = new TextureDescriptor<Texture>();

        boolean useMipMaps;
        if (glTexture.getSampler() != null) {
            var glSampler = root.getSamplers().get(glTexture.getSampler());
            mapTextureSampler(textureDescriptor, glSampler);
            useMipMaps = isMipMapFilter(glSampler);
        } else {
            // default sampler options.
            // https://github.com/KhronosGroup/glTF/blob/master/specification/2.0/README.md#texture
            textureDescriptor.minFilter = Texture.TextureFilter.Linear;
            textureDescriptor.magFilter = Texture.TextureFilter.Linear;
            textureDescriptor.uWrap = Texture.TextureWrap.Repeat;
            textureDescriptor.vWrap = Texture.TextureWrap.Repeat;
            useMipMaps = false;
        }

        //todo move this logic to holder service
        var textureMap = useMipMaps
                ? textureHolder.getTexturesMipmap()
                : textureHolder.getTexturesSimple();

        var texture = textureMap.get(glTexture.getSource());
        if (texture == null) {
            throw new GlTFException("texture not loaded");
        }
        textureDescriptor.texture = texture;
        return textureDescriptor;
    }

    public void mapTextureSampler(TextureDescriptor<Texture> textureDescriptor, GlTFSamplerDto glSampler) {
        if (glSampler.getMinFilter() != null) {
            textureDescriptor.minFilter = mapTextureMinFilter(glSampler.getMinFilter().getValue());
        }
        if (glSampler.getMagFilter() != null) {
            textureDescriptor.magFilter = mapTextureMagFilter(glSampler.getMagFilter().getValue());
        }
        if (glSampler.getWrapS() != null) {
            textureDescriptor.uWrap = mapTextureWrap(glSampler.getWrapS().getValue());
        }
        if (glSampler.getWrapT() != null) {
            textureDescriptor.vWrap = mapTextureWrap(glSampler.getWrapT().getValue());
        }
    }

    public void mapTextureSampler(TextureLoader.TextureParameter textureParameter, GlTFSamplerDto glSampler) {
        textureParameter.minFilter = mapTextureMinFilter(glSampler.getMinFilter().getValue());
        textureParameter.magFilter = mapTextureMagFilter(glSampler.getMagFilter().getValue());
        textureParameter.wrapU = mapTextureWrap(glSampler.getWrapS().getValue());
        textureParameter.wrapV = mapTextureWrap(glSampler.getWrapT().getValue());
    }

    public void mapTextureSampler(TextureLoader.TextureParameter textureParameter) {
        textureParameter.minFilter = mapTextureMinFilter(null);
        textureParameter.magFilter = mapTextureMagFilter(null);
        textureParameter.wrapU = mapTextureWrap(null);
        textureParameter.wrapV = mapTextureWrap(null);
    }

    public static boolean isMipMapFilter(GlTFSamplerDto sampler) {
        Texture.TextureFilter filter = mapTextureMinFilter(sampler.getMinFilter().getValue());
        switch (filter) {
            case Nearest:
            case Linear:
                return false;
            case MipMapNearestNearest:
            case MipMapLinearNearest:
            case MipMapNearestLinear:
            case MipMapLinearLinear:
                return true;
            default:
                throw new GlTFException("unexpected texture min filter " + filter);
        }
    }

    public static Texture.TextureFilter mapTextureMinFilter(Integer filter) {
        if (filter == null) {
            return Texture.TextureFilter.Linear;
        }
        switch (filter) {
            case 9728:
                return Texture.TextureFilter.Nearest;
            case 9729:
                return Texture.TextureFilter.Linear;
            case 9984:
                return Texture.TextureFilter.MipMapNearestNearest;
            case 9985:
                return Texture.TextureFilter.MipMapLinearNearest;
            case 9986:
                return Texture.TextureFilter.MipMapNearestLinear;
            case 9987:
                return Texture.TextureFilter.MipMapLinearLinear;
        }
        throw new GlTFException("unexpected texture mag filter " + filter);
    }

    public static Texture.TextureFilter mapTextureMagFilter(Integer filter) {
        if (filter == null) {
            return Texture.TextureFilter.Linear;
        }
        switch (filter) {
            case 9728:
                return Texture.TextureFilter.Nearest;
            case 9729:
                return Texture.TextureFilter.Linear;
        }
        throw new GlTFException("unexpected texture mag filter " + filter);
    }

    // https://github.com/KhronosGroup/glTF/tree/master/specification/2.0#samplerwraps
    // https://github.com/KhronosGroup/glTF/tree/master/specification/2.0#samplerwrapt
    private Texture.TextureWrap mapTextureWrap(Integer wrap) {
        if (wrap == null) {
            return Texture.TextureWrap.Repeat;
        }
        switch (wrap) {
            case 33071:
                return Texture.TextureWrap.ClampToEdge;
            case 33648:
                return Texture.TextureWrap.MirroredRepeat;
            case 10497:
                return Texture.TextureWrap.Repeat;
        }
        throw new GlTFException("unexpected texture wrap " + wrap);
    }

}
