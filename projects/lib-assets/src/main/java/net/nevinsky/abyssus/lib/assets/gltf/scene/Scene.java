package net.nevinsky.abyssus.lib.assets.gltf.scene;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.utils.Disposable;
import net.nevinsky.abyssus.core.model.Model;
import net.nevinsky.abyssus.core.node.Node;

import java.util.List;
import java.util.Map;

public interface Scene extends Disposable {

    String getName();

    void setName(String name);

    List<Model> getModels();

    void addModel(Model model);

    Map<Node, Camera> getCameras();

    void addCamera(Node node, Camera camera);

    Map<Node, BaseLight<?>> getLights();

    void addLight(Node node, BaseLight<?> light);

    @Override
    default void dispose() {
        getModels().forEach(Model::dispose);
    }
}
