package com.mbrlabs.mundus.commons.loader.gltf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.ObjectSet;
import net.mgsx.gltf.data.GLTF;
import net.mgsx.gltf.data.camera.GLTFCamera;
import net.mgsx.gltf.data.extensions.*;
import net.mgsx.gltf.data.extensions.KHRLightsPunctual.GLTFLight;
import net.mgsx.gltf.data.scene.GLTFNode;
import net.mgsx.gltf.data.scene.GLTFScene;
import net.mgsx.gltf.loaders.exceptions.GLTFUnsupportedException;
import net.mgsx.gltf.loaders.shared.GLTFTypes;
import net.mgsx.gltf.loaders.shared.animation.AnimationLoader;
import net.mgsx.gltf.loaders.shared.data.DataFileResolver;
import net.mgsx.gltf.loaders.shared.data.DataResolver;
import net.mgsx.gltf.loaders.shared.geometry.MeshLoader;
import net.mgsx.gltf.loaders.shared.material.MaterialLoader;
import net.mgsx.gltf.loaders.shared.material.PBRMaterialLoader;
import net.mgsx.gltf.loaders.shared.scene.SkinLoader;
import net.mgsx.gltf.loaders.shared.texture.ImageResolver;
import net.mgsx.gltf.loaders.shared.texture.TextureResolver;
import net.nevinsky.mundus.core.mesh.Mesh;
import net.nevinsky.mundus.core.mesh.MeshPart;
import net.nevinsky.mundus.core.model.Model;
import net.nevinsky.mundus.core.node.Node;
import net.nevinsky.mundus.core.node.NodePart;

import java.util.*;
import java.util.stream.Collectors;

public class GLTFLoaderBase implements Disposable {

    public static final String TAG = "GLTF";

    public final static ObjectSet<String> supportedExtensions = new ObjectSet<>();

    static {
        supportedExtensions.addAll(
                KHRMaterialsPBRSpecularGlossiness.EXT,
                KHRTextureTransform.EXT,
                KHRLightsPunctual.EXT,
                KHRMaterialsUnlit.EXT,
                KHRMaterialsTransmission.EXT,
                KHRMaterialsVolume.EXT,
                KHRMaterialsIOR.EXT,
                KHRMaterialsSpecular.EXT,
                KHRMaterialsIridescence.EXT,
                KHRMaterialsEmissiveStrength.EXT
        );
    }

    private static final Set<Material> materialSet = new HashSet<>();
    private static final Set<MeshPart> meshPartSet = new HashSet<>();
    private static final Set<Mesh> meshSet = new HashSet<>();
    private final Set<Mesh> loadedMeshes = new HashSet<>();

    private final Array<Camera> cameras = new Array<Camera>();
    private final Array<BaseLight> lights = new Array<BaseLight>();

    /**
     * node name to light index
     */
    private final ObjectMap<String, Integer> lightMap = new ObjectMap<String, Integer>();


    /**
     * node name to camera index
     */
    private final ObjectMap<String, Integer> cameraMap = new ObjectMap<String, Integer>();

    private final List<SceneModel> scenes = new ArrayList<>();

    protected GLTF glModel;

    protected DataFileResolver dataFileResolver;
    protected MaterialLoader materialLoader;
    protected TextureResolver textureResolver;
    protected AnimationLoader animationLoader;
    protected DataResolver dataResolver;
    protected SkinLoader skinLoader;
    protected Map<Integer, Node> nodeResolver;
    protected MeshLoader meshLoader;
    protected ImageResolver imageResolver;

    public GLTFLoaderBase() {
        this(null);
    }

    public GLTFLoaderBase(TextureResolver textureResolver) {
        this.textureResolver = textureResolver;
        animationLoader = new AnimationLoader();
        nodeResolver = new HashMap<>();
        meshLoader = new MeshLoader();
        skinLoader = new SkinLoader();
    }

    public SceneAsset load(DataFileResolver dataFileResolver, boolean withData) {
        try {
            this.dataFileResolver = dataFileResolver;

            glModel = dataFileResolver.getRoot();

            // prerequists (mandatory)
            if (glModel.extensionsRequired != null) {
                for (String extension : glModel.extensionsRequired) {
                    if (!supportedExtensions.contains(extension)) {
                        throw new GLTFUnsupportedException("Extension " + extension + " required but not supported");
                    }
                }
            }
            // prerequists (optional)
            if (glModel.extensionsUsed != null) {
                for (String extension : glModel.extensionsUsed) {
                    if (!supportedExtensions.contains(extension)) {
                        Gdx.app.error(TAG, "Extension " + extension + " used but not supported");
                    }
                }
            }

            // load deps from lower to higher

            // images (pixmaps)
            dataResolver = new DataResolver(glModel, dataFileResolver);

            if (textureResolver == null) {
                imageResolver = new ImageResolver(dataFileResolver); // TODO no longer necessary
                imageResolver.load(glModel.images);
                textureResolver = new TextureResolver();
                textureResolver.loadTextures(glModel.textures, glModel.samplers, imageResolver);
                imageResolver.dispose();
            }

            materialLoader = createMaterialLoader(textureResolver);
            materialLoader.loadMaterials(glModel.materials);

            loadCameras();
            loadLights();
            loadScenes();
            //todo
//            animationLoader.load(glModel.animations, nodeResolver, dataResolver);
//            skinLoader.load(glModel.skins, glModel.nodes, nodeResolver, dataResolver);

            // create scene asset
            SceneAsset model = new SceneAsset();
            if (withData) {
                model.data = glModel;
            }
            model.scenes = scenes;
            model.scene = scenes.get(glModel.scene);
            model.maxBones = skinLoader.getMaxBones();
            //todo
//            model.textures = textureResolver.getTextures(new Array<Texture>());
//            model.animations = animationLoader.animations;
//            // XXX don't know where the animation are ...
//            for (SceneModel scene : model.scenes) {
//                scene.getModel().getAnimations().addAll(animationLoader.animations);
//            }
//
//            copy(loadedMeshes, model.meshes = new Array<Mesh>());
            loadedMeshes.clear();

            return model;
        } catch (RuntimeException e) {
            dispose();
            throw e;
        }
    }

