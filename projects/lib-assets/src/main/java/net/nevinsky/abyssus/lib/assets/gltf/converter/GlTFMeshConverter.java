package net.nevinsky.abyssus.lib.assets.gltf.converter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import lombok.RequiredArgsConstructor;
import net.nevinsky.abyssus.core.mesh.Mesh;
import net.nevinsky.abyssus.core.mesh.MeshPart;
import net.nevinsky.abyssus.core.node.MorphNode;
import net.nevinsky.abyssus.core.node.MorphNodePart;
import net.nevinsky.abyssus.core.node.Node;
import net.nevinsky.abyssus.core.node.NodePart;
import net.nevinsky.abyssus.core.node.WeightVector;
import net.nevinsky.abyssus.lib.assets.gltf.GlTFException;
import net.nevinsky.abyssus.lib.assets.gltf.attribute.PBRTextureAttribute;
import net.nevinsky.abyssus.lib.assets.gltf.attribute.PBRVertexAttributes;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFAccessorDto;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFDto;
import net.nevinsky.abyssus.lib.assets.gltf.dto.mesh.GlTFMeshDto;
import net.nevinsky.abyssus.lib.assets.gltf.dto.mesh.GlTFMeshPrimitiveDto;
import net.nevinsky.abyssus.lib.assets.gltf.glb.GlTFBinary;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFAccessorDto.Type.SCALAR;
import static net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFAccessorDto.Type.VEC2;
import static net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFAccessorDto.Type.VEC3;
import static net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFAccessorDto.Type.VEC4;

