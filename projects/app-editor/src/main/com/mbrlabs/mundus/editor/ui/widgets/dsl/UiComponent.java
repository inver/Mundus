package com.mbrlabs.mundus.editor.ui.widgets.dsl;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.mbrlabs.mundus.editor.ui.FormStyle;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class UiComponent<T> {

    private static final FormStyle.FormFieldStyle FORM_FIELD_STYLE =
            VisUI.getSkin().get(FormStyle.FormFieldStyle.class);

    protected UiComponent<?> parent;
    protected final T actor;

    private final List<LayoutType> layoutTypes = new ArrayList<>();
    @Setter
    @Getter
    private String id;
    @Setter
    @Getter
    private int colspan = 1;

    private final Map<String, Actor> fields = new HashMap<>();

    public void setLayoutTypes(String types) {
        if (StringUtils.isEmpty(types)) {
            return;
        }

        for (var type : types.split(",")) {
            layoutTypes.add(LayoutType.fromId(type.trim()));
        }
    }

    public void applyStyles(Cell<?> cell) {
        layoutTypes.forEach(type -> type.apply(cell));
    }

    public T getActor() {
        return actor;
    }

    public void setFields(Map<String, Actor> input) {
        fields.putAll(input);
    }

    @SuppressWarnings("unchecked")
    public <T extends Actor> T getField(String id, Class<T> clazz) {
        return (T) getField(id);
    }

    public Actor getField(String id) {
        return fields.get(id);
    }

    @RequiredArgsConstructor
    public enum LayoutType {
        EXPAND_X("expandX", Cell::expandX),

        FILL_X("fillX", Cell::fillX),

        GROW_X("growX", Cell::growX),

        LEFT("left", Cell::left),

        TOP("top", Cell::top),

        FORM("form", cell -> {
            if (cell.getActor() instanceof VisLabel) {
                cell.top().left().padTop(1f).padRight(FORM_FIELD_STYLE.padRight);
            } else {
                cell.left().top().padBottom(FORM_FIELD_STYLE.padBottom);
            }
        });

        private final String id;
        private final Consumer<Cell<?>> action;

        public static LayoutType fromId(String input) {
            for (var type : values()) {
                if (type.id.equalsIgnoreCase(input)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Wrong LayoutType id: " + input);
        }

        public void apply(Cell<?> cell) {
            action.accept(cell);
        }
    }
}
