package net.nevinsky.abyssus.lib.assets.assimp;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Reads the sample model files directly, without Assimp, to get independent expected values for the loader tests.
 */
final class SampleOracles {

    private SampleOracles() {
    }

    static final class ObjStats {
        int triangles;
        final Set<String> groups = new LinkedHashSet<>();
        final Set<String> materials = new LinkedHashSet<>();
        /** Bounds of the vertices that are used by faces (Assimp drops unused vertices). */
        final BoundingBox bounds = new BoundingBox().inf();
    }

    /** Wavefront OBJ: faces are fan triangulated, indices may be negative (relative). */
    static ObjStats parseObj(Path file) {
        var stats = new ObjStats();
        var positions = new ArrayList<float[]>();
        var used = new boolean[0];
        var usedList = new ArrayList<Integer>();
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.strip();
                if (line.startsWith("v ")) {
                    var t = line.split("\\s+");
                    positions.add(new float[]{Float.parseFloat(t[1]), Float.parseFloat(t[2]),
                            Float.parseFloat(t[3])});
                } else if (line.startsWith("f ")) {
                    var t = line.split("\\s+");
                    stats.triangles += Math.max(0, t.length - 1 - 2);
                    for (int i = 1; i < t.length; i++) {
                        int index = Integer.parseInt(t[i].split("/")[0]);
                        usedList.add(index > 0 ? index - 1 : positions.size() + index);
                    }
                } else if (line.startsWith("g ")) {
                    stats.groups.add(line.substring(2).strip());
                } else if (line.startsWith("usemtl ")) {
                    stats.materials.add(line.substring(7).strip());
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        used = new boolean[positions.size()];
        for (int index : usedList) {
            used[index] = true;
        }
        for (int i = 0; i < used.length; i++) {
            if (used[i]) {
                var p = positions.get(i);
                stats.bounds.ext(p[0], p[1], p[2]);
            }
        }
        return stats;
    }

    static final class AcObject {
        String kind;
        String name;
        String texture;
        Vector3 loc = new Vector3();
        int triangles;
        /** Bounds of the vertices in object space, only of the vertices used by polygon surfaces. */
        final BoundingBox bounds = new BoundingBox().inf();
        boolean rotated;
        final List<float[]> vertices = new ArrayList<>();
        final Set<Integer> usedVertices = new LinkedHashSet<>();

        /** @return the bounds in the parent space: Assimp applies loc to the vertices of AC3D objects */
        BoundingBox boundsWithLoc() {
            return new BoundingBox(bounds.min.cpy().add(loc), bounds.max.cpy().add(loc));
        }
    }

    static final class AcStats {
        final List<AcObject> objects = new ArrayList<>();

        int triangles() {
            return objects.stream().mapToInt(o -> o.triangles).sum();
        }

        /** Named objects that are meshes ("poly"). */
        Map<String, AcObject> polys() {
            var res = new LinkedHashMap<String, AcObject>();
            for (var o : objects) {
                if ("poly".equals(o.kind) && o.name != null) {
                    res.put(o.name, o);
                }
            }
            return res;
        }
    }

    /** AC3D: only polygon surfaces (SURF type 0) are triangles, lines are skipped by the loader. */
    static AcStats parseAc(Path file) {
        var stats = new AcStats();
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            AcObject current = null;
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.strip();
                if (line.startsWith("OBJECT ")) {
                    current = new AcObject();
                    current.kind = line.substring(7).strip();
                    stats.objects.add(current);
                } else if (current == null) {
                    continue;
                } else if (line.startsWith("name ")) {
                    current.name = unquote(line.substring(5));
                } else if (line.startsWith("texture ")) {
                    current.texture = unquote(line.substring(8));
                } else if (line.startsWith("loc ")) {
                    var t = line.split("\\s+");
                    current.loc = new Vector3(Float.parseFloat(t[1]), Float.parseFloat(t[2]),
                            Float.parseFloat(t[3]));
                } else if (line.startsWith("rot ")) {
                    var t = line.split("\\s+");
                    float[] identity = {1, 0, 0, 0, 1, 0, 0, 0, 1};
                    for (int i = 0; i < 9; i++) {
                        if (Math.abs(Float.parseFloat(t[i + 1]) - identity[i]) > 1e-6f) {
                            current.rotated = true;
                        }
                    }
                } else if (line.startsWith("numvert ")) {
                    int n = Integer.parseInt(line.substring(8).strip());
                    for (int i = 0; i < n; i++) {
                        var t = reader.readLine().strip().split("\\s+");
                        current.vertices.add(new float[]{Float.parseFloat(t[0]), Float.parseFloat(t[1]),
                                Float.parseFloat(t[2])});
                    }
                } else if (line.startsWith("SURF ")) {
                    int flags = Integer.decode(line.substring(5).strip());
                    int refs = 0;
                    String next;
                    while ((next = reader.readLine()) != null) {
                        next = next.strip();
                        if (next.startsWith("refs ")) {
                            refs = Integer.parseInt(next.substring(5).strip());
                            break;
                        }
                    }
                    for (int i = 0; i < refs; i++) {
                        var index = Integer.parseInt(reader.readLine().strip().split("\\s+")[0]);
                        if ((flags & 0xF) == 0) {
                            current.usedVertices.add(index);
                        }
                    }
                    if ((flags & 0xF) == 0) {
                        current.triangles += Math.max(0, refs - 2);
                    }
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        for (var object : stats.objects) {
            for (int index : object.usedVertices) {
                var v = object.vertices.get(index);
                object.bounds.ext(v[0], v[1], v[2]);
            }
        }
        return stats;
    }

    private static String unquote(String s) {
        s = s.strip();
        return s.startsWith("\"") && s.endsWith("\"") ? s.substring(1, s.length() - 1) : s;
    }
}
