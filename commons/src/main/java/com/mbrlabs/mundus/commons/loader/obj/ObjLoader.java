package com.mbrlabs.mundus.commons.loader.obj;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.model.data.*;
import com.mbrlabs.mundus.commons.dto.Vector2Dto;
import com.mbrlabs.mundus.commons.dto.Vector3Dto;
import com.mbrlabs.mundus.commons.loader.obj.material.ObjMaterialLoader;
import com.mbrlabs.mundus.commons.loader.obj.material.ObjModelMaterial;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ObjLoader extends ModelLoader<ObjLoader.ObjLoaderParameters> {
    private final static String OBJ_VERTEX_TEXTURE = "vt";
    private final static String OBJ_VERTEX_NORMAL = "vn";
    private final static String OBJ_VERTEX = "v";
    private final static String OBJ_FACE = "f";
    private final static String OBJ_GROUP_NAME = "g";
    private final static String OBJ_OBJECT_NAME = "o";
    private final static String OBJ_SMOOTHING_GROUP = "s";
    private final static String OBJ_POINT = "p";
    private final static String OBJ_LINE = "l";
    private final static String OBJ_MAPLIB = "maplib";
    private final static String OBJ_USEMAP = "usemap";
    private final static String OBJ_MTLLIB = "mtllib";
    private final static String OBJ_USEMTL = "usemtl";

    private final ObjMaterialLoader materialLoader;
    private final boolean failedOnUnknown;


    public ObjLoader(FileHandleResolver resolver, ObjMaterialLoader materialLoader) {
        this(resolver, materialLoader, false);
    }

    public ObjLoader(FileHandleResolver resolver, ObjMaterialLoader materialLoader, boolean failedOnUnknown) {
        super(resolver);
        this.materialLoader = materialLoader;
        this.failedOnUnknown = failedOnUnknown;
    }

    @Override
    public ModelData loadModelData(FileHandle fileHandle, ObjLoaderParameters parameters) {
        var ctx = new ParseCtx();
        try (var bufferedReader = new BufferedReader(new InputStreamReader(fileHandle.read()))) {
            var lineCount = 0;
            String line;
            String[] tokens;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();

                if (line.length() == 0 || line.startsWith("#")) {
                    continue;
                }
                tokens = line.split("\\s+");
                final String key = tokens[0].toLowerCase();

                switch (key) {
                    //today, object and group are similar for our, but it is not correct
                    case OBJ_OBJECT_NAME:
                    case OBJ_GROUP_NAME:
                        processGroupNames(tokens, ctx);
                        break;
                    case OBJ_SMOOTHING_GROUP:
                        log.warn("I don't know how to do smoothing groups in libgdx");
                        break;
                    case OBJ_VERTEX:
                        processVertex(line, ctx);
                        break;
                    case OBJ_VERTEX_TEXTURE:
                        processVertexTexture(line, ctx);
                        break;
                    case OBJ_VERTEX_NORMAL:
                        processVertexNormal(line, ctx);
                        break;
                    case OBJ_USEMTL:
                        if (tokens.length < 2) {
                            log.warn("Empty directive " + OBJ_USEMTL);
                            break;
                        }
                        ctx.activeGroup.currentMaterial = tokens[1];
                        break;
                    case OBJ_FACE:
                        processFace(tokens, ctx);
                        break;
                    case OBJ_MTLLIB:
                        processMaterialLib(fileHandle, tokens, ctx);
                        break;
                    default:
                        if (failedOnUnknown) {
                            throw new IllegalStateException("Fail to parse '" + key + "' at line " + lineCount
                                    + " unknown line |" + line + "|");
                        }
                        log.warn("line " + lineCount + " unknown line |" + line + "|");
                        break;
                }
                lineCount++;
            }
        } catch (Exception e) {
            log.error("ERROR", e);
            throw new RuntimeException(e);
        }
        return convert(ctx);
    }

    private static void processVertexTexture(String line, ParseCtx ctx) {
        float[] values = ObjStringUtils.parseFloatList(2, line, OBJ_VERTEX_TEXTURE.length());
        if (values == null) {
            return;
        }
        ctx.vertexTextures.add(new Vector2Dto(values[0], values[1]));
    }

    private static void processVertex(String line, ParseCtx ctx) {
        float[] values = ObjStringUtils.parseFloatList(3, line, OBJ_VERTEX.length());
        if (values == null) {
            return;
        }
        ctx.vertices.add(new Vector3Dto(values[0], values[1], values[2]));
    }

    private void processVertexNormal(String line, ParseCtx ctx) {
        float[] values = ObjStringUtils.parseFloatList(3, line, OBJ_VERTEX_NORMAL.length());
        if (values == null) {
            return;
        }
        ctx.vertexNormals.add(new Vector3Dto(values[0], values[1], values[2]));
    }

    /**
     * Grouping
     * o       group name (g)
     * o       smoothing group (s)
     * o       merging group (mg)
     * o       object name (o)
     */
    private void processGroupNames(String[] tokens, ParseCtx ctx) {
        if (tokens.length < 2) {
            return;
        }
        var names = Arrays.asList(tokens).subList(1, tokens.length);
        for (var name : names) {
            ctx.groups.put(name, new Group(name));
        }
        if (names.size() > 0) {
            String oldMat = null;
            if (ctx.activeGroup != null) {
                oldMat = ctx.activeGroup.currentMaterial;
            }
            ctx.activeGroup = ctx.groups.get(names.get(0));
            ctx.activeGroup.currentMaterial = oldMat;
        }
    }

    private void processMaterialLib(FileHandle fileHandle, String[] tokens, ParseCtx ctx) {
        if (tokens.length < 2) {
            return;
        }

        for (int i = 1; i < tokens.length; i++) {
            for (var m : materialLoader.load(fileHandle.parent().child(tokens[i]).file())) {
                ctx.materials.put(m.id, m);
            }
        }
    }

    // ------------------------------------------------------
    // From the wavefront OBJ file spec;
    // ------------------------------------------------------
    //
    // Referencing groups of vertices
    //
    // Some elements, such as faces and surfaces, may have a triplet of
    // numbers that reference vertex data.These numbers are the reference
    // numbers for a geometric vertex, a texture vertex, and a vertex normal.
    //
    // Each triplet of numbers specifies a geometric vertex, texture vertex,
    // and vertex normal. The reference numbers must be in order and must
    // separated by slashes (/).
    //
    // o       The first reference number is the geometric vertex.
    //
    // o       The second reference number is the texture vertex. It follows
    // 	the first slash.
    //
    // o       The third reference number is the vertex normal. It follows the
    // 	second slash.
    //
    // There is no space between numbers and the slashes. There may be more
    // than one series of geometric vertex/texture vertex/vertex normal
    // numbers on a line.
    //
    // The following is a portion of a sample file for a four-sided face
    // element:
    //
    //     f 1/1/1 2/2/2 3/3/3 4/4/4
    //
    // Using v, vt, and vn to represent geometric vertices, texture vertices,
    // and vertex normals, the statement would read:
    //
    //     f v/vt/vn v/vt/vn v/vt/vn v/vt/vn
    //
    // If there are only vertices and vertex normals for a face element (no
    // texture vertices), you would enter two slashes (//). For example, to
    // specify only the vertex and vertex normal reference numbers, you would
    // enter:
    //
    //     f 1//1 2//2 3//3 4//4
    //
    // When you are using a series of triplets, you must be consistent in the
    // way you reference the vertex data. For example, it is illegal to give
    // vertex normals for some vertices, but not all.
    //
    // The following is an example of an illegal statement.
    //
    //     f 1/1/1 2/2/2 3//3 4//4
    //
    //  ...
    //
    //     f  v1/vt1/vn1   v2/vt2/vn2   v3/vt3/vn3 . . .
    //
    //     Polygonal geometry statement.
    //
    //     Specifies a face element and its vertex reference number. You can
    //     optionally include the texture vertex and vertex normal reference
    //     numbers.
    //
    //     The reference numbers for the vertices, texture vertices, and
    //     vertex normals must be separated by slashes (/). There is no space
    //     between the number and the slash.
    //
    //     v is the reference number for a vertex in the face element. A
    //     minimum of three vertices are required.
    //
    //     vt is an optional argument.
    //
    //     vt is the reference number for a texture vertex in the face
    //     element. It always follows the first slash.
    //
    //     vn is an optional argument.
    //
    //     vn is the reference number for a vertex normal in the face element.
    //     It must always follow the second slash.
    //
    //     Face elements use surface normals to indicate their orientation. If
    //     vertices are ordered counterclockwise around the face, both the
    //     face and the normal will point toward the viewer. If the vertex
    //     ordering is clockwise, both will point away from the viewer. If
    //     vertex normals are assigned, they should point in the general
    //     direction of the surface normal, otherwise unpredictable results
    //     may occur.
    //
    //     If a face has a texture map assigned to it and no texture vertices
    //     are assigned in the f statement, the texture map is ignored when
    //     the element is rendered.
    //
    //     NOTE: Any references to fo (face outline) are no longer valid as of
    //     version 2.11. You can use f (face) to get the same results.
    //     References to fo in existing .obj files will still be read,
    //     however, they will be written out as f when the file is saved.
    private void processFace(String[] tokens, ParseCtx ctx) {
        if (tokens.length < 2) {
            return;
        }

        var face = new Face();
        for (int i = 1; i < tokens.length; i++) {
            face.verticesRefs.add(processFaceVertex(tokens[i], ctx));
        }
        if (ctx.activeGroup != null) {
            ctx.activeGroup.surfaces.add(face);
        }
    }

    private Vertex processFaceVertex(String token, ParseCtx ctx) {
        var res = new Vertex();
        var ids = token.split("/");
        if (StringUtils.isNotBlank(ids[0]) && NumberUtils.isCreatable(ids[0])) {
            res.position = Integer.parseInt(ids[0]) - 1;
        }
        if (ids.length > 1 && StringUtils.isNotBlank(ids[1]) && NumberUtils.isCreatable(ids[1])) {
            res.uv = Integer.parseInt(ids[1]) - 1;
        }
        if (ids.length > 2 && StringUtils.isNotBlank(ids[2]) && NumberUtils.isCreatable(ids[2])) {
            res.normal = Integer.parseInt(ids[2]) - 1;
        }
        return res;
    }

    public ModelData convert(ParseCtx ctx) {
        var res = new ModelData();
        for (var mat : ctx.materials.values()) {
            res.materials.add(mat);
        }
        for (var obj : ctx.groups.values()) {
            convert(obj, res, null, 0, ctx);
        }
        return res;
    }

    public void convert(Group obj, ModelData res, ModelNode parent, int index, ParseCtx ctx) {
        var node = new ModelNode();
        node.id = "node_" + obj.name;
//        if (obj.getTranslation() != null) {
//            node.translation = new Vector3(obj.getTranslation().getX(), obj.getTranslation().getY(), obj.getTranslation().getZ());
//        }
        //todo
//        node.rotation = new Quaternion(obj.getRotation())
//        node.scale
        if (parent == null) {
            res.nodes.add(node);
        } else {
            parent.children[index] = node;
        }

        if (CollectionUtils.isNotEmpty(obj.getSurfaces())) {
            var mesh = new ModelMesh();
            mesh.id = "mesh_" + obj.getName();

            mesh.attributes = new VertexAttribute[]{VertexAttribute.Position()};
            var vertices = obj.getSurfaces().stream().flatMap(f -> f.getVerticesRefs().stream()).collect(Collectors.toList());
            mesh.vertices = new float[vertices.size() * 3];
            for (int i = 0; i < vertices.size(); i++) {
                var vertexIdx = vertices.get(i);
                var vertex = ctx.vertices.get(vertexIdx.position);
                mesh.vertices[i * 3] = vertex.getX();
                mesh.vertices[i * 3 + 1] = vertex.getY();
                mesh.vertices[i * 3 + 2] = vertex.getZ();
            }

            mesh.parts = new ModelMeshPart[obj.getSurfaces().size()];
            node.parts = new ModelNodePart[obj.getSurfaces().size()];
            for (int i = 0; i < obj.getSurfaces().size(); i++) {
                processSurface(obj, node, mesh, i);
            }

            res.meshes.add(mesh);
        }
    }

    private void processSurface(Group obj, ModelNode node, ModelMesh mesh, int i) {
        var surface = obj.getSurfaces().get(i);

        var part = new ModelMeshPart();
        part.id = mesh.id + "_part_" + i;
        part.indices = new short[surface.getVerticesRefs().size()];
        for (int j = 0; j < surface.getVerticesRefs().size(); j++) {
            //todo
//            part.indices[j] = surface.getVerticesRefs().get(j).position.shortValue();
        }
        if (surface.getVerticesRefs().size() > 3) {
            part.primitiveType = GL20.GL_TRIANGLE_FAN;
        } else {
            part.primitiveType = GL20.GL_TRIANGLES;
        }
        mesh.parts[i] = part;

        var nodePart = new ModelNodePart();
        nodePart.meshPartId = part.id;
        nodePart.materialId = obj.currentMaterial;
        node.parts[i] = nodePart;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class ObjLoaderParameters extends ModelLoader.ModelParameters {
        public boolean flipV;
    }

    private static class ParseCtx {
        final Map<String, ObjModelMaterial> materials = new HashMap<>();
        Map<String, Group> groups = new HashMap<>();
        Group activeGroup = null;
        List<Vector3Dto> vertices = new ArrayList<>();
        List<Vector2Dto> vertexTextures = new ArrayList<>();
        List<Vector3Dto> vertexNormals = new ArrayList<>();
    }

    @Getter
    @RequiredArgsConstructor
    private static class Group {
        final String name;

        final List<Face> surfaces = new ArrayList<>();
        boolean hasNormals = false;
        boolean hasUVs = false;
        String currentMaterial;
    }

    @Getter
    private static class Face {
        final List<Vertex> verticesRefs = new ArrayList<>();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    private static class Vertex {
        Integer position = -1;
        int uv = -1;
        int normal = -1;
    }
}
