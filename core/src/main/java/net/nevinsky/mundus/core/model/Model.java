package net.nevinsky.mundus.core.model;

import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.NodeKeyframe;
import com.badlogic.gdx.graphics.g3d.model.data.*;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.*;
import lombok.Getter;
import net.nevinsky.mundus.core.ModelInstance;
import net.nevinsky.mundus.core.mesh.Mesh;
import net.nevinsky.mundus.core.mesh.MeshPart;
import net.nevinsky.mundus.core.node.Animation;
import net.nevinsky.mundus.core.node.Node;
import net.nevinsky.mundus.core.node.NodeAnimation;
import net.nevinsky.mundus.core.node.NodePart;

import java.nio.Buffer;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class Model implements Disposable {
    @Getter
    private final Map<String, Material> materials = new HashMap<>();
    @Getter
    private final List<Node> nodes = new ArrayList<>();
    @Getter
    private final List<Animation> animations = new ArrayList<>();
    @Getter
    private final List<Mesh> meshes = new ArrayList<>();
    @Getter
    private final Map<String, MeshPart> meshParts = new HashMap<>();
    protected final List<Disposable> disposables = new ArrayList<>();

    /**
     * Constructs an empty model. Manual created models do not manage their resources by default. Use
     * {@link #manageDisposable(Disposable)} to add resources to be managed by this model.
     */
    public Model() {
    }

    /**
     * Constructs a new Model based on the {@link ModelData}. Texture files will be loaded from the internal file
     * storage via an {@link TextureProvider.FileTextureProvider}.
     *
     * @param modelData the {@link ModelData} got from e.g. {@link ModelLoader}
     */
    public Model(ModelData modelData) {
        this(modelData, new TextureProvider.FileTextureProvider());
    }

    /**
     * Constructs a new Model based on the {@link ModelData}.
     *
     * @param modelData       the {@link ModelData} got from e.g. {@link ModelLoader}
     * @param textureProvider the {@link TextureProvider} to use for loading the textures
     */
    public Model(ModelData modelData, TextureProvider textureProvider) {
        load(modelData, textureProvider);
    }

    protected void load(ModelData modelData, TextureProvider textureProvider) {
        loadMeshes(modelData.getMeshes());
        loadMaterials(modelData.getMaterials(), textureProvider);
        loadNodes(modelData.getNodes());
        loadAnimations(modelData.getAnimations());
        calculateTransforms();
    }

    protected void loadAnimations(Iterable<ModelAnimation> modelAnimations) {
        for (final ModelAnimation anim : modelAnimations) {
            Animation animation = new Animation();
            animation.id = anim.id;
            for (ModelNodeAnimation nanim : anim.nodeAnimations) {
                final Node node = getNode(nanim.nodeId);
                if (node == null) {
                    continue;
                }

                NodeAnimation nodeAnim = new NodeAnimation();
                nodeAnim.node = node;

                if (nanim.translation != null) {
                    nodeAnim.translation = convertAndModifyDuration(nanim.translation, Vector3::new, animation,
                            () -> node.translation);
                }

                if (nanim.rotation != null) {
                    nodeAnim.rotation =
                            convertAndModifyDuration(nanim.rotation, Quaternion::new, animation, () -> node.rotation);
                }

                if (nanim.scaling != null) {
                    nodeAnim.scaling =
                            convertAndModifyDuration(nanim.scaling, Vector3::new, animation, () -> node.scale);
                }

                if ((nodeAnim.translation != null && nodeAnim.translation.size() > 0)
                        || (nodeAnim.rotation != null && nodeAnim.rotation.size() > 0)
                        || (nodeAnim.scaling != null && nodeAnim.scaling.size() > 0))
                    animation.nodeAnimations.add(nodeAnim);
            }
            if (animation.nodeAnimations.size > 0) animations.add(animation);
        }
    }

    private <T> List<NodeKeyframe<T>> convertAndModifyDuration(Array<ModelNodeKeyframe<T>> input,
                                                               Function<T, T> converter,
                                                               Animation animation, Supplier<T> nodeParamExtractor) {
        List<NodeKeyframe<T>> res = new ArrayList<>(input.size);
        for (ModelNodeKeyframe<T> kf : input) {
            if (kf.keytime > animation.duration) {
                animation.duration = kf.keytime;
            }
            res.add(new NodeKeyframe<>(kf.keytime,
                    converter.apply(kf.value == null ? nodeParamExtractor.get() : kf.value)));
        }
        return res;
    }

    private final ObjectMap<NodePart, ArrayMap<String, Matrix4>> nodePartBones = new ObjectMap<>();

    protected void loadNodes(Iterable<ModelNode> modelNodes) {
        nodePartBones.clear();
        for (ModelNode node : modelNodes) {
            nodes.add(loadNode(node));
        }
        for (ObjectMap.Entry<NodePart, ArrayMap<String, Matrix4>> e : nodePartBones.entries()) {
            if (e.key.invBoneBindTransforms == null)
                e.key.invBoneBindTransforms = new ArrayMap<>(Node.class, Matrix4.class);
            e.key.invBoneBindTransforms.clear();
            for (ObjectMap.Entry<String, Matrix4> b : e.value.entries())
                e.key.invBoneBindTransforms.put(getNode(b.key), new Matrix4(b.value).inv());
        }
    }

    protected Node loadNode(ModelNode modelNode) {
        Node node = new Node();
        node.id = modelNode.id;

        if (modelNode.translation != null) node.translation.set(modelNode.translation);
        if (modelNode.rotation != null) node.rotation.set(modelNode.rotation);
        if (modelNode.scale != null) node.scale.set(modelNode.scale);
        if (modelNode.parts != null) {
            for (ModelNodePart modelNodePart : modelNode.parts) {
                MeshPart meshPart = meshParts.get(modelNodePart.meshPartId);
                Material meshMaterial = materials.get(modelNodePart.materialId);

                if (meshPart == null || meshMaterial == null) {
                    throw new GdxRuntimeException("Invalid node: " + node.id);
                }

                NodePart nodePart = new NodePart();
                nodePart.meshPart = meshPart;
                nodePart.material = meshMaterial;
                node.parts.add(nodePart);
                if (modelNodePart.bones != null) nodePartBones.put(nodePart, modelNodePart.bones);
            }
        }

        if (modelNode.children != null) {
            for (ModelNode child : modelNode.children) {
                node.addChild(loadNode(child));
            }
        }

        return node;
    }

    protected void loadMeshes(Collection<ModelMesh> meshes) {
        for (ModelMesh mesh : meshes) {
            convertMesh(mesh);
        }
    }

    protected void convertMesh(ModelMesh modelMesh) {
        int numIndices = 0;
        for (ModelMeshPart part : modelMesh.parts) {
            numIndices += part.indices.length;
        }
        boolean hasIndices = numIndices > 0;
        VertexAttributes attributes = new VertexAttributes(modelMesh.attributes);
        int numVertices = modelMesh.vertices.length / (attributes.vertexSize / 4);

        Mesh mesh = new Mesh(true, numVertices, numIndices, attributes);
        meshes.add(mesh);
        disposables.add(mesh);

        BufferUtils.copy(modelMesh.vertices, mesh.getVerticesBuffer(), modelMesh.vertices.length, 0);
        int offset = 0;
        ((Buffer) mesh.getIndicesBuffer()).clear();
        for (ModelMeshPart part : modelMesh.parts) {
            MeshPart meshPart = new MeshPart();
            meshPart.id = part.id;
            meshPart.primitiveType = part.primitiveType;
            meshPart.offset = offset;
            meshPart.size = hasIndices ? part.indices.length : numVertices;
            meshPart.mesh = mesh;
            if (hasIndices) {
                mesh.getIndicesBuffer().put(part.indices);
            }
            offset += meshPart.size;
            meshParts.put(meshPart.id, meshPart);
        }
        mesh.getIndicesBuffer().position(0);
        meshParts.forEach((k, v) -> v.update());
    }

    protected void loadMaterials(Iterable<ModelMaterial> modelMaterials, TextureProvider textureProvider) {
        for (ModelMaterial mtl : modelMaterials) {
            materials.put(mtl.id, convertMaterial(mtl, textureProvider));
        }
    }

    protected Material convertMaterial(ModelMaterial mtl, TextureProvider textureProvider) {
        Material result = new Material();
        result.id = mtl.id;
        if (mtl.ambient != null) result.set(new ColorAttribute(ColorAttribute.Ambient, mtl.ambient));
        if (mtl.diffuse != null) result.set(new ColorAttribute(ColorAttribute.Diffuse, mtl.diffuse));
        if (mtl.specular != null) result.set(new ColorAttribute(ColorAttribute.Specular, mtl.specular));
        if (mtl.emissive != null) result.set(new ColorAttribute(ColorAttribute.Emissive, mtl.emissive));
        if (mtl.reflection != null) result.set(new ColorAttribute(ColorAttribute.Reflection, mtl.reflection));
        if (mtl.shininess > 0f) result.set(new FloatAttribute(FloatAttribute.Shininess, mtl.shininess));
        if (mtl.opacity != 1.f)
            result.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, mtl.opacity));

        ObjectMap<String, Texture> textures = new ObjectMap<String, Texture>();

        // FIXME uvScaling/uvTranslation totally ignored
        if (mtl.textures != null) {
            for (ModelTexture tex : mtl.textures) {
                Texture texture;
                if (textures.containsKey(tex.fileName)) {
                    texture = textures.get(tex.fileName);
                } else {
                    texture = textureProvider.load(tex.fileName);
                    textures.put(tex.fileName, texture);
                    disposables.add(texture);
                }

                TextureDescriptor descriptor = new TextureDescriptor(texture);
                descriptor.minFilter = texture.getMinFilter();
                descriptor.magFilter = texture.getMagFilter();
                descriptor.uWrap = texture.getUWrap();
                descriptor.vWrap = texture.getVWrap();

                float offsetU = tex.uvTranslation == null ? 0f : tex.uvTranslation.x;
                float offsetV = tex.uvTranslation == null ? 0f : tex.uvTranslation.y;
                float scaleU = tex.uvScaling == null ? 1f : tex.uvScaling.x;
                float scaleV = tex.uvScaling == null ? 1f : tex.uvScaling.y;

                switch (tex.usage) {
                    case ModelTexture.USAGE_DIFFUSE:
                        result.set(new TextureAttribute(TextureAttribute.Diffuse, descriptor, offsetU, offsetV, scaleU,
                                scaleV));
                        break;
                    case ModelTexture.USAGE_SPECULAR:
                        result.set(new TextureAttribute(TextureAttribute.Specular, descriptor, offsetU, offsetV, scaleU,
                                scaleV));
                        break;
                    case ModelTexture.USAGE_BUMP:
                        result.set(new TextureAttribute(TextureAttribute.Bump, descriptor, offsetU, offsetV, scaleU,
                                scaleV));
                        break;
                    case ModelTexture.USAGE_NORMAL:
                        result.set(new TextureAttribute(TextureAttribute.Normal, descriptor, offsetU, offsetV, scaleU,
                                scaleV));
                        break;
                    case ModelTexture.USAGE_AMBIENT:
                        result.set(new TextureAttribute(TextureAttribute.Ambient, descriptor, offsetU, offsetV, scaleU,
                                scaleV));
                        break;
                    case ModelTexture.USAGE_EMISSIVE:
                        result.set(new TextureAttribute(TextureAttribute.Emissive, descriptor, offsetU, offsetV, scaleU,
                                scaleV));
                        break;
                    case ModelTexture.USAGE_REFLECTION:
                        result.set(
                                new TextureAttribute(TextureAttribute.Reflection, descriptor, offsetU, offsetV, scaleU,
                                        scaleV));
                        break;
                }
            }
        }

        return result;
    }

    /**
     * Adds a {@link Disposable} to be managed and disposed by this Model. Can be used to keep track of manually loaded
     * textures for {@link ModelInstance}.
     *
     * @param disposable the Disposable
     */
    public void manageDisposable(Disposable disposable) {
        if (!disposables.contains(disposable)) {
            disposables.add(disposable);
        }
    }

    /**
     * @return the {@link Disposable} objects that will be disposed when the {@link #dispose()} method is called.
     */
    public Iterable<Disposable> getManagedDisposables() {
        return disposables;
    }

    @Override
    public void dispose() {
        for (Disposable disposable : disposables) {
            disposable.dispose();
        }
    }

    /**
     * Calculates the local and world transform of all {@link Node} instances in this model, recursively. First each
     * {@link Node#localTransform} transform is calculated based on the translation, rotation and scale of each Node.
     * Then each {@link Node#calculateWorldTransform()} is calculated, based on the parent's world transform and the
     * local transform of each Node. Finally, the animation bone matrices are updated accordingly.
     * </p>
     * <p>
     * This method can be used to recalculate all transforms if any of the Node's local properties (translation,
     * rotation, scale) was modified.
     */
    public void calculateTransforms() {
        final int n = nodes.size();
        for (int i = 0; i < n; i++) {
            nodes.get(i).calculateTransforms(true);
        }
        for (int i = 0; i < n; i++) {
            nodes.get(i).calculateBoneTransforms(true);
        }
    }

    /**
     * Calculate the bounding box of this model instance. This is a potential slow operation, it is advised to cache the
     * result.
     *
     * @param out the {@link BoundingBox} that will be set with the bounds.
     * @return the out parameter for chaining
     */
    public BoundingBox calculateBoundingBox(final BoundingBox out) {
        out.inf();
        return extendBoundingBox(out);
    }

    /**
     * Extends the bounding box with the bounds of this model instance. This is a potential slow operation, it is
     * advised to cache the result.
     *
     * @param out the {@link BoundingBox} that will be extended with the bounds.
     * @return the out parameter for chaining
     */
    public BoundingBox extendBoundingBox(final BoundingBox out) {
        final int n = nodes.size();
        for (int i = 0; i < n; i++)
            nodes.get(i).extendBoundingBox(out);
        return out;
    }

    /**
     * @param id The ID of the animation to fetch (case sensitive).
     * @return The {@link Animation} with the specified id, or null if not available.
     */
    public Animation getAnimation(final String id) {
        return getAnimation(id, true);
    }

    /**
     * @param id         The ID of the animation to fetch.
     * @param ignoreCase whether to use case sensitivity when comparing the animation id.
     * @return The {@link Animation} with the specified id, or null if not available.
     */
    public Animation getAnimation(final String id, boolean ignoreCase) {
        final int n = animations.size();
        Animation animation;
        if (ignoreCase) {
            for (int i = 0; i < n; i++)
                if ((animation = animations.get(i)).id.equalsIgnoreCase(id)) return animation;
        } else {
            for (int i = 0; i < n; i++)
                if ((animation = animations.get(i)).id.equals(id)) return animation;
        }
        return null;
    }

    /**
     * @param id The ID of the material to fetch.
     * @return The {@link Material} with the specified id, or null if not available.
     */
    public Material getMaterial(final String id) {
        return getMaterial(id, true);
    }

    /**
     * @param id         The ID of the material to fetch.
     * @param ignoreCase whether to use case sensitivity when comparing the material id.
     * @return The {@link Material} with the specified id, or null if not available.
     */
    public Material getMaterial(final String id, boolean ignoreCase) {
        final int n = materials.size();
        Material material;
        if (ignoreCase) {
            for (int i = 0; i < n; i++)
                if ((material = materials.get(i)).id.equalsIgnoreCase(id)) return material;
        } else {
            for (int i = 0; i < n; i++)
                if ((material = materials.get(i)).id.equals(id)) return material;
        }
        return null;
    }

    /**
     * @param id The ID of the node to fetch.
     * @return The {@link Node} with the specified id, or null if not found.
     */
    public Node getNode(final String id) {
        return getNode(id, true);
    }

    /**
     * @param id        The ID of the node to fetch.
     * @param recursive false to fetch a root node only, true to search the entire node tree for the specified node.
     * @return The {@link Node} with the specified id, or null if not found.
     */
    public Node getNode(final String id, boolean recursive) {
        return getNode(id, recursive, false);
    }

    /**
     * @param id         The ID of the node to fetch.
     * @param recursive  false to fetch a root node only, true to search the entire node tree for the specified node.
     * @param ignoreCase whether to use case sensitivity when comparing the node id.
     * @return The {@link Node} with the specified id, or null if not found.
     */
    public Node getNode(final String id, boolean recursive, boolean ignoreCase) {
        return Node.getNode(nodes, id, recursive, ignoreCase);
    }
}