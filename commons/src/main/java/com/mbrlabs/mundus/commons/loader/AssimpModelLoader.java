package com.mbrlabs.mundus.commons.loader;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMaterial;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNode;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNodePart;
import com.badlogic.gdx.graphics.g3d.model.data.ModelTexture;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.core.AppModelLoader;
import com.mbrlabs.mundus.commons.model.ImportedModel;
import net.nevinsky.mundus.core.model.Model;
import net.nevinsky.mundus.core.model.ModelData;
import net.nevinsky.mundus.core.model.ModelMesh;
import net.nevinsky.mundus.core.model.ModelMeshPart;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.badlogic.gdx.graphics.g3d.model.data.ModelTexture.USAGE_DIFFUSE;
import static org.lwjgl.assimp.Assimp.*;

//todo add texture cache
public class AssimpModelLoader implements AppModelLoader {

    @Override
    public Model loadModel(FileHandle fileHandle) {
        ModelData data = loadModel(fileHandle.name(), fileHandle);
        if (data == null) {
            return null;
        }
        return new Model(data, new ParentBasedTextureProvider(fileHandle));
    }

    @Override
    public ImportedModel importModel(FileHandle handle) {
        var model = loadModel(handle);
        return new ImportedModel(model, handle);
    }

    public ModelData loadModel(String modelId, FileHandle modelPath) {
        return loadModel(modelId, modelPath, aiProcess_GenNormals | aiProcess_GenSmoothNormals |
                aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_FixInfacingNormals |
                aiProcess_CalcTangentSpace | aiProcess_LimitBoneWeights | aiProcess_PreTransformVertices);
    }

    public ModelData loadModel(String modelId, FileHandle modelPath, int flags) {
//        var file = new File(modelPath);
//        if (!file.exists()) {
//            throw new RuntimeException("Model path does not exist [" + modelPath + "]");
//        }
        String modelDir = modelPath.file().getParent();

        AIScene aiScene = aiImportFile(modelPath.path(), flags);
        if (aiScene == null) {
            throw new RuntimeException("Error loading model [modelPath: " + modelPath + "]");
        }

        var res = new ModelData(modelId);
        res.getMaterials().addAll(loadMaterials(modelDir, aiScene));
        loadMeshes(res, aiScene);

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

    private List<ModelMaterial> loadMaterials(String modelDir, AIScene aiScene) {
        int numMaterials = aiScene.mNumMaterials();

        var materialList = new ArrayList<ModelMaterial>();
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

            //todo add more types of colors
            int result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, color);
            if (result == aiReturn_SUCCESS) {
                material.diffuse = new Color(color.r(), color.g(), color.b(), color.a());
            }

            var aiTexturePath = AIString.calloc(stack);
            //todo add more types of textures
            aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, aiTexturePath, (IntBuffer) null,
                    null, null, null, null, null);
            var texturePath = aiTexturePath.dataString();
            if (texturePath.length() > 0) {
                var texture = new ModelTexture();
                texture.id = UUID.randomUUID().toString();
                texture.usage = USAGE_DIFFUSE;
                texture.fileName = modelDir + File.separator + new File(texturePath).getName().replace("\\", "/");
                material.textures.add(texture);
            }

            return material;
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
            attributes.add(VertexAttribute.TexCoords(1));
        }
        if (normals.length > 0) {
            attributes.add(VertexAttribute.Normal());
        }
        mesh.attributes = attributes.toArray(new VertexAttribute[]{});
        mesh.parts = new ModelMeshPart[]{processParts(aiMesh)};

        String materialId = modelData.getMaterials().get(aiMesh.mMaterialIndex()).id;
        node.parts = new ModelNodePart[mesh.parts.length];
        for (int i = 0; i < mesh.parts.length; i++) {
            var nodePart = new ModelNodePart();
            nodePart.materialId = materialId;
            nodePart.meshPartId = mesh.parts[i].id;
            node.parts[i] = nodePart;
        }

        modelData.meshes.add(mesh);
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
