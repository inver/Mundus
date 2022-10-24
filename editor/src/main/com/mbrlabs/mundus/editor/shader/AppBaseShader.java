package com.mbrlabs.mundus.editor.shader;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.commons.env.lights.SunLightsAttribute;
import com.mbrlabs.mundus.commons.shaders.DefaultBaseShader;

public abstract class AppBaseShader extends DefaultBaseShader {

    // ============================ MATERIALS ============================
    protected final int UNIFORM_MATERIAL_DIFFUSE_TEXTURE = register(new Uniform("u_diffuseTexture"));
    protected final int UNIFORM_MATERIAL_DIFFUSE_COLOR = register(new Uniform("u_diffuseColor"));
    protected final int UNIFORM_MATERIAL_DIFFUSE_USE_TEXTURE = register(new Uniform("u_diffuseUseTexture"));
    protected final int UNIFORM_MATERIAL_SHININESS = register(new Uniform("u_shininess"));

    // ============================ MATRICES & CAM POSITION ============================
    protected final int UNIFORM_PROJ_VIEW_MATRIX = register(new Uniform("u_projViewMatrix"));
    protected final int UNIFORM_TRANS_MATRIX = register(new Uniform("u_transMatrix"));
    protected final int UNIFORM_CAM_POS = register(new Uniform("u_camPos"));

    // ============================ LIGHTS ============================
    protected final int UNIFORM_AMBIENT_LIGHT_COLOR = register(new Uniform("u_ambientLight.color"));
    protected final int UNIFORM_AMBIENT_LIGHT_INTENSITY = register(new Uniform("u_ambientLight.intensity"));
    protected final int UNIFORM_DIRECTIONAL_LIGHT_COLOR = register(new Uniform("u_directionalLight.color"));
    protected final int UNIFORM_DIRECTIONAL_LIGHT_DIR = register(new Uniform("u_directionalLight.direction"));
    protected final int UNIFORM_DIRECTIONAL_LIGHT_INTENSITY = register(new Uniform("u_directionalLight.intensity"));

    protected final int UNIFORM_SUN_LIGHT_POSITION = register(new Uniform("u_sunLightPos"));


    protected final int UNIFORM_LIGHT_COLOR = register(new Uniform("u_lightColor"));
    protected final int UNIFORM_OBJECT_COLOR = register(new Uniform("objectColor"));

    //    protected final String vertexShaderPath;
//    protected final String fragmentShaderPath;
//    @Getter
//    protected final boolean bundled;

    public AppBaseShader(String vertexShader, String fragmentShader) {
        super(vertexShader, fragmentShader);
    }

//    public void compile() {
//        program = ShaderUtils.compile(vertexShaderPath, fragmentShaderPath);
//    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        this.context = context;
        context.begin();
//        context.setCullFace(GL20.GL_BACK);
//
//        context.setDepthTest(GL20.GL_LEQUAL, 0f, 1f);
//        context.setDepthMask(true);

        program.bind();

        set(UNIFORM_PROJ_VIEW_MATRIX, camera.combined);
        set(UNIFORM_CAM_POS, camera.position);
    }

    protected void setLights(SceneEnvironment env) {
        // ambient
        if (env.getAmbientLight() != null) {
            set(UNIFORM_AMBIENT_LIGHT_COLOR, env.getAmbientLight().color);
            set(UNIFORM_AMBIENT_LIGHT_INTENSITY, env.getAmbientLight().intensity);
        }

        var sunLightAttr = env.get(SunLightsAttribute.class, SunLightsAttribute.Type);
        set(UNIFORM_SUN_LIGHT_POSITION, sunLightAttr.lights.get(0).position);
//        set(UNIFORM_OBJECT_COLOR, new Color(1f, 1, 1, 1));
//        set(UNIFORM_AMBIENT_LIGHT_COLOR, env.getAmbientLight().color);
//        set(UNIFORM_AMBIENT_LIGHT_INTENSITY, env.getAmbientLight().intensity);

        // TODO light array for each light type

        // directional lights
//        final DirectionalLightsAttribute dirLightAttribs = env.get(DirectionalLightsAttribute.class,
//                DirectionalLightsAttribute.TYPE);
//        final Array<DirectionalLight> dirLights = dirLightAttribs == null ? null : dirLightAttribs.lights;
//        if (dirLights != null && dirLights.size > 0) {
//            final DirectionalLight light = dirLights.first();
//            set(UNIFORM_DIRECTIONAL_LIGHT_COLOR, light.color);
//            set(UNIFORM_DIRECTIONAL_LIGHT_DIR, light.direction);
//            set(UNIFORM_DIRECTIONAL_LIGHT_INTENSITY, light.intensity);
//        }

        // TODO point lights, spot lights
    }
}
