package com.mbrlabs.mundus.commons.loader.obj;

import com.mbrlabs.mundus.commons.loader.BaseTest;
import com.mbrlabs.mundus.commons.loader.obj.material.ObjMaterialLoader;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class ObjMaterialLoaderTest extends BaseTest {

    private final ObjMaterialLoader loader = new ObjMaterialLoader(true);

    @Test
    public void testLoadMaterial1() {
        var res = loader.load(getFile("/obj/piper/piper_pa18.mtl"));
        Assert.assertNotNull(res);
    }

    @Ignore
    @Test
    public void testLoadMaterial2() {
        var res = loader.load(getFile("/obj/cessna172/cessna172.mtl"));
        Assert.assertNotNull(res);
    }
}
