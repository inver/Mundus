package com.mbrlabs.mundus.commons.loader.obj;

import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.mbrlabs.mundus.commons.loader.BaseTest;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

public class ObjLoaderTest extends BaseTest {

    @SneakyThrows
    @Test
    public void testLoadOnlyObjFile() {
        var res = new ObjLoader().loadModelData(getHandle("/obj/sr22.obj"));
        Assert.assertNotNull(res);
    }

    @SneakyThrows
    @Test
    public void testLoadObjModelWithMaterials() {
        var parser = new ObjParser(getFile("/obj/model.obj"));
        var res = parser.parse();
        Assert.assertNotNull(res);
    }
}
