package com.mbrlabs.mundus.editor.ui.modules.outline;

import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.editor.ui.widgets.Icons;
import com.mbrlabs.mundus.editor.utils.TextureUtils;

public class OutlineNode extends Tree.Node<OutlineNode, GameObject, VisTable> {

    private final VisLabel label = new VisLabel();

    public OutlineNode(String name) {
        this(name, null);
    }

    public OutlineNode(String name, Icons icon) {
        this((GameObject) null, icon);
        label.setText(name);
    }

    public OutlineNode(GameObject value, Icons icon) {
        super(new VisTable());
        setValue(value);

        if (icon != null) {
            //todo add icons to cache with asset
            getActor().add(new VisImage(TextureUtils.load(icon.getPath(), 20, 20))).padRight(5f);
        }
        getActor().add(label).expand().fill();
        if (value != null) {
            label.setText(value.name);
        }
    }

    public VisLabel getLabel() {
        return label;
    }

    public static class RootNode extends OutlineNode {

        private final OutlineNode hierarchy = new OutlineNode("Hierarchy", Icons.HIERARCHY);
        private final OutlineNode shaders = new OutlineNode("Shaders", Icons.SHADER);
        private final OutlineNode terrains = new OutlineNode("Terrains", Icons.TERRAIN);
        private final OutlineNode materials = new OutlineNode("Materials", Icons.MATERIAL);
        private final OutlineNode textures = new OutlineNode("Textures", Icons.TEXTURE);
        private final OutlineNode models = new OutlineNode("Models", Icons.MODEL);

        public RootNode() {
            super("Scene", Icons.SCENE);

            add(hierarchy);
            add(models);
            add(terrains);
            add(materials);
            add(textures);
            add(shaders);
        }

        public OutlineNode getHierarchy() {
            return hierarchy;
        }

        public OutlineNode getShaders() {
            return shaders;
        }

        public OutlineNode getTerrains() {
            return terrains;
        }

        public OutlineNode getMaterials() {
            return materials;
        }

        public OutlineNode getTextures() {
            return textures;
        }

        public OutlineNode getModels() {
            return models;
        }
    }
}
