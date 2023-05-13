package com.mbrlabs.mundus.editor.core.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.core.ecs.EcsService;
import com.mbrlabs.mundus.commons.dto.SceneDto;
import com.mbrlabs.mundus.commons.importer.SceneConverter;
import com.mbrlabs.mundus.editor.config.BaseCtxTest;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileReader;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
public class SceneConverterTest extends BaseCtxTest {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private EcsService ecsService;
    private SceneConverter converter;

    @BeforeEach
    @Override
    public void init() {
        super.init();

        converter = new SceneConverter(mapper);
    }

    @Test
    @SneakyThrows
    public void testConvertScene() {
        var sceneJson = getFile("/scene.json");
        var dto = mapper.readValue(IOUtils.readLines(new FileReader(sceneJson)).stream().collect(Collectors.joining()),
                SceneDto.class);
        var scene = new Scene(ecsService.createWorld());
        converter.fillScene(scene, dto);
        var fromDto = converter.convert(scene);
        assertTrue(fromDto != null);
    }

    @SneakyThrows
    protected File getFile(String path) {
        return new File(Objects.requireNonNull(this.getClass().getResource(path)).getFile());
    }
}
