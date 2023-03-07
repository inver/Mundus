package com.mbrlabs.mundus.commons.loader;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.model.data.*;
import com.badlogic.gdx.utils.Array;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.UUID;

import static com.badlogic.gdx.graphics.g3d.model.data.ModelTexture.USAGE_DIFFUSE;
import static org.lwjgl.assimp.Assimp.*;

//todo add texture cache
public class AssimpModelLoader {


    public ModelData loadModel(String modelId, FileHandle modelPath) {
        return loadModel(modelId, modelPath, aiProcess_GenNormals | aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices |
                aiProcess_Triangulate | aiProcess_FixInfacingNormals | aiProcess_CalcTangentSpace | aiProcess_LimitBoneWeights |
                aiProcess_PreTransformVertices);

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

        var res = new ModelData();
        res.materials.addAll(loadMaterials(modelDir, aiScene));
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
                texture.id = "0";
                texture.usage = USAGE_DIFFUSE;
                texture.fileName = modelDir + File.separator + new File(texturePath).getName();
                material.textures.add(texture);
//                material.diffuse = Color.WHITE;
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

        float[] merged = new float[vertices.length / 3 * 8];
        for (int i = 0; i < vertices.length / 3; i++) {
            merged[i * 8] = vertices[i * 3];
            merged[i * 8 + 1] = vertices[i * 3 + 1];
            merged[i * 8 + 2] = vertices[i * 3 + 2];
            merged[i * 8 + 3] = textCoords[i * 2];
            merged[i * 8 + 4] = textCoords[i * 2 + 1];
            merged[i * 8 + 5] = normals[i * 3];
            merged[i * 8 + 6] = normals[i * 3 + 1];
            merged[i * 8 + 7] = normals[i * 3 + 2];
        }
        mesh.vertices = merged;
        mesh.attributes = new VertexAttribute[]{VertexAttribute.Position(), VertexAttribute.TexCoords(1), VertexAttribute.Normal()};
        mesh.parts = processParts(aiMesh);

        node.parts = new ModelNodePart[mesh.parts.length];
        for (int i = 0; i < mesh.parts.length; i++) {
            var nodePart = new ModelNodePart();
            nodePart.materialId = modelData.materials.get(aiMesh.mMaterialIndex()).id;
            nodePart.meshPartId = mesh.parts[i].id;
            node.parts[i] = nodePart;
        }

        modelData.meshes.add(mesh);
        modelData.nodes.add(node);
    }

    private ModelMeshPart[] processParts(AIMesh aiMesh) {
        var faces = aiMesh.mFaces();

        var res = new ModelMeshPart[faces.remaining()];
        int pos = 0;
        while (faces.remaining() > 0) {
            var face = faces.get();
            var indices = face.mIndices();
            var part = new ModelMeshPart();
            part.id = UUID.randomUUID().toString();

            var resIndices = new ArrayList<Short>();
            while (indices.remaining() > 0) {
                resIndices.add(Integer.valueOf(indices.get()).shortValue());
            }
            part.indices = new short[resIndices.size()];
//            part.indices = resIndices.stream();
            for (int i = 0; i < resIndices.size(); i++) {
                part.indices[i] = resIndices.get(i);
            }
            if (face.mNumIndices() > 3) {
                part.primitiveType = GL20.GL_TRIANGLE_FAN;
            } else {
                part.primitiveType = GL20.GL_TRIANGLES;
            }
            res[pos++] = part;
        }

        return res;
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
