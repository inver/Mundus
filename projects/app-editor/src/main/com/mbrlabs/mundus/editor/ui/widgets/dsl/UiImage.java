package com.mbrlabs.mundus.editor.ui.widgets.dsl;

import com.kotcrab.vis.ui.widget.VisImage;
import lombok.Getter;

@Getter
public class UiImage extends UiComponent<VisImage> {

    private float width;
    private float height;

    public UiImage() {
        super(new VisImage());
    }
    
    @SuppressWarnings("unused")
    public void setWidth(float width) {
        this.width = width;
        actor.setWidth(width);
    }

    @SuppressWarnings("unused")
    public void setHeight(float height) {
        this.height = height;
        actor.setHeight(height);
    }


}
