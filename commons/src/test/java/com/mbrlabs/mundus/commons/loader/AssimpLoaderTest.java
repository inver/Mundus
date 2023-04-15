package com.mbrlabs.mundus.commons.loader;

import com.mbrlabs.mundus.commons.BaseTest;
import org.junit.Assert;
import org.junit.Test;

public class AssimpLoaderTest extends BaseTest {

    private final AssimpModelLoader loader = new AssimpModelLoader();

    @Test
    public void testLoadOnlyObjFile() {
        var res = loader.loadModelData("ololo", getHandle("/obj/sr22/sr22.obj"));
        Assert.assertNotNull(res);
    }

}
