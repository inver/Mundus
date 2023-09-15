package bundled.shaders.terrain

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.RenderContext
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.mbrlabs.mundus.commons.env.Fog
import com.mbrlabs.mundus.commons.env.SceneEnvironment
import com.mbrlabs.mundus.commons.terrain.SplatTexture
import com.mbrlabs.mundus.commons.terrain.TerrainObject
import com.mbrlabs.mundus.commons.terrain.TerrainTexture
import com.mbrlabs.mundus.commons.terrain.TerrainTextureAttribute
import net.nevinsky.abyssus.core.Renderable
import net.nevinsky.abyssus.core.shader.DefaultShader
import net.nevinsky.abyssus.core.shader.Shader
import net.nevinsky.abyssus.core.shader.ShaderConfig

/**
 * @author Marcus Brummer
 * @version 22-11-2015
 */
class BundledTerrainShader extends DefaultShader {

    // ============================ MATRICES & CAM POSITION ============================
    protected final int UNIFORM_PROJ_VIEW_MATRIX = register(new Uniform("u_projViewMatrix"))
    protected final int UNIFORM_TRANS_MATRIX = register(new Uniform("u_transMatrix"))
    protected final int UNIFORM_CAM_POS = register(new Uniform("u_camPos"))

    // ============================ LIGHTS ============================
    protected final int UNIFORM_AMBIENT_LIGHT_COLOR = register(new Uniform("u_ambientLight.color"))
    protected final int UNIFORM_AMBIENT_LIGHT_INTENSITY = register(new Uniform("u_ambientLight.intensity"))
    protected final int UNIFORM_DIRECTIONAL_LIGHT_COLOR = register(new Uniform("u_directionalLight.color"))
    protected final int UNIFORM_DIRECTIONAL_LIGHT_DIR = register(new Uniform("u_directionalLight.direction"))
    protected final int UNIFORM_DIRECTIONAL_LIGHT_INTENSITY = register(new Uniform("u_directionalLight.intensity"))

    // ============================ TEXTURE SPLATTING ============================
    protected final int UNIFORM_TERRAIN_SIZE = register(new Uniform("u_terrainSize"))
    protected final int UNIFORM_TEXTURE_BASE = register(new Uniform("u_texture_base"))
    protected final int UNIFORM_TEXTURE_R = register(new Uniform("u_texture_r"))
    protected final int UNIFORM_TEXTURE_G = register(new Uniform("u_texture_g"))
    protected final int UNIFORM_TEXTURE_B = register(new Uniform("u_texture_b"))
    protected final int UNIFORM_TEXTURE_A = register(new Uniform("u_texture_a"))
    protected final int UNIFORM_TEXTURE_SPLAT = register(new Uniform("u_texture_splat"))
    protected final int UNIFORM_TEXTURE_HAS_SPLATMAP = register(new Uniform("u_texture_has_splatmap"))
    protected final int UNIFORM_TEXTURE_HAS_DIFFUSE = register(new Uniform("u_texture_has_diffuse"))

    // ============================ FOG ============================
    protected final int UNIFORM_FOG_DENSITY = register(new Uniform("u_fogDensity"))
    protected final int UNIFORM_FOG_GRADIENT = register(new Uniform("u_fogGradient"))
    protected final int UNIFORM_FOG_COLOR = register(new Uniform("u_fogColor"))

    private final Vector2 terrainSize = new Vector2()

    BundledTerrainShader(ShaderConfig config, Renderable renderable) {
        super(config, renderable)
    }

    @Override
    int compareTo(Shader other) {
        return 0
    }

    @Override
    boolean canRender(Renderable instance) {
        return true
    }

    @Override
    void begin(Camera camera, RenderContext context) {
        this.context = context
        context.begin()
        context.setCullFace(GL20.GL_BACK)

        context.setDepthTest(GL20.GL_LEQUAL, 0f, 1f)
        context.setDepthMask(true)

        program.bind()

        set(UNIFORM_PROJ_VIEW_MATRIX, camera.combined)
        set(UNIFORM_CAM_POS, camera.position)
    }

