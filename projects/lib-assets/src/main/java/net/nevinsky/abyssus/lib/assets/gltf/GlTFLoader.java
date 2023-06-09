package net.nevinsky.abyssus.lib.assets.gltf;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.nevinsky.abyssus.core.model.Model;
import net.nevinsky.abyssus.lib.assets.gltf.dto.GlTFDto;

import java.io.File;

@Slf4j
@RequiredArgsConstructor
public class GlTFLoader {

    private final ObjectMapper mapper;
    private final GlTFConverter converter;

    @SneakyThrows
    public GlTFDto load(File file) {
        var res = mapper.readValue(file, GlTFDto.class);
        return res;
    }

    public Model loadModel(File file) {
        var ts = System.currentTimeMillis();
        log.debug("Start loading gltf model from '{}'", file);
        var gltf = load(file);
        log.debug("  GlTF loaded in {}ms", (System.currentTimeMillis() - ts));
        var res = converter.convert(gltf);
        log.debug("Model from '{}' loaded in {}ms", file, (System.currentTimeMillis() - ts));
        return res;
    }
}
