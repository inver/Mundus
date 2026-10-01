package net.nevinsky.abyssus.lib.assets.assimp;

import com.badlogic.gdx.graphics.g3d.model.data.ModelAnimation;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNodeAnimation;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNodeKeyframe;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import org.lwjgl.assimp.AIAnimation;
import org.lwjgl.assimp.AINodeAnim;
import org.lwjgl.assimp.AIScene;

import java.util.HashSet;
import java.util.Set;

/**
 * Converts Assimp animations into {@link ModelAnimation}s. Key times are converted from ticks to seconds.
 */
final class AnimationProcessor {

    private static final double DEFAULT_TICKS_PER_SECOND = 25.0;

    private final NodeProcessor nodes;
    private final Set<String> usedIds = new HashSet<>();

    AnimationProcessor(NodeProcessor nodes) {
        this.nodes = nodes;
    }

    Array<ModelAnimation> process(AIScene scene) {
        var result = new Array<ModelAnimation>();
        for (int i = 0; i < scene.mNumAnimations(); i++) {
            result.add(convert(AIAnimation.create(scene.mAnimations().get(i)), i));
        }
        return result;
    }

    private ModelAnimation convert(AIAnimation aiAnimation, int index) {
        double tps = aiAnimation.mTicksPerSecond() > 0 ? aiAnimation.mTicksPerSecond() : DEFAULT_TICKS_PER_SECOND;

        var animation = new ModelAnimation();
        animation.id = uniqueId(aiAnimation.mName().dataString(), index);
        animation.nodeAnimations = new Array<>();
        for (int c = 0; c < aiAnimation.mNumChannels(); c++) {
            var channel = AINodeAnim.create(aiAnimation.mChannels().get(c));
            var nodeAnimation = new ModelNodeAnimation();
            nodeAnimation.nodeId = nodes.idForName(channel.mNodeName().dataString());

            var positions = channel.mPositionKeys();
            if (positions != null && channel.mNumPositionKeys() > 0) {
                nodeAnimation.translation = new Array<>();
                for (int k = 0; k < channel.mNumPositionKeys(); k++) {
                    var key = positions.get(k);
                    var v = key.mValue();
                    nodeAnimation.translation.add(keyframe(key.mTime() / tps, new Vector3(v.x(), v.y(), v.z())));
                }
            }
            var rotations = channel.mRotationKeys();
            if (rotations != null && channel.mNumRotationKeys() > 0) {
                nodeAnimation.rotation = new Array<>();
                for (int k = 0; k < channel.mNumRotationKeys(); k++) {
                    var key = rotations.get(k);
                    var q = key.mValue();
                    nodeAnimation.rotation.add(keyframe(key.mTime() / tps, new Quaternion(q.x(), q.y(), q.z(), q.w())));
                }
            }
            var scalings = channel.mScalingKeys();
            if (scalings != null && channel.mNumScalingKeys() > 0) {
                nodeAnimation.scaling = new Array<>();
                for (int k = 0; k < channel.mNumScalingKeys(); k++) {
                    var key = scalings.get(k);
                    var v = key.mValue();
                    nodeAnimation.scaling.add(keyframe(key.mTime() / tps, new Vector3(v.x(), v.y(), v.z())));
                }
            }
            animation.nodeAnimations.add(nodeAnimation);
        }
        return animation;
    }

    private static <T> ModelNodeKeyframe<T> keyframe(double seconds, T value) {
        var keyframe = new ModelNodeKeyframe<T>();
        keyframe.keytime = (float) seconds;
        keyframe.value = value;
        return keyframe;
    }

    private String uniqueId(String name, int index) {
        var base = name == null || name.isEmpty() ? "animation_" + index : name;
        var id = base;
        for (int n = 1; !usedIds.add(id); n++) {
            id = base + "_" + n;
        }
        return id;
    }
}
