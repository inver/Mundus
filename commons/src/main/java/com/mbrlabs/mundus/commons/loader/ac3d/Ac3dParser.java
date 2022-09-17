package com.mbrlabs.mundus.commons.loader.ac3d;

import com.mbrlabs.mundus.commons.dto.ColorDto;
import com.mbrlabs.mundus.commons.dto.Matrix3Dto;
import com.mbrlabs.mundus.commons.dto.Vector2Dto;
import com.mbrlabs.mundus.commons.dto.Vector3Dto;
import com.mbrlabs.mundus.commons.dto.vertex.VertexDto;
import com.mbrlabs.mundus.commons.loader.ac3d.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

@Slf4j
public class Ac3dParser {
    public Ac3dModel parse(BufferedReader br) throws IOException {
        parseHeader(br);

        var res = new Ac3dModel();
        String line;
        while ((line = br.readLine()) != null) {
            if (StringUtils.isBlank(line)) {
                continue;
            }
            if (line.startsWith(Ac3dConstants.MATERIAL_LINE)) {
                res.getMaterials().add(parseMaterialLine(line));
                continue;
            }
            if (line.startsWith(Ac3dConstants.MATERIAL_BLOCK)) {
                res.getMaterials().add(parseMaterialLine(line));
                continue;
            }

            if (line.startsWith(Ac3dConstants.OBJECT)) {
                res.getObjects().add(parseObject(line, br));
            }
        }
        return res;
    }

    private void parseHeader(BufferedReader br) throws IOException {
        String line;
        while (StringUtils.isBlank(line = br.readLine())) {
            //skip
        }
        if (!line.toUpperCase().startsWith(Ac3dConstants.HEADER)) {
            throw new IllegalArgumentException("Wrong file header!");
        }
    }

    public Ac3dMaterial parseMaterialLine(String line) {
        var res = Ac3dMaterial.builder();

        StringTokenizer tokenizer;
        if (line.contains("\"")) {
            tokenizer = new StringTokenizer(line.substring(line.lastIndexOf('"') + 1), " ");
            tokenizer.nextToken();
        } else {
            tokenizer = new StringTokenizer(line, " ");
            tokenizer.nextToken();
            res.name(tokenizer.nextToken());
            tokenizer.nextToken();
        }

        res.diffuse(createColor(tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken()));
        //skip declaration
        tokenizer.nextToken();
        res.ambient(createColor(tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken()));
        //skip declaration
        tokenizer.nextToken();
        res.emissive(createColor(tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken()));
        //skip declaration
        tokenizer.nextToken();
        res.specular(createColor(tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken()));
        //skip declaration
        tokenizer.nextToken();
        res.shininess(Float.parseFloat(tokenizer.nextToken()));
        //skip declaration
        tokenizer.nextToken();
        res.transparency(Float.parseFloat(tokenizer.nextToken()));

        return res.build();
    }

    //todo
    public Ac3dMaterial parseMaterialBlock(String line) {
        throw new NotImplementedException("Parse of material block is not implemented");
//
//
//        var res = Ac3dMaterial.builder();
//
//        StringTokenizer tokenizer;
//        if (line.contains("\"")) {
//            tokenizer = new StringTokenizer(line.substring(line.lastIndexOf('"') + 1), " ");
//            tokenizer.nextToken();
//        } else {
//            tokenizer = new StringTokenizer(line, " ");
//            tokenizer.nextToken();
//            res.name(tokenizer.nextToken());
//            tokenizer.nextToken();
//        }
//
//        res.diffuse(createColor(tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken()));
//        //skip declaration
//        tokenizer.nextToken();
//        res.ambient(createColor(tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken()));
//        //skip declaration
//        tokenizer.nextToken();
//        res.emissive(createColor(tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken()));
//        //skip declaration
//        tokenizer.nextToken();
//        res.specular(createColor(tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken()));
//        //skip declaration
//        tokenizer.nextToken();
//        res.shininess(Float.parseFloat(tokenizer.nextToken()));
//        //skip declaration
//        tokenizer.nextToken();
//        res.transparency(Float.parseFloat(tokenizer.nextToken()));
//
//        return res.build();
    }


