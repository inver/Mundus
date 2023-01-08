package com.mbrlabs.mundus.commons.importer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbrlabs.mundus.commons.assets.Asset;
import com.mbrlabs.mundus.commons.dto.GameObjectDto;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;

public class GameObjectConverterTest {

    private final GameObjectConverter gameObjectConverter = new GameObjectConverter();

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @SneakyThrows
    public void testConvertFromDto() {
        var f = new File(this.getClass().getResource("/importer/go.json").getFile());

        var dto = mapper.readValue(f, GameObjectDto.class);
        var assets = new HashMap<String, Asset<?>>();
        var go = gameObjectConverter.convert(dto, assets);

        Assert.assertNotNull(go);
        Assert.assertEquals(7486, go.getId());
        Assert.assertTrue(go.isActive());
        Assert.assertEquals("Terrain 28", go.name);

    }

}
