package com.mbrlabs.mundus.editor.ui.modules.outline;

import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import lombok.Getter;

public class OutlineNode extends Tree.Node<OutlineNode, GameObject, VisTable> {

    public static OutlineNode ROOT_NODE = new OutlineNode("Root");

    private final VisLabel label = new VisLabel();
    @Getter
    private final GameObject value;


    public OutlineNode(String name) {
        this((GameObject) null);
        label.setText(name);
    }

    public OutlineNode(GameObject value) {
        super(new VisTable());
        this.value = value;

        getActor().add(label).expand().fill();
        if (value != null) {
            label.setText(value.name);
        }
    }

    public VisLabel getLabel() {
        return label;
    }
}
