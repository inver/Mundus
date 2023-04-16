package com.mbrlabs.mundus.commons.importExport;

import net.nevinsky.abyssus.core.model.Model;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.Assimp;

public class AssimpExporter {

    public void export(Model model, String fileName) {
        String format = "gltf";
        var assimpScene = AIScene.create();

        var rootNode = AINode.create();

        var nodeBuffer = PointerBuffer.allocateDirect(model.nodes.size);
        for (var inputNode : model.nodes) {
            var outputNode = AINode.create();

        }
//        nodeBuffer.put()


//        AINode.nmChildren();
        assimpScene.mRootNode(AINode.create());
//        AINode.nmNumMeshes(assimpScene.mRootNode().address(), model.getMeshes().size);

        Assimp.aiExportScene(assimpScene, format, fileName, 0);
    }
}
