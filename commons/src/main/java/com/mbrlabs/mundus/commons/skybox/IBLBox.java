package com.mbrlabs.mundus.commons.skybox;

import com.badlogic.gdx.graphics.Cubemap;
import net.nevinsky.mundus.core.model.Model;
import net.nevinsky.mundus.core.ModelInstance;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;

public class IBLBox implements Disposable {

    protected ShaderProgram shader;

    private Model boxModel;
    private ModelInstance boxInstance;

    private Cubemap cubemap;


    @Override
    public void dispose() {
        boxModel.dispose();
        cubemap.dispose();
    }
}
