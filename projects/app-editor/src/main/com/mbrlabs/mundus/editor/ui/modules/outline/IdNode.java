package com.mbrlabs.mundus.editor.ui.modules.outline;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.editor.ui.widgets.icon.SymbolIcon;
import lombok.Getter;

public class IdNode extends Tree.Node<IdNode, Integer, VisTable> {

    @Getter
    private final VisLabel label = new VisLabel();
    @Getter
    private final Type type;

    public IdNode(int id, String name, SymbolIcon icon, Type type) {
        super(new VisTable());
        this.type = type;
        setValue(id);
        if (icon != null) {
            var label = new VisLabel(icon.getSymbol(), VisUI.getSkin().get("icon", Label.LabelStyle.class));
            label.setFontScale(0.85f);
            getActor().add(label).padRight(4f);
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

        public static final int ROOT_NODE_ID = -100500;
        private final IdNode hierarchy = new IdNode(-1, "Hierarchy", SymbolIcon.HIERARCHY, Type.ROOT);
        private final IdNode shaders = new IdNode(-101, "Shaders", SymbolIcon.SHADER, Type.NONE);
        private final IdNode terrains = new IdNode(-102, "Terrains", SymbolIcon.TERRAIN, Type.NONE);
        private final IdNode materials = new IdNode(-103, "Materials", SymbolIcon.MATERIAL, Type.NONE);
        private final IdNode textures = new IdNode(-104, "Textures", SymbolIcon.TEXTURE, Type.NONE);
        private final IdNode models = new IdNode(-105, "Models", SymbolIcon.MODEL, Type.NONE);
        private final IdNode cameras = new IdNode(-106, "Cameras", SymbolIcon.CAMERA, Type.NONE);
        private final IdNode lights = new IdNode(-107, "Lights", SymbolIcon.LIGHT, Type.NONE);
        private final IdNode skybox = new IdNode(-108, "Skybox", SymbolIcon.SKYBOX, Type.NONE);

        public RootNode() {
            super(ROOT_NODE_ID, "Scene", SymbolIcon.SCENE, Type.ROOT);

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
