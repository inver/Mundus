package com.mbrlabs.mundus.editor.core.ecs;

import com.artemis.Component;
import com.mbrlabs.mundus.editor.tools.picker.PickerIDAttribute;
import com.mbrlabs.mundus.editor.utils.PickerColorEncoder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PickableComponent extends Component {
    private PickerIDAttribute pickerIdAttribute;

    private PickableComponent(int entityId) {
        pickerIdAttribute = PickerColorEncoder.encodeRayPickColorId(entityId);
    }

    public static PickableComponent of(int entityId) {
        return new PickableComponent(entityId);
    }
}
