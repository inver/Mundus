package net.nevinsky.abyssus.lib.assets.assimp;

import com.badlogic.gdx.graphics.g3d.model.data.ModelNode;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.assimp.AIMetaData;
import org.lwjgl.assimp.AIScene;

import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.assimp.Assimp.AI_DOUBLE;
import static org.lwjgl.assimp.Assimp.AI_FLOAT;
import static org.lwjgl.assimp.Assimp.AI_INT32;

/**
 * Brings a scene into the coordinate system of the engine: Y up, X right, Z towards the viewer. Importers like the
 * FBX one only report the axes and the unit of the file in the scene metadata, they do not change the geometry.
 * <p>
 * The correction is applied to the transform of the root node, the vertices stay untouched.
 */
@Slf4j
final class SceneNormalizer {

    private static final float METERS_PER_FBX_UNIT = 0.01f;

    private SceneNormalizer() {
    }

    /**
     * @param convertUnits also scale the scene to meters (FBX: {@code UnitScaleFactor} is in centimeters)
     * @return the matrix that converts the scene, {@code null} if the scene is already in the right system
     */
    static Matrix4 correction(AIScene scene, boolean convertUnits) {
        var meta = readMetaData(scene.mMetaData());

        Matrix4 result = null;
        if (meta.containsKey("UpAxis")) {
            result = axes(
                    meta.getOrDefault("CoordAxis", 0).intValue(), meta.getOrDefault("CoordAxisSign", 1).intValue(),
                    meta.get("UpAxis").intValue(), meta.getOrDefault("UpAxisSign", 1).intValue(),
                    meta.getOrDefault("FrontAxis", 2).intValue(), meta.getOrDefault("FrontAxisSign", 1).intValue());
        }
        if (convertUnits && meta.containsKey("UnitScaleFactor")) {
            float scale = meterScale(meta.get("UnitScaleFactor").doubleValue());
            if (Math.abs(scale - 1f) > 1e-6f) {
                var scaling = new Matrix4().scl(scale);
                result = result == null ? scaling : result.mulLeft(scaling);
            }
        }
        return result;
    }

    /**
     * @return the rotation that maps the axes of a file (right = coord axis, up, front = towards the viewer, each
     * axis 0, 1, 2 for X, Y, Z with a sign of 1 or -1) to X right, Y up, Z towards the viewer; {@code null} for the
     * default Y up system, and for invalid or left handed axes
     */
    static Matrix4 axes(int coord, int coordSign, int up, int upSign, int front, int frontSign) {
        if (!validAxis(coord, coordSign) || !validAxis(up, upSign) || !validAxis(front, frontSign)
                || coord == up || coord == front || up == front) {
            log.warn("Invalid axes in the scene metadata, skip the axes conversion");
            return null;
        }
        var right = axis(coord, coordSign);
        var upVector = axis(up, upSign);
        var frontVector = axis(front, frontSign);
        if (right.cpy().crs(upVector).dot(frontVector) < 0f) {
            log.warn("The scene has a left handed coordinate system, skip the axes conversion");
            return null;
        }
        if (coord == 0 && coordSign == 1 && up == 1 && upSign == 1 && front == 2 && frontSign == 1) {
            return null;
        }
        // the rows are the new axes expressed in the file system: p' = (right . p, up . p, front . p)
        var m = new Matrix4();
        m.val[Matrix4.M00] = right.x;
        m.val[Matrix4.M01] = right.y;
        m.val[Matrix4.M02] = right.z;
        m.val[Matrix4.M10] = upVector.x;
        m.val[Matrix4.M11] = upVector.y;
        m.val[Matrix4.M12] = upVector.z;
        m.val[Matrix4.M20] = frontVector.x;
        m.val[Matrix4.M21] = frontVector.y;
        m.val[Matrix4.M22] = frontVector.z;
        return m;
    }

    /** @return meters per unit for the FBX {@code UnitScaleFactor}, which is in centimeters */
    static float meterScale(double unitScaleFactor) {
        return unitScaleFactor > 0 ? (float) unitScaleFactor * METERS_PER_FBX_UNIT : 1f;
    }

    /** Multiplies the local transform of the node by the correction. */
    static void apply(ModelNode root, Matrix4 correction) {
        if (correction == null) {
            return;
        }
        var local = new Matrix4().set(root.translation != null ? root.translation : new Vector3(),
                root.rotation != null ? root.rotation : new Quaternion(),
                root.scale != null ? root.scale : new Vector3(1f, 1f, 1f));
        local.mulLeft(correction);

        var translation = local.getTranslation(new Vector3());
        var rotation = local.getRotation(new Quaternion(), true);
        var scale = local.getScale(new Vector3());
        root.translation = translation.isZero(1e-9f) ? null : translation;
        root.rotation = rotation.isIdentity(1e-6f) ? null : rotation;
        root.scale = scale.epsilonEquals(1f, 1f, 1f, 1e-6f) ? null : scale;
    }

    private static boolean validAxis(int axis, int sign) {
        return axis >= 0 && axis <= 2 && (sign == 1 || sign == -1);
    }

    private static Vector3 axis(int axis, int sign) {
        var v = new Vector3();
        v.setZero();
        switch (axis) {
            case 0:
                v.x = sign;
                break;
            case 1:
                v.y = sign;
                break;
            default:
                v.z = sign;
        }
        return v;
    }

    /** Reads the numeric metadata entries (int, float and double) by key. */
    private static Map<String, Number> readMetaData(AIMetaData meta) {
        var res = new HashMap<String, Number>();
        if (meta == null) {
            return res;
        }
        for (int i = 0; i < meta.mNumProperties(); i++) {
            var key = meta.mKeys().get(i).dataString();
            var entry = meta.mValues().get(i);
            var data = entry.mData(8).order(ByteOrder.nativeOrder());
            switch (entry.mType()) {
                case AI_INT32:
                    res.put(key, data.getInt(0));
                    break;
                case AI_FLOAT:
                    res.put(key, data.getFloat(0));
                    break;
                case AI_DOUBLE:
                    res.put(key, data.getDouble(0));
                    break;
                default:
                    break;
            }
        }
        return res;
    }
}
