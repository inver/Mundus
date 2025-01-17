package com.mbrlabs.mundus.commons.loader;

import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMaterial;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNode;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNodePart;
import com.badlogic.gdx.graphics.g3d.model.data.ModelTexture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.model.ImportedModel;
import lombok.extern.slf4j.Slf4j;
import net.nevinsky.abyssus.core.model.Model;
import net.nevinsky.abyssus.core.model.ModelData;
import net.nevinsky.abyssus.core.model.ModelMesh;
import net.nevinsky.abyssus.core.model.ModelMeshPart;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMaterialProperty;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.badlogic.gdx.graphics.g3d.model.data.ModelTexture.USAGE_UNKNOWN;
import static org.lwjgl.assimp.Assimp.aiGetMaterialColor;
import static org.lwjgl.assimp.Assimp.aiGetMaterialTexture;
import static org.lwjgl.assimp.Assimp.aiProcess_CalcTangentSpace;
import static org.lwjgl.assimp.Assimp.aiProcess_FixInfacingNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_GenBoundingBoxes;
import static org.lwjgl.assimp.Assimp.aiProcess_GenNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_GenSmoothNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_JoinIdenticalVertices;
import static org.lwjgl.assimp.Assimp.aiProcess_LimitBoneWeights;
import static org.lwjgl.assimp.Assimp.aiProcess_PreTransformVertices;
import static org.lwjgl.assimp.Assimp.aiProcess_Triangulate;
import static org.lwjgl.assimp.Assimp.aiReturn_SUCCESS;
import static org.lwjgl.assimp.Assimp.aiTextureType_NONE;

@Slf4j
//todo add texture cache
public class AssimpWorker{

    protected static final int FLAGS = aiProcess_GenNormals | aiProcess_GenSmoothNormals |
            aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_FixInfacingNormals |
            aiProcess_CalcTangentSpace | aiProcess_LimitBoneWeights | aiProcess_PreTransformVertices |
            aiProcess_GenBoundingBoxes;

    public ImportedModel importModel(FileHandle handle) {
        var model = loadModel(handle);
        return new ImportedModel(model, handle);
    }

    public Model loadModel(FileHandle fileHandle) {
        ModelData data = loadModelData(fileHandle.name(), fileHandle, FLAGS);
        if (data == null) {
            return null;
        }
        return new Model(data, new ParentBasedTextureProvider(fileHandle));
    }

    public void loadModelAndSaveForAsset(FileHandle from, FileHandle to) {
        var aiScene = Assimp.aiImportFile(from.path(), FLAGS);
        if (aiScene == null) {
            throw new RuntimeException("Error loading model [modelPath: " + from + "]");
        }
        var textures = extractTextures(aiScene);
        textures.forEach(t -> {
            var folder = "./";
            if (t.contains("/")) {
                folder = t.substring(0, t.lastIndexOf("/"));
            }
            var pathForCopy = to.parent().child(folder);
            pathForCopy.mkdirs();
            from.parent().child(t).copyTo(pathForCopy);
        });
        Assimp.aiExportScene(aiScene, "gltf2", to.path(), 0);
    }

    public ModelData loadModelData(String modelId, FileHandle modelPath, int flags) {
        var ts = System.currentTimeMillis();

        var aiScene = Assimp.aiImportFile(modelPath.path(), flags);
        if (aiScene == null) {
            throw new RuntimeException("Error loading model [modelPath: " + modelPath + "]");
        }

        var res = new ModelData();
        res.id = modelId;
        res.materials.addAll(loadMaterials(modelPath.file().getParent(), aiScene));
        loadMeshes(res, aiScene);

        log.debug("Model loaded in {} ms", System.currentTimeMillis() - ts);
        return res;
    }

    private void loadMeshes(ModelData modelData, AIScene aiScene) {
        int numMeshes = aiScene.mNumMeshes();
        var aiMeshes = aiScene.mMeshes();

        for (int i = 0; i < numMeshes; i++) {
            var aiMesh = AIMesh.create(aiMeshes.get(i));
            processMesh(modelData, aiMesh);
        }
    }

