package net.nevinsky.abyssus.core.model;

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
import com.badlogic.gdx.graphics.g3d.model.data.ModelAnimation;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMaterial;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNode;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNodeAnimation;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNodeKeyframe;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNodePart;
import com.badlogic.gdx.graphics.g3d.model.data.ModelTexture;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nevinsky.abyssus.core.ModelInstance;
import net.nevinsky.abyssus.core.mesh.Mesh;
import net.nevinsky.abyssus.core.mesh.MeshPart;
import net.nevinsky.abyssus.core.node.Animation;
import net.nevinsky.abyssus.core.node.Node;
import net.nevinsky.abyssus.core.node.NodeAnimation;
import net.nevinsky.abyssus.core.node.NodePart;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A model represents a 3D assets. It stores a hierarchy of nodes. A node has a transform and optionally a graphical
 * part in form of a {@link MeshPart} and {@link Material}. Mesh parts reference subsets of vertices in one of the
 * meshes of the model. Animations can be applied to nodes, to modify their transform (translation, rotation, scale)
 * over time.
 * </p>
 * <p>
 * A model can be rendered by creating a {@link ModelInstance} from it. That instance has an additional transform to
 * position the model in the world, and allows modification of materials and nodes without destroying the original
 * model. The original model is the owner of any meshes and textures, all instances created from the model share these
 * resources. Disposing the model will automatically make all instances invalid!
 * </p>
 * <p>
 * A model is created from {@link ModelData}, which in turn is loaded by a {@link ModelLoader}.
 *
 * @author badlogic, xoppa
 */
@Getter
@Slf4j
@NoArgsConstructor
public class Model implements Disposable {
    /**
     * root nodes of the model
     **/
    @Getter
    private final List<Node> nodes = new ArrayList<>();
    /**
     * animations of the model, modifying node transformations
     **/
    @Getter
    private final List<Animation> animations = new ArrayList<>();
    /**
     * Array of disposable resources like textures or meshes the Model is responsible for disposing
     **/
    protected final Set<Disposable> disposables = new HashSet<>();

