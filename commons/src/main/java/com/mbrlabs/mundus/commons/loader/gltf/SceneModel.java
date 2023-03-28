package com.mbrlabs.mundus.commons.loader.gltf;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.utils.Disposable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.nevinsky.mundus.core.model.Model;
import net.nevinsky.mundus.core.node.Node;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class SceneModel implements Disposable {
    private final String name;
    private final Model model;
    private final Map<Node, Camera> cameras = new HashMap<>();
    private final Map<Node, BaseLight> lights = new HashMap<>();

    @Override
    public void dispose() {
        model.dispose();
    }
}