    private Set<String> extractTextures(AIScene aiScene) {
        var res = new HashSet<String>();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            var aiTexturePath = AIString.calloc(stack);
            for (int i = 0; i < aiScene.mNumMaterials(); i++) {
                var matAddress = aiScene.mMaterials().get(i);
                log.info("Mat address: {}", matAddress);
                var aiMaterial = AIMaterial.create(matAddress);
                log.debug("Process material {}", i);
                for (var textureType : TextureType.values()) {
                    log.debug("  Process texture {}", textureType);
                    aiGetMaterialTexture(aiMaterial, textureType.getValue(), 0, aiTexturePath, (IntBuffer) null,
                            null, null, null, null, null);
                    var texturePath = aiTexturePath.dataString();
                    if (texturePath.length() > 0) {
                        var path = new File(texturePath).getName().replace("\\", "/").replace("//", "/");
                        res.add(path);
                    }
                }
            }
        }
        return res;
    }

    private Array<ModelMaterial> loadMaterials(String modelDir, AIScene aiScene) {
        int numMaterials = aiScene.mNumMaterials();

        var materialList = new Array<ModelMaterial>();
        for (int i = 0; i < numMaterials; i++) {
            AIMaterial aiMaterial = AIMaterial.create(aiScene.mMaterials().get(i));
            materialList.add(processMaterial(aiMaterial, modelDir));
        }
        return materialList;
    }

    private ModelMaterial processMaterial(AIMaterial aiMaterial, String modelDir) {
        var material = new ModelMaterial();
        material.id = UUID.randomUUID().toString();
        material.textures = new Array<>();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            var color = AIColor4D.create();
            processMaterialColors(aiMaterial, color, material);

            var aiTexturePath = AIString.calloc(stack);
            processMaterialTexture(aiMaterial, modelDir, aiTexturePath, material);

            processMaterialProperty(aiMaterial, material);
            return material;
        }
    }

    private void processMaterialProperty(AIMaterial aiMaterial, ModelMaterial material) {
        for (int i = 0; i < aiMaterial.mNumProperties(); i++) {
            var aiProperty = AIMaterialProperty.create(aiMaterial.mProperties().get(i));
            var type = MaterialPropertyType.of(aiProperty.mKey().dataString());
            if (type == null) {
                log.warn("Unknown material property: {}", aiProperty.mKey().dataString());
                continue;
            }
            switch (type) {
                case NAME:
                    processMaterialNameProperty(aiProperty, material);
                    break;
                case COLOR_DIFFUSE:
                case COLOR_AMBIENT:
                case COLOR_SPECULAR:
                case COLOR_EMISSIVE:
                case COLOR_TRANSPARENT:
                case COLOR_REFLECTIVE:
                    log.debug("Skip parse '{}' property. It parsed in #processMaterialColors method", type);
                    break;
                case OPACITY:
                    processMaterialOpacityProperty(aiProperty, material);
                    break;
                case SHININESS:
                    processShininessOpacityProperty(aiProperty, material);
                    break;
                default: {
                    var sb = new StringBuilder();
                    var buffer = aiProperty.mData();
                    for (int j = 0; j < aiProperty.mDataLength(); j++) {
                        sb.append(new String(new byte[]{buffer.get()}));
                    }
                    log.debug("Unsupported material property with type: {}, texture index:{}, key: {}, value: {}", type,
                            aiProperty.mIndex(), aiProperty.mKey().dataString(), sb);
                }
            }
        }
    }

    private void processShininessOpacityProperty(AIMaterialProperty aiProperty, ModelMaterial material) {
        var buffer = aiProperty.mData();
        material.shininess = buffer.order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    private void processMaterialOpacityProperty(AIMaterialProperty aiProperty, ModelMaterial material) {
        var buffer = aiProperty.mData();
        material.opacity = buffer.order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    private void processMaterialNameProperty(AIMaterialProperty aiProperty, ModelMaterial material) {
        var sb = new StringBuilder();
        var buffer = aiProperty.mData();
        for (int j = 0; j < aiProperty.mDataLength(); j++) {
            sb.append(Character.valueOf((char) buffer.get()));
        }
        material.id = sb.toString();
    }

    private static void processMaterialTexture(AIMaterial aiMaterial, String modelDir, AIString aiTexturePath,
                                               ModelMaterial material) {
        for (var type : TextureType.values()) {
            if (type.getTextureUsage() == USAGE_UNKNOWN) {
                continue;
            }

            var res = aiGetMaterialTexture(aiMaterial, type.getValue(), 0, aiTexturePath, (IntBuffer) null,
                    null, null, null, null, null);
            if (res != aiReturn_SUCCESS) {
                continue;
            }

            var texturePath = aiTexturePath.dataString();
            if (!texturePath.isEmpty()) {
                var texture = new ModelTexture();
                texture.id = UUID.randomUUID().toString();
                texture.usage = type.getTextureUsage();
                texture.fileName = modelDir + File.separator + new File(texturePath).getName().replace("\\", "/");
                material.textures.add(texture);
            }
        }
    }

    private static void processMaterialColors(AIMaterial aiMaterial, AIColor4D color, ModelMaterial material) {
        for (var type : ColorType.values()) {
            int result = aiGetMaterialColor(aiMaterial, type.getValue(), aiTextureType_NONE, 0, color);
            if (result != aiReturn_SUCCESS) {
                continue;
            }
            var matColor = new Color(color.r(), color.g(), color.b(), color.a());
            switch (type) {
                case DIFFUSE:
                    material.diffuse = matColor;
                    continue;
                case AMBIENT:
                    material.ambient = matColor;
                    continue;
                case SPECULAR:
                    material.specular = matColor;
                    continue;
                case EMISSIVE:
                    material.emissive = matColor;
                    continue;
                case REFLECTIVE:
                    material.reflection = matColor;
            }
        }
    }

    private void processMesh(ModelData modelData, AIMesh aiMesh) {
        var mesh = new ModelMesh();
        mesh.id = UUID.randomUUID().toString();
        var node = new ModelNode();
        node.meshId = mesh.id;

        float[] vertices = processVertices(aiMesh);
        float[] textCoords = processTextCoords(aiMesh);
        float[] normals = processNormals(aiMesh);

        mesh.vertices = mergeVerticesData(vertices, textCoords, normals);
        List<VertexAttribute> attributes = new ArrayList<>();
        if (vertices.length > 0) {
            attributes.add(VertexAttribute.Position());
        }
        if (textCoords.length > 0) {
            attributes.add(VertexAttribute.TexCoords(0));
        }
        if (normals.length > 0) {
            attributes.add(VertexAttribute.Normal());
        }
        mesh.attributes = attributes.toArray(new VertexAttribute[]{});
        mesh.parts = new ModelMeshPart[]{processParts(aiMesh)};

        String materialId = modelData.materials.get(aiMesh.mMaterialIndex()).id;
        node.parts = new ModelNodePart[mesh.parts.length];
        for (int i = 0; i < mesh.parts.length; i++) {
            var nodePart = new ModelNodePart();
            nodePart.materialId = materialId;
            nodePart.meshPartId = mesh.parts[i].id;
            node.parts[i] = nodePart;
        }

        modelData.addMesh(mesh);
        modelData.nodes.add(node);
    }

    private float[] mergeVerticesData(float[] vertices, float[] textCoords, float[] normals) {
        if (vertices.length == 0) {
            return new float[]{};
        }

        int dataLength = 3;
        boolean hasTextCoords = false;
        if (textCoords.length > 0) {
            dataLength += 2;
            hasTextCoords = true;
        }

        boolean hasNormals = false;
        if (normals.length > 0) {
            dataLength += 3;
            hasNormals = true;
        }

        float[] merged = new float[vertices.length / 3 * dataLength];
        for (int i = 0; i < vertices.length / 3; i++) {
            merged[i * dataLength] = vertices[i * 3];
            merged[i * dataLength + 1] = vertices[i * 3 + 1];
            merged[i * dataLength + 2] = vertices[i * 3 + 2];

            int shift = 0;
            if (hasTextCoords) {
                shift = 2;
                merged[i * dataLength + 3] = textCoords[i * 2];
                merged[i * dataLength + 4] = textCoords[i * 2 + 1];
            }

            if (hasNormals) {
                merged[i * dataLength + 3 + shift] = normals[i * 3];
                merged[i * dataLength + 4 + shift] = normals[i * 3 + 1];
                merged[i * dataLength + 5 + shift] = normals[i * 3 + 2];
            }
        }
        return merged;
    }

    private ModelMeshPart processParts(AIMesh aiMesh) {
        var faces = aiMesh.mFaces();

        var resIndices = new ArrayList<Integer>();
        while (faces.remaining() > 0) {
            var face = faces.get();
            var indices = face.mIndices();
            while (indices.remaining() > 0) {
                resIndices.add(indices.get());
            }
        }

        var part = new ModelMeshPart();
        part.id = UUID.randomUUID().toString();
        part.indices = new int[resIndices.size()];
        for (int i = 0; i < resIndices.size(); i++) {
            part.indices[i] = resIndices.get(i);
        }
        part.primitiveType = GL20.GL_TRIANGLES;

        var bb = aiMesh.mAABB();
        var min = bb.mMin();
        var max = bb.mMax();
        var boundingBox =
                new BoundingBox(new Vector3(min.x(), min.y(), min.z()), new Vector3(max.x(), max.y(), max.z()));
        part.setBoundingBox(boundingBox);

        return part;
    }

    private float[] processNormals(AIMesh aiMesh) {
        var buffer = aiMesh.mNormals();
        if (buffer == null) {
            return new float[]{};
        }
        float[] data = new float[buffer.remaining() * 3];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D normal = buffer.get();
            data[pos++] = normal.x();
            data[pos++] = normal.y();
            data[pos++] = normal.z();
        }
        return data;
    }

    private float[] processTextCoords(AIMesh aiMesh) {
        AIVector3D.Buffer buffer = aiMesh.mTextureCoords(0);
        if (buffer == null) {
            return new float[]{};
        }
        float[] data = new float[buffer.remaining() * 2];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D textCoord = buffer.get();
            data[pos++] = textCoord.x();
            data[pos++] = 1 - textCoord.y();
        }
        return data;
    }

    private float[] processVertices(AIMesh aiMesh) {
        var buffer = aiMesh.mVertices();
        var data = new float[buffer.remaining() * 3];
        int pos = 0;
        while (buffer.remaining() > 0) {
            var textCoord = buffer.get();
            data[pos++] = textCoord.x();
            data[pos++] = textCoord.y();
            data[pos++] = textCoord.z();
        }
        return data;
    }

}
