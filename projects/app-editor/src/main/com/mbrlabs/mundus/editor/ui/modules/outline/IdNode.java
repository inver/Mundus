package com.mbrlabs.mundus.editor.ui.modules.outline;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.mbrlabs.mundus.editor.ui.widgets.icon.SymbolIcon;
import lombok.Getter;

@Getter
public class IdNode extends Tree.Node<IdNode, Integer, VisTable> {

    public static final int ROOT_NODE_SCENE = -100500;
    public static final int ROOT_NODE_ASSET = -100600;
    public static final int NODE_ASSET = -100601;


    private final VisLabel label = new VisLabel();
    private final Type type;
    private final Object data;

    public IdNode(int id, String name, SymbolIcon icon, Type type, Object data) {
        super(new VisTable());
        this.type = type;
        this.data = data;
        setValue(id);
        if (icon != null) {
            var label = new VisLabel(icon.getSymbol(), VisUI.getSkin().get("icon", Label.LabelStyle.class));
            getActor().add(label).padRight(4f);
        }

        getActor().add(label).expand().fill();
        label.setText(name);
    }

    public IdNode(int id, String name, Type type) {
        this(id, name, null, type, null);
    }

    public void setName(String name) {
        label.setText(name);
    }

    public enum Type {
        NONE,
        ROOT,
        OBJECT,
        GROUP,
        CAMERA,
        ASSET
    }
}
