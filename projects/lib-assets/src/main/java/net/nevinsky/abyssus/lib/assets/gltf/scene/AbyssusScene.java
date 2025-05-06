package net.nevinsky.abyssus.lib.assets.gltf.scene;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import lombok.Getter;
import lombok.Setter;
import net.nevinsky.abyssus.core.model.Model;
import net.nevinsky.abyssus.core.node.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AbyssusScene implements Scene {

    @Getter
    private final List<Model> models = new ArrayList<>();

    @Setter
    @Getter
    private String name;

    @Override
    public void addModel(Model model) {
        models.add(model);
    }

    @Override
    public Map<Node, Camera> getCameras() {
        return null;
    }

    @Override
    public void addCamera(Node node, Camera camera) {

    }

    @Override
    public Map<Node, BaseLight<?>> getLights() {
        return null;
    }

    @Override
    public void addLight(Node node, BaseLight<?> light) {

    }

    @Override
    public void dispose() {

    }
}
