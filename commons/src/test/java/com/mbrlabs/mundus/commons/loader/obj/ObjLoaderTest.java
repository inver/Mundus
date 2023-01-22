package com.mbrlabs.mundus.commons.loader.obj;

import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver;
import com.mbrlabs.mundus.commons.loader.BaseTest;
import com.mbrlabs.mundus.commons.loader.obj.material.ObjMaterialLoader;
import org.junit.Assert;
import org.junit.Test;

public class ObjLoaderTest extends BaseTest {

    private final ObjMaterialLoader materialLoader = new ObjMaterialLoader();
    private final ObjLoader loader = new ObjLoader(new ClasspathFileHandleResolver(), materialLoader, true);

    @Test
    public void testLoadOnlyObjFile() {
        var res = loader.loadModelData(getHandle("/obj/sr22/sr22.obj"));
        Assert.assertNotNull(res);
    }

    @Test
    public void testLoadObjModelWithMaterials() {
        var res = loader.loadModelData(getHandle("/obj/cessna172/cessna172.obj"));
        Assert.assertNotNull(res);
    }

    @Test
    public void testLoadComplexModelWithMaterials() {
        var res = loader.loadModelData(getHandle("/obj/piper/piper_pa18.obj"));
        Assert.assertNotNull(res);
    }
}
