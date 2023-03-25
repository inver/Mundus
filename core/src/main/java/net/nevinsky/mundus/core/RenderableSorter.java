package net.nevinsky.mundus.core;

import com.badlogic.gdx.graphics.Camera;

import java.util.List;

public interface RenderableSorter {
    /**
     * Sorts the array of {@link Renderable} instances based on some criteria, e.g. material, distance to camera etc.
     *
     * @param renderables the array of renderables to be sorted
     */
    public void sort(Camera camera, List<Renderable> renderables);
}