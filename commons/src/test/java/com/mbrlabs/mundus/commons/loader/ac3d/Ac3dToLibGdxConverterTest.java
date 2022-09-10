package com.mbrlabs.mundus.commons.loader.ac3d;

import com.badlogic.gdx.graphics.GL20;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Ac3dToLibGdxConverterTest {
    private final Ac3dToLibGdxConverter converter = new Ac3dToLibGdxConverter();
    private final Ac3dParser parser = new Ac3dParser();

    @SneakyThrows
    @Test
    public void testConvert1() {
        var f = new File(this.getClass().getResource("/ac3d/shadow.ac").getFile());
        var model = parser.parse(new BufferedReader(new FileReader(f)));
        var res = converter.convert(model);
        Assert.assertEquals(1, res.meshes.size);
    }

    @SneakyThrows
    @Test
    public void testConvert2() {
        var f = new File(this.getClass().getResource("/ac3d/sr22.ac").getFile());
        var model = parser.parse(new BufferedReader(new FileReader(f)));
        var res = converter.convert(model);
        Assert.assertEquals(37, res.meshes.size);
        for (var mesh : res.meshes) {
            for (var part : mesh.parts) {
                if (part.indices.length <= 3) {
                    Assert.assertEquals(GL20.GL_TRIANGLES, part.primitiveType);
                } else {
                    Assert.assertEquals(GL20.GL_TRIANGLE_FAN, part.primitiveType);
                }
            }
        }
    }
}
