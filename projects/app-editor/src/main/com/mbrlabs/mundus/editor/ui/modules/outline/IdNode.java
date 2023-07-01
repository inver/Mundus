package com.mbrlabs.mundus.editor.ui.modules.outline;

import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.editor.ui.widgets.Icons;
import com.mbrlabs.mundus.editor.utils.TextureUtils;
import lombok.Getter;
import lombok.ToString;

@ToString(of = {"value", "label"})
public class IdNode extends Tree.Node<IdNode, Integer, VisTable> {

    @Getter
    private final VisLabel label = new VisLabel();
    @Getter
    private final Type type;

    public IdNode(int id, String name, Icons icon, Type type) {
        super(new VisTable());
        this.type = type;
        setValue(id);
        if (icon != null) {
            //todo add icons to cache with asset
            getActor().add(new VisImage(TextureUtils.load(icon.getPath(), 20, 20))).padRight(5f);
        }

        getActor().add(label).expand().fill();
        label.setText(name);
    }

    public IdNode(int id, String name, Type type) {
        this(id, name, null, type);
    }

    public void setName(String name) {
        label.setText(name);
    }

    public static class RootNode extends IdNode {

        private final IdNode hierarchy = new IdNode(-1, "Hierarchy", Icons.HIERARCHY, Type.ROOT);
        private final IdNode shaders = new IdNode(-101, "Shaders", Icons.SHADER, Type.NONE);
        private final IdNode terrains = new IdNode(-102, "Terrains", Icons.TERRAIN, Type.NONE);
        private final IdNode materials = new IdNode(-103, "Materials", Icons.MATERIAL, Type.NONE);
        private final IdNode textures = new IdNode(-104, "Textures", Icons.TEXTURE, Type.NONE);
        private final IdNode models = new IdNode(-105, "Models", Icons.MODEL, Type.NONE);
        private final IdNode cameras = new IdNode(-106, "Cameras", Icons.CAMERA, Type.NONE);
        private final IdNode lights = new IdNode(-107, "Lights", Icons.LIGHT, Type.NONE);
        private final IdNode skybox = new IdNode(-108, "Skybox", Icons.SKYBOX, Type.NONE);

        public RootNode() {
            super(-100500, "Scene", Icons.SCENE, Type.ROOT);

            add(hierarchy);
            add(models);
            add(terrains);
            add(materials);
            add(textures);
            add(cameras);
            add(shaders);
            add(lights);
            add(skybox);
            skybox.setSelectable(true);
        }

        public IdNode getHierarchy() {
            return hierarchy;
        }

        public IdNode getShaders() {
            return shaders;
        }

        public IdNode getTerrains() {
            return terrains;
        }

        public IdNode getMaterials() {
            return materials;
        }

        public IdNode getTextures() {
            return textures;
        }

        public IdNode getModels() {
            return models;
        }

        public IdNode getSkybox() {
            return skybox;
        }
    }

    public enum Type {
        NONE,
        ROOT,
        OBJECT,
        GROUP,
        CAMERA
    }
}