@RequiredArgsConstructor
public class GlTFMeshConverter {
    public void load(GlTFDto root, GlTFBinary binary, Node node, GlTFMeshDto meshDto, Material defaultMat,
                     List<Material> materials) {
        if (CollectionUtils.isEmpty(meshDto.getPrimitives())) {
            return;
        }

//        ((MorphNode) node).setMorphTargetNames(parseBlenderMorphTargets(meshDto));

        var parts = new Array<NodePart>();

        meshDto.getPrimitives().forEach(primitive -> {
            Material material = defaultMat;
            if (primitive.getMaterial() >= 0) {
                material = materials.get(primitive.getMaterial());
            }


            // vertices
            List<VertexAttribute> vertexAttributes = new ArrayList<>();
            List<GlTFAccessorDto> glAccessors = new ArrayList<>();

            List<int[]> bonesIndices = new ArrayList<>();
            List<float[]> bonesWeights = new ArrayList<>();

            boolean hasNormals = false;
            boolean hasTangent = false;

            var result = fillGeneralVertexAttributes(root, binary, primitive, vertexAttributes, glAccessors,
                    bonesIndices, bonesWeights, hasNormals, hasTangent);

//            fillMorphVertexAttributes(root, (MorphNode) node, primitive, vertexAttributes, glAccessors);

            int bSize = bonesIndices.size() * 4;

            Array<VertexAttribute> bonesAttributes = new Array<>();
            for (int b = 0; b < bSize; b++) {
                VertexAttribute boneAttribute = VertexAttribute.BoneWeight(b);
                vertexAttributes.add(boneAttribute);
                bonesAttributes.add(boneAttribute);
            }

            // add missing vertex attributes (normals and tangent)
            boolean computeNormals = false;
            boolean computeTangents = false;
            VertexAttribute normalMapUVs = null;

            final int glPrimitiveType = getPrimitiveMode(primitive.getMode());
            if (primitive.getMode() == GlTFMeshPrimitiveDto.Mode.TRIANGLES || primitive.getMode() == null) {
                if (!result.hasNormals) {
                    vertexAttributes.add(VertexAttribute.Normal());
                    glAccessors.add(null);
                    computeNormals = true;
                }

                if (!result.hasTangent) {
                    // tangent is only needed when normal map is used
                    var normalMap = material.get(PBRTextureAttribute.class, PBRTextureAttribute.NormalTexture);
                    if (normalMap != null) {
                        vertexAttributes.add(new VertexAttribute(VertexAttributes.Usage.Tangent, 4,
                                ShaderProgram.TANGENT_ATTRIBUTE));
                        glAccessors.add(null);
                        computeTangents = true;
                        for (var attribute : vertexAttributes) {
                            if (attribute.usage == VertexAttributes.Usage.TextureCoordinates
                                    && attribute.unit == normalMap.uvIndex) {
                                normalMapUVs = attribute;
                            }
                        }
                        if (normalMapUVs == null) {
                            throw new GlTFException("UVs not found for normal map");
                        }
                    }
                }
            }

            var attributesGroup = new VertexAttributes(vertexAttributes.toArray(new VertexAttribute[]{}));

            int vertexFloats = attributesGroup.vertexSize / 4;

            int maxVertices = glAccessors.stream().map(GlTFAccessorDto::getCount).max(Integer::compareTo).orElse(0);

            float[] vertices = new float[maxVertices * vertexFloats];

            for (int b = 0; b < bSize; b++) {
                VertexAttribute boneAttribute = bonesAttributes.get(b);
                for (int i = 0; i < maxVertices; i++) {
                    vertices[i * vertexFloats + boneAttribute.offset / 4] = bonesIndices.get(b / 4)[i * 4 + b % 4];
                    vertices[i * vertexFloats + boneAttribute.offset / 4 + 1] = bonesWeights.get(b / 4)[i * 4 + b % 4];
                }
            }

            for (int i = 0; i < glAccessors.size(); i++) {
                var glAccessor = glAccessors.get(i);
                var attribute = vertexAttributes.get(i);

                if (glAccessor == null) {
                    continue;
                }

                if (glAccessor.getBufferView() == null) {
                    throw new GlTFException("bufferView is null (mesh compression ?)");
                }

                var glBufferView = root.getBufferViews().get(glAccessor.getBufferView());

                FloatBuffer floatBuffer = binary.getBufferFloat(glBufferView);

                int attributeFloats = glAccessor.getStrideSize() / 4;

                // buffer can be interleaved, so vertex stride may be different from vertex size
                int floatStride = glBufferView.getByteStride() == null
                        ? attributeFloats
                        : glBufferView.getByteStride() / 4;

                for (int j = 0; j < glAccessor.getCount(); j++) {
                    floatBuffer.position(j * floatStride);
                    int vIndex = j * vertexFloats + attribute.offset / 4;
                    floatBuffer.get(vertices, vIndex, attributeFloats);
                }
            }

            // indices
            if (primitive.getIndices() != null) {

                var indicesAccessor = root.getAccessors().get(primitive.getIndices());

                if (indicesAccessor.getType() != SCALAR) {
                    throw new GlTFException("indices accessor must be SCALAR but was " + indicesAccessor.getType());
                }

                int maxIndices = indicesAccessor.getCount();

                var indicesBufferView = root.getBufferViews().get(indicesAccessor.getBufferView());
                switch (indicesAccessor.getComponentType()) {
                    case UNSIGNED_INT: {
                        int[] indices = new int[maxIndices];
                        binary.getBufferInt(indicesBufferView).get(indices);

                        generateParts(node, parts, material, meshDto.getName(), vertices, maxVertices, indices,
                                attributesGroup, glPrimitiveType, computeNormals, computeTangents, normalMapUVs);

                        break;
                    }
                    case UNSIGNED_SHORT:
                    case SHORT: {
                        int[] indices = new int[maxIndices];
                        var shortBuffer = binary.getBufferShort(indicesBufferView);
                        for (int i = 0; i < maxIndices; i++) {
                            indices[i] = shortBuffer.get();
                        }
                        generateParts(node, parts, material, meshDto.getName(), vertices, maxVertices, indices,
                                attributesGroup, glPrimitiveType, computeNormals, computeTangents, normalMapUVs);
                        break;
                    }
                    case UNSIGNED_BYTE: {
                        int[] indices = new int[maxIndices];
                        var byteBuffer = binary.getBufferByte(indicesBufferView);
                        for (int i = 0; i < maxIndices; i++) {
                            indices[i] = byteBuffer.get() & 0xFF;
                        }
                        generateParts(node, parts, material, meshDto.getName(), vertices, maxVertices, indices,
                                attributesGroup, glPrimitiveType, computeNormals, computeTangents, normalMapUVs);
                        break;
                    }
                    default:
                        throw new GlTFException("illegal componentType " + indicesAccessor.getComponentType());
                }

            } else {
                // non indexed mesh
                generateParts(node, parts, material, meshDto.getName(), vertices, maxVertices, null,
                        attributesGroup, glPrimitiveType, computeNormals, computeTangents, normalMapUVs);
            }
        });

//        meshMap.put(meshDto, parts);

        node.parts.addAll(parts);
    }

