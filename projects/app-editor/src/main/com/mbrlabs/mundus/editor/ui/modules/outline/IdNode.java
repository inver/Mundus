package com.mbrlabs.mundus.editor.ui.modules.outline;

import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.editor.ui.widgets.Icons;
import com.mbrlabs.mundus.editor.utils.TextureUtils;
import lombok.Getter;

public class IdNode extends Tree.Node<IdNode, Integer, VisTable> {

    @Getter
    private final VisLabel label = new VisLabel();

    public IdNode(int id, String name, Icons icon) {
        super(new VisTable());
        setValue(id);
        if (icon != null) {
            //todo add icons to cache with asset
            getActor().add(new VisImage(TextureUtils.load(icon.getPath(), 20, 20))).padRight(5f);
        }

        getActor().add(label).expand().fill();
        label.setText(name);
    }

    public IdNode(int id, String name) {
        this(id, name, null);
    }

    public static class RootNode extends IdNode {

        private final IdNode hierarchy = new IdNode(-100, "Hierarchy", Icons.HIERARCHY);
        private final IdNode shaders = new IdNode(-101, "Shaders", Icons.SHADER);
        private final IdNode terrains = new IdNode(-102, "Terrains", Icons.TERRAIN);
        private final IdNode materials = new IdNode(-103, "Materials", Icons.MATERIAL);
        private final IdNode textures = new IdNode(-104, "Textures", Icons.TEXTURE);
        private final IdNode models = new IdNode(-105, "Models", Icons.MODEL);
        private final IdNode cameras = new IdNode(-106, "Cameras", Icons.CAMERA);
        private final IdNode lights = new IdNode(-107, "Lights", Icons.LIGHT);
        private final IdNode skybox = new IdNode(-108, "Skybox", Icons.SKYBOX);

        public RootNode() {
            super(-100500, "Scene", Icons.SCENE);

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
}
