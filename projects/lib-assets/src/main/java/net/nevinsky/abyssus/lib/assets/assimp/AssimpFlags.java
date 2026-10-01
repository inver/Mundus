package net.nevinsky.abyssus.lib.assets.assimp;

import static org.lwjgl.assimp.Assimp.aiProcess_CalcTangentSpace;
import static org.lwjgl.assimp.Assimp.aiProcess_GenBoundingBoxes;
import static org.lwjgl.assimp.Assimp.aiProcess_GenSmoothNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_ImproveCacheLocality;
import static org.lwjgl.assimp.Assimp.aiProcess_JoinIdenticalVertices;
import static org.lwjgl.assimp.Assimp.aiProcess_LimitBoneWeights;
import static org.lwjgl.assimp.Assimp.aiProcess_SortByPType;
import static org.lwjgl.assimp.Assimp.aiProcess_Triangulate;

/**
 * Post-processing flags for importing a scene.
 * <p>
 * Deliberately excludes {@code aiProcess_PreTransformVertices} (destroys node hierarchy, bones and animations) and
 * {@code aiProcess_FixInfacingNormals} (inverts normals of open meshes). {@code aiProcess_GenSmoothNormals} only
 * generates normals for meshes that have none.
 */
public final class AssimpFlags {

    public static final int DEFAULT = aiProcess_Triangulate
            | aiProcess_JoinIdenticalVertices
            | aiProcess_SortByPType
            | aiProcess_GenSmoothNormals
            | aiProcess_CalcTangentSpace
            | aiProcess_LimitBoneWeights
            | aiProcess_ImproveCacheLocality
            | aiProcess_GenBoundingBoxes;

    private AssimpFlags() {
    }
}