    private static void fillMorphVertexAttributes(GlTFDto root, MorphNode node, GlTFMeshPrimitiveDto primitive,
                                                  List<VertexAttribute> vertexAttributes,
                                                  List<GlTFAccessorDto> glAccessors) {
        if (primitive.getTargets() != null) {
            node.setWeights(new WeightVector(primitive.getTargets().size()));

            for (int unit = 0; unit < primitive.getTargets().size(); unit++) {
                for (Map.Entry<String, Integer> attribute : primitive.getTargets().get(unit).entrySet()) {
                    String attributeName = attribute.getKey();
                    var accessor = root.getAccessors().get(attribute.getValue());
                    glAccessors.add(accessor);

                    if (attributeName.equals("POSITION")) {
                        if (accessor.getType() != VEC3
                                && accessor.getComponentType() == GlTFAccessorDto.ComponentType.FLOAT) {
                            throw new GlTFException("illegal morph target position attribute format");
                        }
                        vertexAttributes.add(new VertexAttribute(PBRVertexAttributes.Usage.PositionTarget,
                                3, ShaderProgram.POSITION_ATTRIBUTE + unit, unit));
                        continue;
                    }
                    if (attributeName.equals("NORMAL")) {
                        if (accessor.getType() != VEC3
                                && accessor.getComponentType() == GlTFAccessorDto.ComponentType.FLOAT) {
                            throw new GlTFException("illegal morph target normal attribute format");
                        }
                        vertexAttributes.add(new VertexAttribute(PBRVertexAttributes.Usage.NormalTarget,
                                3, ShaderProgram.NORMAL_ATTRIBUTE + unit, unit));
                        continue;
                    }
                    if (attributeName.equals("TANGENT")) {
                        if (accessor.getType() != VEC3
                                && accessor.getComponentType() == GlTFAccessorDto.ComponentType.FLOAT) {
                            throw new GlTFException("illegal morph target tangent attribute format");
                        }
                        vertexAttributes.add(new VertexAttribute(PBRVertexAttributes.Usage.TangentTarget,
                                3, ShaderProgram.TANGENT_ATTRIBUTE + unit, unit));
                        continue;
                    }

                    throw new GlTFException("illegal morph target attribute type " + attributeName);
                }
            }

        }
    }

    private HasNormalsAndTangents fillGeneralVertexAttributes(GlTFDto root, GlTFBinary binary,
                                                              GlTFMeshPrimitiveDto primitive,
                                                              List<VertexAttribute> vertexAttributes,
                                                              List<GlTFAccessorDto> glAccessors,
                                                              List<int[]> bonesIndices, List<float[]> bonesWeights,
                                                              boolean hasNormals, boolean hasTangent) {
        for (var entry : primitive.getAttributes().entrySet()) {
            var attributeName = entry.getKey();
            var accessorId = entry.getValue();

            var accessor = root.getAccessors().get(accessorId);

            if ("POSITION".equals(attributeName)) {
                addPositionAttribute(vertexAttributes, accessor);
                glAccessors.add(accessor);
                continue;
            }

            if ("NORMAL".equals(attributeName)) {
                addNormalAttribute(vertexAttributes, accessor);
                glAccessors.add(accessor);
                hasNormals = true;
                continue;
            }

            if ("TANGENT".equals(attributeName)) {
                addTangentAttribute(vertexAttributes, accessor);
                glAccessors.add(accessor);
                hasTangent = true;
                continue;
            }

            if (StringUtils.startsWith(attributeName, "TEXCOORD_")) {
                addTextCoordAttribute(vertexAttributes, attributeName, accessor);
                glAccessors.add(accessor);
                continue;
            }

            if (StringUtils.startsWith(attributeName, "COLOR_")) {
                addColorAttribute(vertexAttributes, attributeName, accessor);
                glAccessors.add(accessor);
                continue;
            }
            if (StringUtils.startsWith(attributeName, "WEIGHTS_")) {
                addWeightsAttribute(root, binary, bonesWeights, attributeName, accessorId, accessor);
                continue;
            }
            if (StringUtils.startsWith(attributeName, "JOINTS_")) {
                addJointsAttribute(root, binary, bonesIndices, attributeName, accessorId, accessor);
                continue;
            }

            if (StringUtils.startsWith(attributeName, "_")) {
                Gdx.app.error("GLTF", "skip unsupported custom attribute: " + attributeName);
            }

            throw new GlTFException("illegal attribute type " + attributeName);
        }
        return new HasNormalsAndTangents(hasNormals, hasTangent);
    }