    private final Map<NodePart, ArrayMap<String, Matrix4>> nodePartBones = new HashMap<>();

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
        var meshParts = loadMeshes(modelData.meshes);
        var materials = loadMaterials(modelData.materials, textureProvider);
        loadNodes(meshParts, materials, modelData.nodes);
        loadAnimations(modelData.animations);
        calculateTransforms();
    }

    protected void loadAnimations(Iterable<ModelAnimation> modelAnimations) {
        for (final ModelAnimation anim : modelAnimations) {
            Animation animation = new Animation(anim.id);
            for (ModelNodeAnimation nanim : anim.nodeAnimations) {
                final Node node = getNode(nanim.nodeId);
                if (node == null) {
                    continue;
                }
                NodeAnimation nodeAnim = new NodeAnimation();
                nodeAnim.node = node;

                if (nanim.translation != null) {
                    nodeAnim.translation = new Array<>();
                    nodeAnim.translation.ensureCapacity(nanim.translation.size);
                    for (ModelNodeKeyframe<Vector3> kf : nanim.translation) {
                        if (kf.keytime > animation.duration) {
                            animation.duration = kf.keytime;
                        }
                        nodeAnim.translation.add(new NodeKeyframe<>(kf.keytime,
                                new Vector3(kf.value == null ? node.translation : kf.value))
                        );
                    }
                }

                if (nanim.rotation != null) {
                    nodeAnim.rotation = new Array<>();
                    nodeAnim.rotation.ensureCapacity(nanim.rotation.size);
                    for (ModelNodeKeyframe<Quaternion> kf : nanim.rotation) {
                        if (kf.keytime > animation.duration) {
                            animation.duration = kf.keytime;
                        }
                        nodeAnim.rotation.add(new NodeKeyframe<>(kf.keytime,
                                new Quaternion(kf.value == null ? node.rotation : kf.value))
                        );
                    }
                }

                if (nanim.scaling != null) {
                    nodeAnim.scaling = new Array<>();
                    nodeAnim.scaling.ensureCapacity(nanim.scaling.size);
                    for (ModelNodeKeyframe<Vector3> kf : nanim.scaling) {
                        if (kf.keytime > animation.duration) {
                            animation.duration = kf.keytime;
                        }
                        nodeAnim.scaling.add(new NodeKeyframe<>(kf.keytime,
                                new Vector3(kf.value == null ? node.scale : kf.value))
                        );
                    }
                }

                if ((nodeAnim.translation != null && nodeAnim.translation.size > 0)
                        || (nodeAnim.rotation != null && nodeAnim.rotation.size > 0)
                        || (nodeAnim.scaling != null && nodeAnim.scaling.size > 0)) {
                    animation.nodeAnimations.add(nodeAnim);
                }
            }
            if (!animation.nodeAnimations.isEmpty()) {
                animations.add(animation);
            }
        }
    }

    protected void loadNodes(Map<String, MeshPart> meshParts, Map<String, Material> materials,
                             Iterable<ModelNode> modelNodes) {
        nodePartBones.clear();
        for (ModelNode node : modelNodes) {
            nodes.add(loadNode(meshParts, materials, node));
        }
        for (Map.Entry<NodePart, ArrayMap<String, Matrix4>> e : nodePartBones.entrySet()) {
            if (e.getKey().invBoneBindTransforms == null) {
                e.getKey().invBoneBindTransforms = new ArrayMap<>(Node.class, Matrix4.class);
            }
            e.getKey().invBoneBindTransforms.clear();
            for (ObjectMap.Entry<String, Matrix4> b : e.getValue().entries()) {
                e.getKey().invBoneBindTransforms.put(getNode(b.key), new Matrix4(b.value).inv());
            }
        }
    }

    protected Node loadNode(Map<String, MeshPart> meshParts, Map<String, Material> materials, ModelNode modelNode) {
        Node node = new Node();
        node.id = modelNode.id;

        if (modelNode.translation != null) {
            node.translation.set(modelNode.translation);
        }
        if (modelNode.rotation != null) {
            node.rotation.set(modelNode.rotation);
        }
        if (modelNode.scale != null) {
            node.scale.set(modelNode.scale);
        }
        // TODO create temporary maps for faster lookup?
        if (modelNode.parts != null) {
            for (ModelNodePart modelNodePart : modelNode.parts) {
                MeshPart meshPart = null;
                Material meshMaterial = null;

                if (modelNodePart.meshPartId != null) {
                    meshPart = meshParts.get(modelNodePart.meshPartId);
                }

                if (modelNodePart.materialId != null) {
                    meshMaterial = materials.get(StringUtils.upperCase(modelNodePart.materialId));
                }

                if (meshPart == null || meshMaterial == null) {
                    throw new GdxRuntimeException("Invalid node: " + node.id);
                }

                NodePart nodePart = new NodePart();
                nodePart.meshPart = meshPart;
                nodePart.material = meshMaterial;
                node.parts.add(nodePart);
                if (modelNodePart.bones != null) {
                    nodePartBones.put(nodePart, modelNodePart.bones);
                }
            }
        }

        if (modelNode.children != null) {
            for (ModelNode child : modelNode.children) {
                node.addChild(loadNode(meshParts, materials, child));
            }
        }

        return node;
    }

    protected Map<String, MeshPart> loadMeshes(Iterable<ModelMesh> meshes) {
        var res = new HashMap<String, MeshPart>();
        for (ModelMesh mesh : meshes) {
            convertMesh(res, mesh);
        }
        return res;
    }

    protected void convertMesh(Map<String, MeshPart> res, ModelMesh modelMesh) {
        int numIndices = 0;
        for (ModelMeshPart part : modelMesh.parts) {
            numIndices += part.indices.length;
        }
        boolean hasIndices = numIndices > 0;
        VertexAttributes attributes = new VertexAttributes(modelMesh.attributes);
        int numVertices = modelMesh.vertices.length / (attributes.vertexSize / 4);

        Mesh mesh = new Mesh(true, numVertices, numIndices, attributes);
//        meshes.add(mesh);
        disposables.add(mesh);

        BufferUtils.copy(modelMesh.vertices, mesh.getVerticesBuffer(), modelMesh.vertices.length, 0);

        int offset = 0;
        mesh.getIndicesBuffer().clear();

        boolean hasBoundingBox = true;
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
            if (part.getBoundingBox() == null) {
                hasBoundingBox = false;
            } else {
                meshPart.update(part.getBoundingBox());
            }
            res.put(meshPart.id, meshPart);
        }
        mesh.getIndicesBuffer().position(0);
        if (!hasBoundingBox) {
            //hack, for loaded model without bounding box
            //todo why for each mesh recalculated all map of mesh???
            for (MeshPart part : res.values()) {
                part.update();
            }
        }
    }

    protected Map<String, Material> loadMaterials(Iterable<ModelMaterial> modelMaterials,
                                                  TextureProvider textureProvider) {
        var res = new HashMap<String, Material>();
        for (ModelMaterial mtl : modelMaterials) {
            var m = convertMaterial(mtl, textureProvider);
            res.put(StringUtils.upperCase(m.id), m);
        }
        return res;
    }

    protected Material convertMaterial(ModelMaterial mtl, TextureProvider textureProvider) {
        Material result = new Material();
        result.id = mtl.id;
        if (mtl.ambient != null) {
            result.set(new ColorAttribute(ColorAttribute.Ambient, mtl.ambient));
        }
        if (mtl.diffuse != null) {
            result.set(new ColorAttribute(ColorAttribute.Diffuse, mtl.diffuse));
        }
        if (mtl.specular != null) {
            result.set(new ColorAttribute(ColorAttribute.Specular, mtl.specular));
        }
        if (mtl.emissive != null) {
            result.set(new ColorAttribute(ColorAttribute.Emissive, mtl.emissive));
        }
        if (mtl.reflection != null) {
            result.set(new ColorAttribute(ColorAttribute.Reflection, mtl.reflection));
        }
        if (mtl.shininess > 0f) {
            result.set(new FloatAttribute(FloatAttribute.Shininess, mtl.shininess));
        }
        if (mtl.opacity != 1.f) {
            result.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, mtl.opacity));
        }

        ObjectMap<String, Texture> textures = new ObjectMap<>();

        // TODO uvScaling/uvTranslation totally ignored
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

                var descriptor = new TextureDescriptor<>(texture);
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
        disposables.add(disposable);
    }

    /**
     * @return the {@link Disposable} objects that will be disposed when the {@link #dispose()} method is called.
     */
    public Iterable<Disposable> getManagedDisposables() {
        return disposables;
    }

    @Override
    public void dispose() {
        disposables.forEach(Disposable::dispose);
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
        for (Node node : nodes) {
            node.calculateTransforms(true);
        }
        for (Node node : nodes) {
            node.calculateBoneTransforms(true);
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
        for (Node node : nodes) {
            node.extendBoundingBox(out);
        }
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
        Animation animation;
        if (ignoreCase) {
            for (Animation value : animations) {
                if ((animation = value).id.equalsIgnoreCase(id)) {
                    return animation;
                }
            }
        } else {
            for (Animation value : animations) {
                if ((animation = value).id.equals(id)) {
                    return animation;
                }
            }
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

    // todo is caching of this result needed?
    public Collection<Mesh> getMeshes() {
        var set = new HashSet<Mesh>();
        getMeshesFromNode(set, nodes);
        return set;
    }

    private void getMeshesFromNode(Collection<Mesh> meshes, Iterable<Node> nodes) {
        nodes.forEach(n -> {
            for (var p : n.parts) {
                meshes.add(p.meshPart.mesh);
            }
            if (n.hasChildren()) {
                getMeshesFromNode(meshes, n.getChildren());
            }
        });
    }
}
