package com.mbrlabs.mundus.editor.ui.modules.outline;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import lombok.Getter;

public class OutlineNode extends Tree.Node<OutlineNode, GameObject, VisTable> {

    private final VisLabel label = new VisLabel();
    @Getter
    private final GameObject value;

    public OutlineNode(String name, BitmapFont font) {
        this((GameObject) null, font);
        label.setText(name);
    }

    public OutlineNode(GameObject value, BitmapFont font) {
        super(new VisTable());
        this.value = value;

        if (font != null) {
//            getActor().add(new VisImage(new FontAwesomeIcon(font, Fa.Companion.getPLUS_SQUARE())));
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
