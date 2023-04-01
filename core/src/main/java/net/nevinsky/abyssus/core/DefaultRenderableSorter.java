package net.nevinsky.abyssus.core;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import java.util.Comparator;
import java.util.List;

public class DefaultRenderableSorter implements RenderableSorter, Comparator<Renderable> {
    private Camera camera;
    private final Vector3 tmpV1 = new Vector3();
    private final Vector3 tmpV2 = new Vector3();

    @Override
    public void sort(final Camera camera, final List<Renderable> renderables) {
        this.camera = camera;
        renderables.sort(this);
    }

    private Vector3 getTranslation(Matrix4 worldTransform, Vector3 center, Vector3 output) {
        if (center.isZero())
            worldTransform.getTranslation(output);
        else if (!worldTransform.hasRotationOrScaling())
            worldTransform.getTranslation(output).add(center);
        else
            output.set(center).mul(worldTransform);
        return output;
    }

    @Override
    public int compare(final Renderable o1, final Renderable o2) {
        final boolean b1 = o1.getMaterial().has(BlendingAttribute.Type)
                && ((BlendingAttribute) o1.getMaterial().get(BlendingAttribute.Type)).blended;
        final boolean b2 = o2.getMaterial().has(BlendingAttribute.Type)
                && ((BlendingAttribute) o2.getMaterial().get(BlendingAttribute.Type)).blended;
        if (b1 != b2) return b1 ? 1 : -1;
        // FIXME implement better sorting algorithm
        // final boolean same = o1.shader == o2.shader && o1.mesh == o2.mesh && (o1.lights == null) ==
        // (o2.lights == null) &&
        // o1.material.equals(o2.material);
        getTranslation(o1.getWorldTransform(), o1.getMeshPart().center, tmpV1);
        getTranslation(o2.getWorldTransform(), o2.getMeshPart().center, tmpV2);
        final float dst = (int) (1000f * camera.position.dst2(tmpV1)) - (int) (1000f * camera.position.dst2(tmpV2));
        final int result = dst < 0 ? -1 : (dst > 0 ? 1 : 0);
        return b1 ? -result : result;
    }
}