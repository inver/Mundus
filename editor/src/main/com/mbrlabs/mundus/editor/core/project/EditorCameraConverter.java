package com.mbrlabs.mundus.editor.core.project;

import com.mbrlabs.mundus.commons.dto.CameraDto;
import com.mbrlabs.mundus.commons.importer.CameraConverter;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.editor.ui.components.camera.EditorCameraComponent;
import com.mbrlabs.mundus.editor.ui.components.camera.CameraService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EditorCameraConverter extends CameraConverter {

    private final CameraService cameraService;

    @Override
    public void addComponents(GameObject go, CameraDto dto) {
        var camera = new EditorCameraComponent(go, fromDto(dto));
        go.addComponent(camera);
//        var handle = new

    }
}
