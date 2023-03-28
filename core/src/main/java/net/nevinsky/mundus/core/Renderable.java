package net.nevinsky.mundus.core;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.math.Matrix4;
import lombok.Getter;
import lombok.Setter;
import net.nevinsky.mundus.core.mesh.MeshPart;
import net.nevinsky.mundus.core.shader.Shader;
import net.nevinsky.mundus.core.shader.ShaderProvider;

@Getter
@Setter
public class Renderable {
    private final Matrix4 worldTransform = new Matrix4();
    /**
     * The {@link MeshPart} that contains the shape to render
     **/
    private final MeshPart meshPart = new MeshPart();
    /**
     * The {@link Material} to be applied to the shape (part of the mesh), must not be null.
     *
     * @see #environment
     **/
    private Material material;
    /**
     * The {@link Environment} to be used to render this Renderable, may be null. When specified it will be combined by
     * the shader with the {@link #material}. When both the material and environment contain an attribute of the same
     * type, the attribute of the material will be used.
     **/
    private Environment environment;
    /**
     * The bone transformations used for skinning, or null if not applicable. When specified and the mesh contains one
     * or more {@link com.badlogic.gdx.graphics.VertexAttributes.Usage#BoneWeight} vertex attributes, then the
     * BoneWeight index is used as index in the array. If the array isn't large enough then the identity matrix is used.
     * Each BoneWeight weight is used to combine multiple bones into a single transformation matrix, which is used to
     * transform the vertex to model space. In other words: the bone transformation is applied prior to the
     * {@link #worldTransform}.
     */
    private Matrix4[] bones;
    /**
     * The {@link Shader} to be used to render this Renderable using a {@link ModelBatch}, may be null. It is not
     * guaranteed that the shader will be used, the used {@link ShaderProvider} is responsible for actually choosing the
     * correct shader to use.
     **/
    private Shader shader;
    /**
     * User definable value, may be null.
     */
    private Object userData;

    private Renderable set(Renderable renderable) {
        worldTransform.set(renderable.worldTransform);
        material = renderable.material;
        meshPart.set(renderable.meshPart);
        bones = renderable.bones;
        environment = renderable.environment;
        shader = renderable.shader;
        userData = renderable.userData;
        return this;
    }
}