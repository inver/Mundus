package net.nevinsky.abyssus.lib.assets.gltf;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.nevinsky.abyssus.lib.assets.gltf.scene.Scenes;

import java.io.File;

@RequiredArgsConstructor
public class GlTFSaver {

    //    private final GlTFValidator validator;
    private final ObjectMapper mapper;
    private final GlTFBinaryWriter binaryWriter;

    public void save(Scenes scenes, File gltfResultFile) {

    }
}
