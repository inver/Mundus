package net.nevinsky.mundus.core.node;

import com.badlogic.gdx.utils.Array;

public class Animation {
    /**
     * the unique id of the animation
     **/
    public String id;
    /**
     * the duration in seconds
     **/
    public float duration;
    /**
     * the animation curves for individual nodes
     **/
    public Array<NodeAnimation> nodeAnimations = new Array<NodeAnimation>();
}
