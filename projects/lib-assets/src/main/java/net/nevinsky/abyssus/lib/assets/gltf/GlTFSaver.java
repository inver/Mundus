package net.nevinsky.abyssus.lib.assets.gltf;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.nevinsky.abyssus.core.model.Model;

@RequiredArgsConstructor
public class GlTFSaver {

    private final GlTFValidator validator;
    private final ObjectMapper mapper;

    public void writeFiles(Model model) {

    }
}
