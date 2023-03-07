package com.mbrlabs.mundus.commons.loader;

import org.junit.Assert;
import org.junit.Test;

public class AssimpLoaderTest extends BaseTest {

    private final AssimpModelLoader loader = new AssimpModelLoader();

    @Test
    public void testLoadOnlyObjFile() {
        var res = loader.loadModel("ololo", getHandle("/obj/sr22/sr22.obj"));
        Assert.assertNotNull(res);
    }

}