    @Override
    void render(Renderable renderable) {
        final SceneEnvironment env = (SceneEnvironment) renderable.environment

        setLights(env, (TerrainObject.TerrainUserData) renderable.userData)
        setTerrainSplatTextures(renderable)
        set(UNIFORM_TRANS_MATRIX, renderable.worldTransform)

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

    protected void setLights(SceneEnvironment env, TerrainObject.TerrainUserData userData) {
        // ambient
        set(UNIFORM_AMBIENT_LIGHT_COLOR, env.getAmbientLight().getColor())
        set(UNIFORM_AMBIENT_LIGHT_INTENSITY, env.getAmbientLight().getIntensity())

        for (def light : userData.getLights()) {

        }

        // TODO light array for each light type

        // directional lights
        final DirectionalLightsAttribute dirLightAttribs = env.get(DirectionalLightsAttribute.class,
                DirectionalLightsAttribute.Type)
        final Array<DirectionalLight> dirLights = dirLightAttribs == null ? null : dirLightAttribs.lights
        if (dirLights != null && dirLights.size > 0) {
            final DirectionalLight light = dirLights.first()
            set(UNIFORM_DIRECTIONAL_LIGHT_COLOR, light.color)
            set(UNIFORM_DIRECTIONAL_LIGHT_DIR, light.direction)
            //todo
//            set(UNIFORM_DIRECTIONAL_LIGHT_INTENSITY, light.intensity);
            set(UNIFORM_DIRECTIONAL_LIGHT_INTENSITY, 0.3f)
        }

        // TODO point lights, spot lights
    }

    protected void setTerrainSplatTextures(Renderable renderable) {
        final TerrainTextureAttribute splatAttrib = (TerrainTextureAttribute) renderable.material
                .get(TerrainTextureAttribute.ATTRIBUTE_SPLAT0)
        final TerrainTexture terrainTexture = splatAttrib.terrainTexture

        // base texture
        SplatTexture st = terrainTexture.getTexture(SplatTexture.Channel.BASE)
        if (st != null) {
            set(UNIFORM_TEXTURE_BASE, st.texture.getTexture())
            set(UNIFORM_TEXTURE_HAS_DIFFUSE, 1)
        } else {
            set(UNIFORM_TEXTURE_HAS_DIFFUSE, 0)
        }

        // splat textures
        if (terrainTexture.getSplatMap() != null) {
            set(UNIFORM_TEXTURE_HAS_SPLATMAP, 1)
            set(UNIFORM_TEXTURE_SPLAT, terrainTexture.getSplatMap().getTexture())
            st = terrainTexture.getTexture(SplatTexture.Channel.R)
            if (st != null) {
                set(UNIFORM_TEXTURE_R, st.texture.getTexture())
            }
            st = terrainTexture.getTexture(SplatTexture.Channel.G)
            if (st != null) {
                set(UNIFORM_TEXTURE_G, st.texture.getTexture())
            }
            st = terrainTexture.getTexture(SplatTexture.Channel.B)
            if (st != null) {
                set(UNIFORM_TEXTURE_B, st.texture.getTexture())
            }
            st = terrainTexture.getTexture(SplatTexture.Channel.A)
            if (st != null) {
                set(UNIFORM_TEXTURE_A, st.texture.getTexture())
            }
        } else {
            set(UNIFORM_TEXTURE_HAS_SPLATMAP, 0)
        }

        TerrainObject.TerrainUserData userData = (TerrainObject.TerrainUserData) renderable.userData
        // set terrain world size
        //todo migrate to Vector2
        terrainSize.x = userData.getTerrainWidth()
        terrainSize.y = userData.getTerrainDepth()
        set(UNIFORM_TERRAIN_SIZE, terrainSize)
    }
}