    private void addJointsAttribute(GlTFDto root, GlTFBinary binary, List<int[]> bonesIndices, String attributeName,
                                    Integer accessorId, GlTFAccessorDto accessor) {
        if (accessor.getType() != VEC4) {
            throw new GlTFException("illegal joints attribute type: " + accessor.getType());
        }

        int unit = parseAttributeUnit(attributeName);
        if (unit >= bonesIndices.size()) {
            bonesIndices.add(new int[]{});
        }

        if (accessor.getComponentType() == GlTFAccessorDto.ComponentType.UNSIGNED_BYTE) {
            bonesIndices.set(unit, binary.readBufferUByte(root, accessorId));
            return;
        }

        if (accessor.getComponentType() == GlTFAccessorDto.ComponentType.UNSIGNED_SHORT) {
            bonesIndices.set(unit, binary.readBufferUShort(root, accessorId));
            return;
        }

        throw new GlTFException("illegal type for joints: " + accessor.getComponentType());
    }

    private void addWeightsAttribute(GlTFDto root, GlTFBinary binary, List<float[]> bonesWeights, String attributeName,
                                     Integer accessorId, GlTFAccessorDto accessor) {
        if (accessor.getType() != VEC4) {
            throw new GlTFException("illegal weight attribute type: " + accessor.getType());
        }

        int unit = parseAttributeUnit(attributeName);
        if (unit >= bonesWeights.size()) {
            //increases bonesWeight list length
            bonesWeights.add(new float[]{});
        }

        if (accessor.getComponentType() == GlTFAccessorDto.ComponentType.FLOAT) {
            bonesWeights.set(unit, binary.readBufferFloat(root, accessorId));
            return;
        }

        if (accessor.getComponentType() == GlTFAccessorDto.ComponentType.UNSIGNED_SHORT) {
            bonesWeights.set(unit, binary.readBufferUShortAsFloat(root, accessorId));
            return;
        }
        if (accessor.getComponentType() == GlTFAccessorDto.ComponentType.UNSIGNED_BYTE) {
            bonesWeights.set(unit, binary.readBufferUByteAsFloat(root, accessorId));
            return;
        }

        throw new GlTFException("illegal weight attribute type: " + accessor.getComponentType());
    }

