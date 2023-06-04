package com.mbrlabs.mundus.editor.core.ecs;

import com.artemis.Component;
import com.mbrlabs.mundus.commons.core.ecs.base.RenderableDelegate;
import com.mbrlabs.mundus.editor.tools.picker.PickerIDAttribute;
import com.mbrlabs.mundus.editor.utils.PickerColorEncoder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PickableComponent extends Component {
    @Getter
    private RenderableDelegate renderable;
    private PickerIDAttribute pickerIdAttribute;

    private PickableComponent(int entityId, RenderableDelegate renderableObjectDelegate) {
        renderable = renderableObjectDelegate;
        pickerIdAttribute = PickerColorEncoder.encodeRaypickColorId(entityId);
    }

    public static PickableComponent of(int entityId, RenderableDelegate renderableObjectDelegate) {
        return new PickableComponent(entityId, renderableObjectDelegate);
    }
}
