package net.nevinsky.abyssus.lib.assets.gltf.scene;

import com.badlogic.gdx.graphics.Texture;
import lombok.Getter;
import net.nevinsky.abyssus.core.mesh.Mesh;
import net.nevinsky.abyssus.core.node.Animation;

import java.util.ArrayList;
import java.util.List;

public class AbyssusScenes implements Scenes {
    @Getter
    private final List<Scene> scenes = new ArrayList<>();
    private int defaultScene = -1;

    @Override
    public void addScene(Scene scene) {
        scenes.add(scene);
    }

    @Override
    public Scene createAndAddScene() {
        var res = new AbyssusScene();
        scenes.add(res);
        if (defaultScene < 0) {
            defaultScene = 0;
        }
        return res;
    }

    @Override
    public Scene getDefaultScene() {
        if (defaultScene < 0 || scenes.size() == 0) {
            return null;
        }

        return scenes.get(defaultScene);
    }

    @Override
    public void setDefaultScene(Scene scene) {
        defaultScene = scenes.indexOf(scene);
    }

    @Override
    public List<Animation> getAnimations() {
        return null;
    }

    @Override
    public void addAnimation(Animation animation) {

    }

    @Override
    public List<Texture> getTextures() {
        return null;
    }

    @Override
    public void addTexture(Texture texture) {

    }

    @Override
    public List<Mesh> getMeshes() {
        return null;
    }

    @Override
    public void addMesh(Mesh mesh) {

    }
}
