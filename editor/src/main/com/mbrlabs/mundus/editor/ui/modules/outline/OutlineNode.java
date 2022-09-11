package com.mbrlabs.mundus.editor.ui.modules.outline;

import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.editor.utils.TextureUtils;
import lombok.Getter;

public class OutlineNode extends Tree.Node<OutlineNode, GameObject, VisTable> {

    private final VisLabel label = new VisLabel();
    @Getter
    private final GameObject value;

    public OutlineNode(String name, String iconPath) {
        this((GameObject) null, iconPath);
        label.setText(name);
    }

    public OutlineNode(GameObject value, String iconPath) {
        super(new VisTable());
        this.value = value;


        if (iconPath != null) {
            getActor().add(new VisImage(TextureUtils.load(iconPath, 20, 20))).padRight(5f);
        }
        getActor().add(label).expand().fill();
        if (value != null) {
            label.setText(value.name);
        }
    }

    public VisLabel getLabel() {
        return label;
    }
}