    private void addColorAttribute(List<VertexAttribute> vertexAttributes, String attributeName,
                                   GlTFAccessorDto accessor) {
        int unit = parseAttributeUnit(attributeName);
        String alias = unit > 0 ? ShaderProgram.COLOR_ATTRIBUTE + unit : ShaderProgram.COLOR_ATTRIBUTE;

        if (accessor.getType() == VEC4) {
            if (accessor.getComponentType() == GlTFAccessorDto.ComponentType.FLOAT) {
                vertexAttributes.add(new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 4,
                        GL20.GL_FLOAT, false, alias));
                return;
            }

            if (accessor.getComponentType() == GlTFAccessorDto.ComponentType.UNSIGNED_SHORT) {
                vertexAttributes.add(new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 4,
                        GL20.GL_UNSIGNED_SHORT, true, alias));
                return;
            }
            if (accessor.getComponentType() == GlTFAccessorDto.ComponentType.UNSIGNED_BYTE) {
                vertexAttributes.add(new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 4,
                        GL20.GL_UNSIGNED_BYTE, true, alias));
                return;
            }
            throw new GlTFException("illegal color attribute component type: " + accessor.getComponentType());
        }

        if (accessor.getType() == VEC3) {
            if (accessor.getComponentType() == GlTFAccessorDto.ComponentType.FLOAT) {
                vertexAttributes.add(new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 3,
                        GL20.GL_FLOAT, false, alias));
                return;
            }

            if (accessor.getComponentType() == GlTFAccessorDto.ComponentType.UNSIGNED_SHORT) {
                throw new GlTFException("RGB unsigned short color attribute not supported");
            }

            if (accessor.getComponentType() == GlTFAccessorDto.ComponentType.UNSIGNED_BYTE) {
                throw new GlTFException("RGB unsigned byte color attribute not supported");
            }
            throw new GlTFException("illegal color attribute component type: " + accessor.getComponentType());
        }
        throw new GlTFException("illegal color attribute type: " + accessor.getType());
    }

    private void addTextCoordAttribute(List<VertexAttribute> vertexAttributes, String attributeName,
                                       GlTFAccessorDto accessor) {
        if (accessor.getType() != VEC2) {
            throw new GlTFException("illegal texture coordinate attribute type : " + accessor.getType());
        }
        if (accessor.getComponentType() == GlTFAccessorDto.ComponentType.UNSIGNED_BYTE) {
            throw new GlTFException("unsigned byte texture coordinate attribute not supported");
        }
        if (accessor.getComponentType() == GlTFAccessorDto.ComponentType.UNSIGNED_SHORT) {
            throw new GlTFException("unsigned short texture coordinate attribute not supported");
        }
        if (accessor.getComponentType() != GlTFAccessorDto.ComponentType.FLOAT) {
            throw new GlTFException("illegal texture coordinate component type : " + accessor.getComponentType());
        }
        vertexAttributes.add(VertexAttribute.TexCoords(parseAttributeUnit(attributeName)));
    }

    private static void addTangentAttribute(List<VertexAttribute> vertexAttributes, GlTFAccessorDto accessor) {
        if (accessor.getType() != GlTFAccessorDto.Type.VEC4
                && accessor.getComponentType() == GlTFAccessorDto.ComponentType.FLOAT) {
            throw new GlTFException("illegal tangent attribute format");
        }
        vertexAttributes.add(new VertexAttribute(VertexAttributes.Usage.Tangent, 4, ShaderProgram.TANGENT_ATTRIBUTE));
    }

    private static void addNormalAttribute(List<VertexAttribute> vertexAttributes, GlTFAccessorDto accessor) {
        if (accessor.getType() != GlTFAccessorDto.Type.VEC3
                && accessor.getComponentType() == GlTFAccessorDto.ComponentType.FLOAT) {
            throw new GlTFException("illegal position attribute format");
        }
        vertexAttributes.add(VertexAttribute.Normal());
    }

    private static void addPositionAttribute(List<VertexAttribute> vertexAttributes, GlTFAccessorDto accessor) {
        if (accessor.getType() != GlTFAccessorDto.Type.VEC3
                && accessor.getComponentType() == GlTFAccessorDto.ComponentType.FLOAT) {
            throw new GlTFException("illegal normal attribute format");
        }
        vertexAttributes.add(VertexAttribute.Position());
    }

    private void generateParts(Node node, Array<NodePart> parts, Material material, String id, float[] vertices,
                               int vertexCount, int[] indices, VertexAttributes attributesGroup, int glPrimitiveType,
                               boolean computeNormals, boolean computeTangents, VertexAttribute normalMapUVs) {

        // skip empty meshes
        if (vertices.length == 0 || (indices != null && indices.length == 0)) {
            return;
        }

        if (computeNormals || computeTangents) {
            if (computeNormals && computeTangents) {
                Gdx.app.log("GLTF", "compute normals and tangents for primitive " + id);
            } else if (computeTangents) {
                Gdx.app.log("GLTF", "compute tangents for primitive " + id);
            } else {
                Gdx.app.log("GLTF", "compute normals for primitive " + id);
            }
            MeshTangentSpaceGenerator.computeTangentSpace(vertices, indices, attributesGroup, computeNormals,
                    computeTangents, normalMapUVs);
        }

        var mesh = new Mesh(true, vertexCount, indices == null ? 0 : indices.length, attributesGroup);
//        meshes.add(mesh);
        mesh.setVertices(vertices);

        if (indices != null) {
            mesh.setIndices(indices);
        }

        int len = indices == null ? vertexCount : indices.length;

        var meshPart = new MeshPart(id, mesh, 0, len, glPrimitiveType);


        var nodePart = new MorphNodePart();
        //todo
//        nodePart.setMorphTargets(((MorphNode) node).getWeights());
        nodePart.meshPart = meshPart;
        nodePart.material = material;
        parts.add(nodePart);

    }

    private int parseAttributeUnit(String attributeName) {
        int lastUnderscoreIndex = attributeName.lastIndexOf('_');
        try {
            return Integer.parseInt(attributeName.substring(lastUnderscoreIndex + 1));
        } catch (NumberFormatException e) {
            throw new GlTFException("illegal attribute name " + attributeName);
        }
    }

    private int getPrimitiveMode(GlTFMeshPrimitiveDto.Mode glMode) {
        if (glMode == null) {
            return GL20.GL_TRIANGLES;
        }

        return glMode.getValue();
    }

    private List<String> parseBlenderMorphTargets(GlTFMeshDto dto) {
        if (dto.getExtras() == null) {
            return null;
        }

        //todo
        throw new NotImplementedException();
//        JsonValue targetNames = dto.getExtras().value.get("targetNames");
//        if (targetNames != null && targetNames.isArray()) {
//            return new Array<String>(targetNames.asStringArray());
//        }
//        return null;
    }


    @RequiredArgsConstructor
    private static class HasNormalsAndTangents {
        final boolean hasNormals;
        final boolean hasTangent;
    }
}
