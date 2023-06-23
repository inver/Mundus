package net.nevinsky.abyssus.lib.assets.gltf;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nevinsky.abyssus.lib.assets.gltf.converter.GlTFSceneConverter;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFDto;
import net.nevinsky.abyssus.lib.assets.gltf.glb.GlTFBinary;
import net.nevinsky.abyssus.lib.assets.gltf.scene.Scenes;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class GlTFLoader {

//    public final static ObjectSet<String> supportedExtensions = new ObjectSet<String>();
//    ;
//
//    static {
//        supportedExtensions.addAll(
//                KHRMaterialsPBRSpecularGlossiness.EXT,
//                KHRTextureTransform.EXT,
//                KHRLightsPunctual.EXT,
//                KHRMaterialsUnlit.EXT,
//                KHRMaterialsTransmission.EXT,
//                KHRMaterialsVolume.EXT,
//                KHRMaterialsIOR.EXT,
//                KHRMaterialsSpecular.EXT,
//                KHRMaterialsIridescence.EXT,
//                KHRMaterialsEmissiveStrength.EXT
//        );
//    }
//
//    private static final ObjectSet<Material> materialSet = new ObjectSet<Material>();
//    private static final ObjectSet<MeshPart> meshPartSet = new ObjectSet<MeshPart>();
//    private static final ObjectSet<Mesh> meshSet = new ObjectSet<Mesh>();
//    private final ObjectSet<Mesh> loadedMeshes = new ObjectSet<Mesh>();
//
//    private final Array<Camera> cameras = new Array<Camera>();
//    private final Array<BaseLight> lights = new Array<BaseLight>();
//
//    /**
//     * node name to light index
//     */
//    private ObjectMap<String, Integer> lightMap = new ObjectMap<String, Integer>();
//
//
//    /**
//     * node name to camera index
//     */
//    private ObjectMap<String, Integer> cameraMap = new ObjectMap<String, Integer>();
//
//    private Array<SceneModel> scenes = new Array<SceneModel>();
//
//    protected GLTF glModel;
//
//    protected DataFileResolver dataFileResolver;
//    protected MaterialLoader materialLoader;
//    protected TextureResolver textureResolver;
//    protected AnimationLoader animationLoader;
//    protected DataResolver dataResolver;
//    protected SkinLoader skinLoader;
//    protected NodeResolver nodeResolver;
//    protected MeshLoader meshLoader;
//    protected ImageResolver imageResolver;
//
//    public GLTFLoaderBase() {
//        this(null);
//    }

//    public GLTFLoaderBase(TextureResolver textureResolver) {
//        this.textureResolver = textureResolver;
//        animationLoader = new AnimationLoader();
//        nodeResolver = new NodeResolver();
//        meshLoader = new MeshLoader();
//        skinLoader = new SkinLoader();
//    }

    private final List<String> supportedExtensions = Arrays.asList(

    );

    private final GlTFSceneConverter sceneConverter;

    /**
     * Loads gltf data to scenes holder
     *
     * @param scenes  result store in this object
     * @param glTFDto dto represents gltf file
     * @param binary  object represents all binary data of gltf file
     */
    public void load(Scenes scenes, GlTFDto glTFDto, GlTFBinary binary) {
        if (!CollectionUtils.containsAll(supportedExtensions, glTFDto.getExtensionsRequired())) {
            var unsupported = glTFDto.getExtensionsRequired().stream()
                    .filter(ex -> !supportedExtensions.contains(ex))
                    .collect(Collectors.joining("\n"));
            throw new GlTFException("Extensions required, but not supported: \n" + unsupported);
        }

        if (!CollectionUtils.containsAll(supportedExtensions, glTFDto.getExtensionsUsed())) {
            var unsupported = glTFDto.getExtensionsRequired().stream()
                    .filter(ex -> !supportedExtensions.contains(ex))
                    .collect(Collectors.joining("\n"));
            log.warn("Extensions used, but not supported: \n" + unsupported);
        }

        glTFDto.getScenes().forEach(scene -> {
            var res = scenes.createAndAddScene();
            sceneConverter.fillScene(glTFDto, binary, scene, res);
        });
    }

//    public Scenes load(DataFileResolver dataFileResolver, boolean withData) {
//        try {
//            this.dataFileResolver = dataFileResolver;
//
//            glModel = dataFileResolver.getRoot();
//
//            // prerequists (mandatory)
//            if (glModel.extensionsRequired != null) {
//                for (String extension : glModel.extensionsRequired) {
//                    if (!supportedExtensions.contains(extension)) {
//                        throw new GLTFUnsupportedException("Extension " + extension + " required but not supported");
//                    }
//                }
//            }
//            // prerequists (optional)
//            if (glModel.extensionsUsed != null) {
//                for (String extension : glModel.extensionsUsed) {
//                    if (!supportedExtensions.contains(extension)) {
//                        Gdx.app.error(TAG, "Extension " + extension + " used but not supported");
//                    }
//                }
//            }
//
//            // load deps from lower to higher
//
//            // images (pixmaps)
//            dataResolver = new DataResolver(glModel, dataFileResolver);
//
//            if (textureResolver == null) {
//                imageResolver = new ImageResolver(dataFileResolver); // TODO no longer necessary
//                imageResolver.load(glModel.images);
//                textureResolver = new TextureResolver();
//                textureResolver.loadTextures(glModel.textures, glModel.samplers, imageResolver);
//                imageResolver.dispose();
//            }
//
//            materialLoader = createMaterialLoader(textureResolver);
//            materialLoader.loadMaterials(glModel.materials);
//
//            loadCameras();
//            loadLights();
//            loadScenes();
//
//            animationLoader.load(glModel.animations, nodeResolver, dataResolver);
//            skinLoader.load(glModel.skins, glModel.nodes, nodeResolver, dataResolver);
//
//            // create scene asset
//            Scenes model = new Scenes();
//            if (withData) {
//                model.data = glModel;
//            }
//            model.scenes = scenes;
//            model.scene = scenes.get(glModel.scene);
//            model.maxBones = skinLoader.getMaxBones();
//            model.textures = textureResolver.getTextures(new Array<Texture>());
//            model.animations = animationLoader.animations;
//            // XXX don't know where the animation are ...
//            for (SceneModel scene : model.scenes) {
//                scene.model.animations.addAll(animationLoader.animations);
//            }
//
//            copy(loadedMeshes, model.meshes = new Array<Mesh>());
//            loadedMeshes.clear();
//
//            return model;
//        } catch (RuntimeException e) {
//            dispose();
//            throw e;
//        }
//    }
//
//    protected MaterialLoader createMaterialLoader(TextureResolver textureResolver) {
//        return new PBRMaterialLoader(textureResolver);
//    }
//
//    private void loadLights() {
//        if (glModel.extensions != null) {
//            KHRLightsPunctual.GLTFLights lightExt =
//                    glModel.extensions.get(KHRLightsPunctual.GLTFLights.class, KHRLightsPunctual.EXT);
//            if (lightExt != null) {
//                for (GLTFLight light : lightExt.lights) {
//                    lights.add(KHRLightsPunctual.map(light));
//                }
//            }
//        }
//    }
//
//    @Override
//    public void dispose() {
//        if (imageResolver != null) {
//            imageResolver.dispose();
//        }
//        if (textureResolver != null) {
//            textureResolver.dispose();
//        }
//        for (SceneModel scene : scenes) {
//            scene.dispose();
//        }
//        for (Mesh mesh : loadedMeshes) {
//            mesh.dispose();
//        }
//        loadedMeshes.clear();
//    }
//
//    private void loadScenes() {
//        for (int i = 0; i < glModel.scenes.size; i++) {
//            scenes.add(loadScene(glModel.scenes.get(i)));
//        }
//    }
//
//    private void loadCameras() {
//        if (glModel.cameras != null) {
//            for (GLTFCamera glCamera : glModel.cameras) {
//                cameras.add(GLTFTypes.map(glCamera));
//            }
//        }
//    }
//
//    private SceneModel loadScene(GLTFScene gltfScene) {
//        SceneModel sceneModel = new SceneModel();
//        sceneModel.name = gltfScene.name;
//        sceneModel.model = new Model();
//
//        // add root nodes
//        if (gltfScene.nodes != null) {
//            for (int id : gltfScene.nodes) {
//                sceneModel.model.nodes.add(getNode(id));
//            }
//        }
//        // add scene cameras (filter from all scenes cameras)
//        for (Entry<String, Integer> entry : cameraMap) {
//            Node node = sceneModel.model.getNode(entry.key, true);
//            if (node != null) {
//                sceneModel.cameras.put(node, cameras.get(entry.value));
//            }
//        }
//        // add scene lights (filter from all scenes lights)
//        for (Entry<String, Integer> entry : lightMap) {
//            Node node = sceneModel.model.getNode(entry.key, true);
//            if (node != null) {
//                sceneModel.lights.put(node, lights.get(entry.value));
//            }
//        }
//
//        // collect data references to store in model
//        collectData(sceneModel.model, sceneModel.model.nodes);
//
//        loadedMeshes.addAll(meshSet);
//
//        copy(meshSet, sceneModel.model.meshes);
//        copy(meshPartSet, sceneModel.model.meshParts);
//        copy(materialSet, sceneModel.model.materials);
//
//        meshSet.clear();
//        meshPartSet.clear();
//        materialSet.clear();
//
//        return sceneModel;
//    }
//
//    private void collectData(Model model, Iterable<Node> nodes) {
//        for (Node node : nodes) {
//            for (NodePart part : node.parts) {
//                meshSet.add(part.meshPart.mesh);
//                meshPartSet.add(part.meshPart);
//                materialSet.add(part.material);
//            }
//            collectData(model, node.getChildren());
//        }
//    }
//
//    private static <T> void copy(ObjectSet<T> src, Array<T> dst) {
//        for (T e : src) {
//            dst.add(e);
//        }
//    }
//
//    private Node getNode(int id) {
//        Node node = nodeResolver.get(id);
//        if (node == null) {
//            node = new NodePlus();
//            nodeResolver.put(id, node);
//
//            GLTFNode glNode = glModel.nodes.get(id);
//
//            if (glNode.matrix != null) {
//                Matrix4 matrix = new Matrix4(glNode.matrix);
//                matrix.getTranslation(node.translation);
//                matrix.getScale(node.scale);
//                matrix.getRotation(node.rotation, true);
//            } else {
//                if (glNode.translation != null) {
//                    GLTFTypes.map(node.translation, glNode.translation);
//                }
//                if (glNode.rotation != null) {
//                    GLTFTypes.map(node.rotation, glNode.rotation);
//                }
//                if (glNode.scale != null) {
//                    GLTFTypes.map(node.scale, glNode.scale);
//                }
//            }
//
//            node.id = glNode.name == null ? "glNode " + id : glNode.name;
//
//            if (glNode.children != null) {
//                for (int childId : glNode.children) {
//                    node.addChild(getNode(childId));
//                }
//            }
//
//            if (glNode.mesh != null) {
//                meshLoader.load(node, glModel.meshes.get(glNode.mesh), dataResolver, materialLoader);
//            }
//
//            if (glNode.camera != null) {
//                cameraMap.put(node.id, glNode.camera);
//            }
//
//            // node extensions
//            if (glNode.extensions != null) {
//                KHRLightsPunctual.GLTFLightNode nodeLight =
//                        glNode.extensions.get(KHRLightsPunctual.GLTFLightNode.class, KHRLightsPunctual.EXT);
//                if (nodeLight != null) {
//                    lightMap.put(node.id, nodeLight.light);
//                }
//            }
//
//        }
//        return node;
//    }
}
