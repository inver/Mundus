package com.mbrlabs.mundus.commons.ac3d;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Ac3dParserTest {
    private final Ac3dParser parser = new Ac3dParser();

    @SneakyThrows
    @Test
    public void testParse1() {
        var f = new File(this.getClass().getResource("/ac3d/test1.ac").getFile());
        var br = new BufferedReader(new FileReader(f));
        var res = parser.parse(br);
        Assert.assertNotNull(res);
    }

    @SneakyThrows
    @Test
    public void testParse2() {
        var f = new File(this.getClass().getResource("/ac3d/test2.ac").getFile());
        var br = new BufferedReader(new FileReader(f));
        var res = parser.parse(br);
        Assert.assertNotNull(res);
    }

    @SneakyThrows
    @Test
    public void testParse3() {
        var f = new File(this.getClass().getResource("/ac3d/sr22.ac").getFile());
        var br = new BufferedReader(new FileReader(f));
        var res = parser.parse(br);
        Assert.assertNotNull(res);
    }

    @SneakyThrows
    @Test
    public void testParse4() {
        var f = new File(this.getClass().getResource("/ac3d/shadow.ac").getFile());
        var br = new BufferedReader(new FileReader(f));
        var res = parser.parse(br);
        Assert.assertEquals(1, res.getMaterials().size());
        Assert.assertEquals(1, res.getObjects().size());

        var child = res.getObjects().get(0).getChildren().get(0);
        Assert.assertEquals("shadow", child.getName());
        Assert.assertEquals(2, child.getData().size());
        Assert.assertEquals("shadow", child.getData().get(0));
        Assert.assertEquals("d", child.getData().get(1));
        Assert.assertEquals(1, child.getSurfaces().size());

        Assert.assertEquals(1, res.getMaterials().size());
    }

    @SneakyThrows
    @Test
    public void testParse5() {
        var f = new File(this.getClass().getResource("/ac3d/sr22.obj").getFile());
        var br = new BufferedReader(new FileReader(f));

        var res = new ObjLoader().loadModelData(new FileHandle(f));
        Assert.assertNotNull(res);
    }
}