    protected MaterialLoader createMaterialLoader(TextureResolver textureResolver) {
        return new PBRMaterialLoader(textureResolver);
    }

    private void loadLights() {
        if (glModel.extensions != null) {
            KHRLightsPunctual.GLTFLights lightExt =
                    glModel.extensions.get(KHRLightsPunctual.GLTFLights.class, KHRLightsPunctual.EXT);
            if (lightExt != null) {
                for (GLTFLight light : lightExt.lights) {
                    lights.add(KHRLightsPunctual.map(light));
                }
            }
        }
    }

    @Override
    public void dispose() {
        if (imageResolver != null) {
            imageResolver.dispose();
        }
        if (textureResolver != null) {
            textureResolver.dispose();
        }
        for (SceneModel scene : scenes) {
            scene.dispose();
        }
        for (Mesh mesh : loadedMeshes) {
            mesh.dispose();
        }
        loadedMeshes.clear();
    }

    private void loadScenes() {
        for (int i = 0; i < glModel.scenes.size; i++) {
            scenes.add(loadScene(glModel.scenes.get(i)));
        }
    }

    private void loadCameras() {
        if (glModel.cameras != null) {
            for (GLTFCamera glCamera : glModel.cameras) {
                cameras.add(GLTFTypes.map(glCamera));
            }
        }
    }

    private SceneModel loadScene(GLTFScene gltfScene) {
        SceneModel sceneModel = new SceneModel(gltfScene.name, new Model());

        // add root nodes
        if (gltfScene.nodes != null) {
            for (int id : gltfScene.nodes) {
                sceneModel.getModel().getNodes().add(getNode(id));
            }
        }
        // add scene cameras (filter from all scenes cameras)
        for (Entry<String, Integer> entry : cameraMap) {
            Node node = sceneModel.getModel().getNode(entry.key, true);
            if (node != null) {
                sceneModel.getCameras().put(node, cameras.get(entry.value));
            }
        }
        // add scene lights (filter from all scenes lights)
        for (Entry<String, Integer> entry : lightMap) {
            Node node = sceneModel.getModel().getNode(entry.key, true);
            if (node != null) {
                sceneModel.getLights().put(node, lights.get(entry.value));
            }
        }

        // collect data references to store in model
        collectData(sceneModel.getModel(), sceneModel.getModel().getNodes());

        loadedMeshes.addAll(meshSet);

        sceneModel.getModel().getMeshes().addAll(meshSet);
        sceneModel.getModel().getMeshParts().putAll(meshPartSet.stream().collect(Collectors.toMap(p -> p.id, p -> p)));
        sceneModel.getModel().getMaterials().putAll(materialSet.stream().collect(Collectors.toMap(p -> p.id, p -> p)));

        meshSet.clear();
        meshPartSet.clear();
        materialSet.clear();

        return sceneModel;
    }

    private void collectData(Model model, Iterable<Node> nodes) {
        for (Node node : nodes) {
            for (NodePart part : node.parts) {
                meshSet.add(part.meshPart.mesh);
                meshPartSet.add(part.meshPart);
                materialSet.add(part.material);
            }
            collectData(model, node.getChildren());
        }
    }

    private static <T> void copy(Set<T> src, List<T> dst) {
        dst.addAll(src);
    }

    private Node getNode(int id) {
        Node node = nodeResolver.get(id);
        if (node == null) {
            node = new NodePlus();
            nodeResolver.put(id, node);

            GLTFNode glNode = glModel.nodes.get(id);

            if (glNode.matrix != null) {
                Matrix4 matrix = new Matrix4(glNode.matrix);
                matrix.getTranslation(node.translation);
                matrix.getScale(node.scale);
                matrix.getRotation(node.rotation, true);
            } else {
                if (glNode.translation != null) {
                    GLTFTypes.map(node.translation, glNode.translation);
                }
                if (glNode.rotation != null) {
                    GLTFTypes.map(node.rotation, glNode.rotation);
                }
                if (glNode.scale != null) {
                    GLTFTypes.map(node.scale, glNode.scale);
                }
            }

            node.id = glNode.name == null ? "glNode " + id : glNode.name;

            if (glNode.children != null) {
                for (int childId : glNode.children) {
                    node.addChild(getNode(childId));
                }
            }

            if (glNode.mesh != null) {
                //todo
//                meshLoader.load(node, glModel.meshes.get(glNode.mesh), dataResolver, materialLoader);
            }

            if (glNode.camera != null) {
                cameraMap.put(node.id, glNode.camera);
            }

            // node extensions
            if (glNode.extensions != null) {
                KHRLightsPunctual.GLTFLightNode nodeLight =
                        glNode.extensions.get(KHRLightsPunctual.GLTFLightNode.class, KHRLightsPunctual.EXT);
                if (nodeLight != null) {
                    lightMap.put(node.id, nodeLight.light);
                }
            }

        }
        return node;
    }
}
