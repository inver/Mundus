package net.nevinsky.mundus.core.node;

import com.badlogic.gdx.graphics.g3d.model.NodeKeyframe;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.List;

public class NodeAnimation {
	/** the Node affected by this animation **/
	public Node node;
	/** the translation keyframes if any (might be null), sorted by time ascending **/
	public List<NodeKeyframe<Vector3>> translation = null;
	/** the rotation keyframes if any (might be null), sorted by time ascending **/
	public List<NodeKeyframe<Quaternion>> rotation = null;
	/** the scaling keyframes if any (might be null), sorted by time ascending **/
	public List<NodeKeyframe<Vector3>> scaling = null;
}