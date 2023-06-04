package net.nevinsky.abyssus.core.node;

import com.badlogic.gdx.graphics.g3d.model.NodeKeyframe;
import com.badlogic.gdx.utils.Array;
import net.mgsx.gltf.loaders.shared.animation.Interpolation;
import net.mgsx.gltf.scene3d.model.WeightVector;

public class NodeAnimationHack extends NodeAnimation {
    public Interpolation translationMode;
    public Interpolation rotationMode;
    public Interpolation scalingMode;
    public Interpolation weightsMode;

    public Array<NodeKeyframe<WeightVector>> weights = null;
}
