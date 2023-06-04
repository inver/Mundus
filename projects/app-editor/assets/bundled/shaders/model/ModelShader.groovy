package bundled.shaders.model

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.RenderContext
import com.badlogic.gdx.utils.Array
import com.mbrlabs.mundus.commons.env.Fog
import com.mbrlabs.mundus.commons.env.SceneEnvironment
import com.mbrlabs.mundus.commons.env.lights.DirectionalLight
import com.mbrlabs.mundus.commons.env.lights.DirectionalLightsAttribute
import net.nevinsky.abyssus.core.Renderable
import net.nevinsky.abyssus.core.shader.DefaultShader
import net.nevinsky.abyssus.core.shader.Shader

class ModelShader extends DefaultShader {

    // ============================ MATERIALS ============================
    protected final int UNIFORM_MATERIAL_DIFFUSE_TEXTURE = register(new Uniform("u_diffuseTexture"))
    protected final int UNIFORM_MATERIAL_DIFFUSE_COLOR = register(new Uniform("u_diffuseColor"))
    protected final int UNIFORM_MATERIAL_DIFFUSE_USE_TEXTURE = register(new Uniform("u_diffuseUseTexture"))
    protected final int UNIFORM_MATERIAL_SHININESS = register(new Uniform("u_shininess"))

    // ============================ LIGHTS ============================
    protected final int UNIFORM_AMBIENT_LIGHT_COLOR = register(new Uniform("u_ambientLight.color"))
    protected final int UNIFORM_AMBIENT_LIGHT_INTENSITY = register(new Uniform("u_ambientLight.intensity"))
    protected final int UNIFORM_DIRECTIONAL_LIGHT_COLOR = register(new Uniform("u_directionalLight.color"))
    protected final int UNIFORM_DIRECTIONAL_LIGHT_DIR = register(new Uniform("u_directionalLight.direction"))
    protected final int UNIFORM_DIRECTIONAL_LIGHT_INTENSITY = register(new Uniform("u_directionalLight.intensity"))

    // ============================ FOG ============================
    protected final int UNIFORM_FOG_DENSITY = register(new Uniform("u_fogDensity"))
    protected final int UNIFORM_FOG_GRADIENT = register(new Uniform("u_fogGradient"))
    protected final int UNIFORM_FOG_COLOR = register(new Uniform("u_fogColor"))

    ModelShader(String vertexShader, String fragmentShader) {
        super(vertexShader, fragmentShader)
    }

    @Override
    void begin(Camera camera, RenderContext context) {
        this.context = context
        context.begin()

        this.context.setCullFace(GL20.GL_BACK)
        this.context.setDepthTest(GL20.GL_LEQUAL, 0f, 1f)
        this.context.setDepthMask(true)

        program.bind()

        set(UNIFORM_PROJ_VIEW_MATRIX, camera.combined)
        set(UNIFORM_CAM_POS, camera.position)
    }

    @Override
    void render(Renderable renderable) {
        final SceneEnvironment env = (SceneEnvironment) renderable.environment

        setLights(env)
        set(UNIFORM_TRANS_MATRIX, renderable.worldTransform)

        // texture uniform
        TextureAttribute diffuseTexture = ((TextureAttribute) (renderable.material.get(TextureAttribute.Diffuse)))
        ColorAttribute diffuseColor = ((ColorAttribute) (renderable.material.get(ColorAttribute.Diffuse)))

        if (diffuseTexture != null) {
            set(UNIFORM_MATERIAL_DIFFUSE_TEXTURE, diffuseTexture.textureDescription.texture)
            set(UNIFORM_MATERIAL_DIFFUSE_USE_TEXTURE, 1)
        } else {
            set(UNIFORM_MATERIAL_DIFFUSE_COLOR, diffuseColor.color)
            set(UNIFORM_MATERIAL_DIFFUSE_USE_TEXTURE, 0)
        }

        // shininess
        if (renderable.material.has(FloatAttribute.Shininess)) {
            float shininess = ((FloatAttribute) renderable.material.get(FloatAttribute.Shininess)).value
            set(UNIFORM_MATERIAL_SHININESS, shininess)
        }

        // Fog
        final Fog fog = env.getFog()
        if (fog == null) {
            set(UNIFORM_FOG_DENSITY, 0f)
            set(UNIFORM_FOG_GRADIENT, 0f)
        } else {
            set(UNIFORM_FOG_DENSITY, fog.density)
            set(UNIFORM_FOG_GRADIENT, fog.gradient)
            set(UNIFORM_FOG_COLOR, fog.color)
        }

        // bind attributes, bind mesh & render; then unbinds everything
        renderable.meshPart.render(program)
    }

    private void setLights(SceneEnvironment env) {
        // ambient
        set(UNIFORM_AMBIENT_LIGHT_COLOR, env.getAmbientLight().getColor())
        set(UNIFORM_AMBIENT_LIGHT_INTENSITY, env.getAmbientLight().getIntensity())

        // TODO light array for each light type

        // directional lights
        final DirectionalLightsAttribute dirLightAttribs = env.get(DirectionalLightsAttribute.class,
                DirectionalLightsAttribute.TYPE)
        final Array<DirectionalLight> dirLights = dirLightAttribs == null ? null : dirLightAttribs.lights
        if (dirLights != null && dirLights.size > 0) {
            final DirectionalLight light = dirLights.first()
            set(UNIFORM_DIRECTIONAL_LIGHT_COLOR, light.getColor())
            //todo
//            set(UNIFORM_DIRECTIONAL_LIGHT_DIR, light.getDirection());
            set(UNIFORM_DIRECTIONAL_LIGHT_INTENSITY, light.getIntensity())
        }

        // TODO point lights, spot lights
    }
}

