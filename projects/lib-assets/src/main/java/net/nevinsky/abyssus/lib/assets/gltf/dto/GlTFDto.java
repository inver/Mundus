package net.nevinsky.abyssus.lib.assets.gltf.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import net.nevinsky.abyssus.lib.assets.gltf.dto.animation.GlTFAnimationDto;
import net.nevinsky.abyssus.lib.assets.gltf.dto.material.GlTFMaterialDto;
import net.nevinsky.abyssus.lib.assets.gltf.dto.material.GlTFTextureDto;
import net.nevinsky.abyssus.lib.assets.gltf.dto.mesh.GlTFMeshDto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The root object for a glTF asset.
 */
@Getter
@Setter
public class GlTFDto extends GlTFPropertyDto {
    /**
     * The index of the default scene.  This property **MUST NOT** be defined, when `scenes` is undefined.
     */
    private int scene = -1;

    /**
     * Metadata about the glTF asset.
     */
    private final GlTFAssetDto asset;

    /**
     * Names of glTF extensions used in this asset.
     * <p>
     * Min items = 1.
     */
    private final List<String> extensionsUsed = new ArrayList<>();

    /**
     * Names of glTF extensions required to properly load this asset.
     * <p>
     * Min items = 1.
     */
    private final Set<String> extensionsRequired = new HashSet<>();

    /**
     * An array of accessors.  An accessor is a typed view into a bufferView.
     * <p>
     * Min items = 1.
     */
    private final List<GlTFAccessorDto> accessors = new ArrayList<>();

    /**
     * An array of keyframe animations.
     */
    private final List<GlTFAnimationDto> animations = new ArrayList<>();

    /**
     * An array of buffers.  A buffer points to binary geometry, animation, or skins.
     */
    private final List<GlTFBufferDto> buffers = new ArrayList<>();

    /**
     * An array of bufferViews.  A bufferView is a view into a buffer generally representing a subset of the buffer.
     */
    private final List<GlTFBufferViewDto> bufferViews = new ArrayList<>();

    /**
     * An array of cameras.  A camera defines a projection matrix.
     */
    private final List<GlTFCameraDto> cameras = new ArrayList<>();

    /**
     * An array of images.  An image defines data used to create a texture.
     */
    private final List<GlTFImageDto> images = new ArrayList<>();

    /**
     * An array of materials.  A material defines the appearance of a primitive.
     */
    private final List<GlTFMaterialDto> materials = new ArrayList<>();

    /**
     * An array of meshes.  A mesh is a set of primitives to be rendered.
     */
    private final List<GlTFMeshDto> meshes = new ArrayList<>();

    /**
     * An array of nodes.
     */
    private final List<GlTFNodeDto> nodes = new ArrayList<>();

    /**
     * An array of samplers.  A sampler contains properties for texture filtering and wrapping modes.
     */
    private final List<GlTFSamplerDto> samplers = new ArrayList<>();

    /**
     * An array of scenes.
     */
    private final List<GlTFSceneDto> scenes = new ArrayList<>();

    /**
     * An array of skins.  A skin is defined by joints and matrices.
     */
    private final List<GlTFImageDto> skins = new ArrayList<>();

    /**
     * An array of textures.
     */
    private final List<GlTFTextureDto> textures = new ArrayList<>();

    @JsonCreator
    public GlTFDto(@JsonProperty("asset") GlTFAssetDto asset) {
        this.asset = asset;
    }
}