    private ColorDto createColor(String red, String green, String blue) {
        return new ColorDto(Float.parseFloat(red), Float.parseFloat(green), Float.parseFloat(blue));
    }

    public Ac3dObject parseObject(String line, BufferedReader br) throws IOException {
        var tokenizer = new StringTokenizer(line, " ");
        //skip OBJECT declaration
        tokenizer.nextToken();
        var token = tokenizer.nextToken();

        var res = Ac3dObject.builder();
        res.type(parseObjectType(token));

        List<Ac3dSurface> surfaces = Collections.emptyList();
        var vertices = new ArrayList<VertexDto>();
        while (!Ac3dConstants.OBJECT_KIDS.equals(token)) {
            line = br.readLine();
            tokenizer = new StringTokenizer(line);
            token = tokenizer.nextToken();

            switch (token) {
                case Ac3dConstants.OBJECT_NAME: {
                    if (line.contains("\"")) {
                        res.name(line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\"")));
                    } else {
                        res.name(tokenizer.nextToken());
                    }
                    break;
                }
                case Ac3dConstants.OBJECT_DATA: {
                    var charCount = Integer.parseInt(tokenizer.nextToken());
                    var data = new ArrayList<String>();
                    while (charCount > 0) {
                        var dataLine = br.readLine();
                        data.add(dataLine);
                        charCount -= dataLine.length();
                    }
                    res.data(data);
                    break;
                }
                case Ac3dConstants.OBJECT_TEXTURE: {
                    tokenizer.nextToken("\"");
                    res.texturePath(tokenizer.nextToken("\""));
                    break;
                }
                case Ac3dConstants.OBJECT_TEXTURE_OFFSET: {
                    res.textureOffset(new Vector2Dto(
                            Float.parseFloat(tokenizer.nextToken()),
                            Float.parseFloat(tokenizer.nextToken())
                    ));
                    break;
                }
                case Ac3dConstants.OBJECT_TEXTURE_REPEAT: {
                    res.textureRepeat(new Vector2Dto(
                            Float.parseFloat(tokenizer.nextToken()),
                            Float.parseFloat(tokenizer.nextToken())
                    ));
                    break;
                }
                case Ac3dConstants.OBJECT_TEXTURE_SUBDIVISION: {
                    res.subdivisionLevel(Integer.parseInt(tokenizer.nextToken()));
                    break;
                }
                case Ac3dConstants.OBJECT_TEXTURE_CREASE: {
                    res.crease(Float.parseFloat(tokenizer.nextToken()));
                    break;
                }
                case Ac3dConstants.OBJECT_ROTATION_SCALE: {
                    //todo rotate matrix, bcz floats follow by columns
                    res.rotation(new Matrix3Dto(
                            Float.parseFloat(tokenizer.nextToken()), Float.parseFloat(tokenizer.nextToken()), Float.parseFloat(tokenizer.nextToken()),
                            Float.parseFloat(tokenizer.nextToken()), Float.parseFloat(tokenizer.nextToken()), Float.parseFloat(tokenizer.nextToken()),
                            Float.parseFloat(tokenizer.nextToken()), Float.parseFloat(tokenizer.nextToken()), Float.parseFloat(tokenizer.nextToken())
                    ));
                    break;
                }
                case Ac3dConstants.OBJECT_TRANSLATION: {
                    res.translation(new Vector3Dto(
                            Float.parseFloat(tokenizer.nextToken()),
                            Float.parseFloat(tokenizer.nextToken()),
                            Float.parseFloat(tokenizer.nextToken())
                    ));
                    break;
                }
                case Ac3dConstants.OBJECT_URL: {
                    res.url(tokenizer.nextToken());
                    break;
                }
                case Ac3dConstants.OBJECT_HIDDEN: {
                    res.hidden(true);
                    break;
                }
                case Ac3dConstants.OBJECT_LOCKED: {
                    res.locked(true);
                    break;
                }
                case Ac3dConstants.OBJECT_FOLDED: {
                    res.folded(true);
                    break;
                }
                case Ac3dConstants.OBJECT_NUMBER_OF_VERTICES: {
                    var count = Integer.parseInt(tokenizer.nextToken());
                    for (int i = 0; i < count; i++) {
                        line = br.readLine();
                        tokenizer = new StringTokenizer(line, " ");
                        vertices.add(new VertexDto(
                                Float.parseFloat(tokenizer.nextToken()),
                                Float.parseFloat(tokenizer.nextToken()),
                                Float.parseFloat(tokenizer.nextToken())
                        ));
                    }
                    res.vertices(vertices);
                    break;
                }
                case Ac3dConstants.OBJECT_NUMBER_OF_SURFACES: {
                    surfaces = parseSurfaces(br, Integer.parseInt(tokenizer.nextToken()));
                    break;
                }
            }
        }

        //post-processing of surfaces
        for (var surface : surfaces) {
            for (var p : surface.getVerticesRefs()) {
                surface.getVertices().add(Pair.of(vertices.get(p.getLeft()), p.getRight()));
            }
        }
        res.surfaces(surfaces);

        var childrenCount = Integer.parseInt(tokenizer.nextToken());
        if (childrenCount > 0) {
            var children = new ArrayList<Ac3dObject>();
            for (int i = 0; i < childrenCount; i++) {
                children.add(parseObject(br.readLine(), br));
            }
            res.children(children);
        }

        return res.build();
    }

    private Ac3dObjectType parseObjectType(String str) {
        if (Ac3dConstants.OBJECT_TYPE_WORLD.equals(str)) {
            return Ac3dObjectType.WORLD;
        }
        if (Ac3dConstants.OBJECT_TYPE_POLY.equals(str)) {
            return Ac3dObjectType.POLY;
        }
        if (Ac3dConstants.OBJECT_TYPE_GROUP.equals(str)) {
            return Ac3dObjectType.GROUP;
        }

        throw new RuntimeException("Wrong OBJECT type " + str);
    }

    private List<Ac3dSurface> parseSurfaces(BufferedReader br, int count) throws IOException {
        var res = new ArrayList<Ac3dSurface>();
        String line;
        for (int i = 0; i < count; i++) {
            var surface = parseSurfaceHeader(br);
            line = br.readLine();
            var tokenizer = new StringTokenizer(line);
            var token = tokenizer.nextToken();
            if (Ac3dConstants.OBJECT_SURFACE_MATERIAL_INDEX.equals(token)) {
                surface.materialIndex(Integer.parseInt(tokenizer.nextToken()));
            }
            line = br.readLine();
            tokenizer = new StringTokenizer(line);
            token = tokenizer.nextToken();

            var vertices = new ArrayList<Pair<Integer, Vector2Dto>>();
            if (Ac3dConstants.OBJECT_SURFACE_NUMBER_OF_VERTICES.equals(token)) {
                var verticesCount = Integer.parseInt(tokenizer.nextToken());
                for (int j = 0; j < verticesCount; j++) {
                    line = br.readLine();
                    tokenizer = new StringTokenizer(line);

                    vertices.add(Pair.of(
                            Integer.parseInt(tokenizer.nextToken()),
                            new Vector2Dto(
                                    Float.parseFloat(tokenizer.nextToken()),
                                    Float.parseFloat(tokenizer.nextToken())
                            )
                    ));
                }
            }
            surface.verticesRefs(vertices);
            res.add(surface.build());
        }
        return res;
    }

    public Ac3dSurface.Ac3dSurfaceBuilder parseSurfaceHeader(final BufferedReader reader) throws IOException {
        var line = reader.readLine();
        var tokenizer = new StringTokenizer(line, " ");
        // skip SURF declaration
        tokenizer.nextToken();
        final int typeAndFlags = Integer.parseInt(tokenizer.nextToken().substring(2), 16);

        var res = Ac3dSurface.builder();
        res.type(parseSurfaceType(typeAndFlags & 0xF));
        res.shaded((typeAndFlags >> 4 & 0x1) == 0x1);
        res.twoSided((typeAndFlags >> 5 & 0x1) == 0x1);
        return res;
    }

    public Ac3dSurface.Type parseSurfaceType(int typeInt) {
        if (typeInt == 0) {
            return Ac3dSurface.Type.POLYGON;
        }
        if (typeInt == 1) {
            return Ac3dSurface.Type.CLOSED_LINE;
        }
        if (typeInt == 2) {
            return Ac3dSurface.Type.LINE;
        }
        throw new IllegalArgumentException("Illegal surface type " + typeInt);
    }
}
