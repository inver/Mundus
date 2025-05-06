package net.nevinsky.abyssus.lib.assets.gltf.scene;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;
import net.nevinsky.abyssus.core.mesh.Mesh;
import net.nevinsky.abyssus.core.node.Animation;

import java.util.List;

public interface Scenes extends Disposable {

    List<Scene> getScenes();

    void addScene(Scene scene);

    Scene createAndAddScene();

    Scene getDefaultScene();

    void setDefaultScene(Scene scene);

    List<Animation> getAnimations();

    void addAnimation(Animation animation);

    List<Texture> getTextures();

    void addTexture(Texture texture);

    List<Mesh> getMeshes();

    void addMesh(Mesh mesh);

    @Override
    default void dispose() {
        getScenes().forEach(Scene::dispose);
    }
}
