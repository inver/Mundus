package net.nevinsky.abyssus.core.shader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.CubemapAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.SpotLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.AmbientCubemap;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lombok.RequiredArgsConstructor;
import net.nevinsky.abyssus.core.Renderable;

import java.util.function.Function;
import java.util.function.Supplier;

public class DefaultShader extends BaseShader {
    private static final long optionalAttributes = IntAttribute.CullFace | DepthTestAttribute.Type;
    protected static long implementedFlags = BlendingAttribute.Type | TextureAttribute.Diffuse | ColorAttribute.Diffuse
            | ColorAttribute.Specular | FloatAttribute.Shininess;

    protected String vertexShader;
    protected String fragmentShader;
    private boolean initialized = false;
    private final Attributes tmpAttributes = new Attributes();

    // Global uniforms
    protected int u_projTrans;
    protected int u_viewTrans;
    protected int u_projViewTrans;
    protected int u_cameraPosition;
    protected int u_cameraDirection;
    protected int u_cameraUp;
    protected int u_cameraNearFar;
    protected int u_time;
    // Object uniforms
    protected int u_worldTrans;
    protected int u_viewWorldTrans;
    protected int u_projViewWorldTrans;
    protected int u_normalMatrix;
    protected int u_bones = -1;
    // Material uniforms
    protected int u_shininess;
    protected int u_opacity;
    protected int u_diffuseColor;
    protected int u_diffuseTexture;
    protected int u_diffuseUVTransform;
    protected int u_specularColor;
    protected int u_specularTexture;
    protected int u_specularUVTransform;
    protected int u_emissiveColor;
    protected int u_emissiveTexture;
    protected int u_emissiveUVTransform;
    protected int u_reflectionColor;
    protected int u_reflectionTexture;
    protected int u_reflectionUVTransform;
    protected int u_normalTexture;
    protected int u_normalUVTransform;
    protected int u_ambientTexture;
    protected int u_ambientUVTransform;
    protected int u_alphaTest;
    // Lighting uniforms
    protected int u_ambientCubemap = -1;
    protected int u_environmentCubemap = -1;
    protected final int u_dirLights0color = register(new Uniform("u_dirLights[0].color"));
    protected final int u_dirLights0direction = register(new Uniform("u_dirLights[0].direction"));
    protected final int u_dirLights1color = register(new Uniform("u_dirLights[1].color"));
    protected final int u_pointLights0color = register(new Uniform("u_pointLights[0].color"));
    protected final int u_pointLights0position = register(new Uniform("u_pointLights[0].position"));
    protected final int u_pointLights0intensity = register(new Uniform("u_pointLights[0].intensity"));
    protected final int u_pointLights1color = register(new Uniform("u_pointLights[1].color"));
    protected final int u_spotLights0color = register(new Uniform("u_spotLights[0].color"));
    protected final int u_spotLights0position = register(new Uniform("u_spotLights[0].position"));
    protected final int u_spotLights0intensity = register(new Uniform("u_spotLights[0].intensity"));
    protected final int u_spotLights0direction = register(new Uniform("u_spotLights[0].direction"));
    protected final int u_spotLights0cutoffAngle = register(new Uniform("u_spotLights[0].cutoffAngle"));
    protected final int u_spotLights0exponent = register(new Uniform("u_spotLights[0].exponent"));
    protected final int u_spotLights1color = register(new Uniform("u_spotLights[1].color"));
    protected final int u_fogColor = register(new Uniform("u_fogColor"));
    protected final int u_shadowMapProjViewTrans = register(new Uniform("u_shadowMapProjViewTrans"));
    protected final int u_shadowTexture = register(new Uniform("u_shadowTexture"));
    protected final int u_shadowPCFOffset = register(new Uniform("u_shadowPCFOffset"));
    // TODO Cache vertex attribute locations...

    protected int dirLightsLoc;
    protected int dirLightsColorOffset;
    protected int dirLightsDirectionOffset;
    protected int dirLightsSize;
    protected int pointLightsLoc;
    protected int pointLightsColorOffset;
    protected int pointLightsPositionOffset;
    protected int pointLightsIntensityOffset;
    protected int pointLightsSize;
    protected int spotLightsLoc;
    protected int spotLightsColorOffset;
    protected int spotLightsPositionOffset;
    protected int spotLightsDirectionOffset;
    protected int spotLightsIntensityOffset;
    protected int spotLightsCutoffAngleOffset;
    protected int spotLightsExponentOffset;
    protected int spotLightsSize;

    protected final boolean lighting;
    protected final boolean environmentCubemap;
    protected final boolean shadowMap;

    protected final ShaderConfig config;

    protected final DirectionalLight[] directionalLights;
    protected final PointLight[] pointLights;
    protected final SpotLight[] spotLights;

    /**
     * The attributes that this shader supports
     */
    protected final long attributesMask;
    private final long vertexMask;

    private float time;
    private boolean lightsSet;

    public DefaultShader(ShaderConfig config, Renderable renderable) {
        this.config = config;
        this.vertexShader = config.getVertexShader();
        this.fragmentShader = config.getFragmentShader();
        this.lighting = renderable.environment != null;

        final var attributes = combineAttributes(renderable);
        this.environmentCubemap = attributes.has(CubemapAttribute.EnvironmentMap) ||
                (lighting && attributes.has(CubemapAttribute.EnvironmentMap));
        this.shadowMap = lighting && renderable.environment.shadowMap != null;

        attributesMask = attributes.getMask() | optionalAttributes;
        vertexMask = renderable.meshPart.mesh.getVertexAttributes().getMaskWithSizePacked();

        directionalLights = createAndInit(config.getNumDirectionalLights(), DirectionalLight[]::new,
                DirectionalLight::new);

        pointLights = createAndInit(config.getNumPointLights(), PointLight[]::new, PointLight::new);
        spotLights = createAndInit(config.getNumSpotLights(), SpotLight[]::new, SpotLight::new);

        if (!config.isIgnoreUnimplemented() && (implementedFlags & attributesMask) != attributesMask) {
            throw new GdxRuntimeException("Some attributes not implemented yet (" + attributesMask + ")");
        }

        if (renderable.bones != null && renderable.bones.length > config.getNumBones()) {
            throw new GdxRuntimeException(String.format("too many bones: %d, max configured: %d",
                    renderable.bones.length, config.getNumBones())
            );
        }

        initGlobalUniforms();
        initObjectUniforms(renderable);
        initMaterialUniforms();
        initLightUniforms();
    }

    private <T> T[] createAndInit(int length, Function<Integer, T[]> arrayCreator, Supplier<T> instanceCreator) {
        if (!lighting && length < 1) {
            return arrayCreator.apply(0);
        }

        var res = arrayCreator.apply(length);
        for (int i = 0; i < res.length; i++) {
            res[i] = instanceCreator.get();
        }
        return res;
    }

    private void initGlobalUniforms() {
        u_projTrans = registerUniformGlobal("u_projTrans",
                (shader, inputID, renderable1, combinedAttributes) -> shader.set(inputID, shader.camera.projection));
        u_viewTrans = registerUniformGlobal("u_viewTrans",
                (shader, inputID, renderable1, combinedAttributes) -> shader.set(inputID, shader.camera.view));
        u_projViewTrans = registerUniformGlobal("u_projViewTrans",
                (shader, inputID, renderable1, combinedAttributes) -> shader.set(inputID, shader.camera.combined));
        u_cameraPosition = registerUniformGlobal("u_cameraPosition",
                (shader, inputID, renderable1, combinedAttributes) -> shader.set(inputID, shader.camera.position.x,
                        shader.camera.position.y, shader.camera.position.z,
                        1.1881f / (shader.camera.far * shader.camera.far)));
        u_cameraDirection = registerUniformGlobal("u_cameraDirection",
                (shader, inputID, renderable1, combinedAttributes) -> shader.set(inputID, shader.camera.direction));
        u_cameraUp = registerUniformGlobal("u_cameraUp",
                (shader, inputID, renderable1, combinedAttributes) -> shader.set(inputID, shader.camera.up));
        u_cameraNearFar = registerUniformGlobal("u_cameraNearFar",
                (shader, inputID, renderable1, combinedAttributes) -> shader.set(inputID, shader.camera.near,
                        shader.camera.far));
        u_time = register(new Uniform("u_time"));
    }

    private void initObjectUniforms(Renderable inputRenderable) {
        u_worldTrans = registerUniformLocal("u_worldTrans",
                (shader, inputID, renderable, combinedAttributes) -> shader.set(inputID, renderable.worldTransform));

        u_viewWorldTrans = register(new Uniform("u_viewWorldTrans"), new LocalSetterWithAttr<>(new Matrix4()) {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable,
                            Attributes combinedAttributes) {
                shader.set(inputID, attr.set(shader.camera.view).mul(renderable.worldTransform));
            }
        });
        u_projViewWorldTrans = register(new Uniform("u_viewWorldTrans"), new LocalSetterWithAttr<>(new Matrix4()) {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable,
                            Attributes combinedAttributes) {
                shader.set(inputID, attr.set(shader.camera.combined).mul(renderable.worldTransform));
            }
        });
        u_normalMatrix = register(new Uniform("u_normalMatrix"), new LocalSetterWithAttr<>(new Matrix3()) {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable,
                            Attributes combinedAttributes) {
                shader.set(inputID, attr.set(renderable.worldTransform).inv().transpose());
            }
        });
        if (inputRenderable.bones != null && config.getNumBones() > 0) {
            u_bones = register(new Uniform("u_bones"), new LocalSetterWithAttr<>(new Matrix4()) {
                private final float[] bones = new float[config.getNumBones() * 16];

                @Override
                public void set(BaseShader shader, int inputID, Renderable renderable,
                                Attributes combinedAttributes) {
                    for (int i = 0; i < bones.length; i += 16) {
                        final int idx = i / 16;
                        if (renderable.bones == null || idx >= renderable.bones.length ||
                                renderable.bones[idx] == null) {
                            System.arraycopy(attr.val, 0, bones, i, 16);
                        } else {
                            System.arraycopy(renderable.bones[idx].val, 0, bones, i, 16);
                        }
                    }
                    shader.program.setUniformMatrix4fv(shader.loc(inputID), bones, 0, bones.length);
                }
            });
        }
    }

    private void initMaterialUniforms() {
        u_shininess = registerUniformLocal("u_shininess", FloatAttribute.Shininess,
                (shader, inputID, renderable1, combinedAttributes) -> shader.set(inputID,
                        ((FloatAttribute) (combinedAttributes.get(FloatAttribute.Shininess))).value));
        u_opacity = register(new Uniform("u_opacity", BlendingAttribute.Type));

        var materialPart = initMaterialPart("diffuse", ColorAttribute.Diffuse, TextureAttribute.Diffuse);
        u_diffuseColor = materialPart.colorUniform;
        u_diffuseTexture = materialPart.textureUniform;
        u_diffuseUVTransform = materialPart.uVTransformUniform;
        materialPart = initMaterialPart("specular", ColorAttribute.Specular, TextureAttribute.Specular);
        u_specularColor = materialPart.colorUniform;
        u_specularTexture = materialPart.textureUniform;
        u_specularUVTransform = materialPart.uVTransformUniform;
        materialPart = initMaterialPart("emissive", ColorAttribute.Emissive, TextureAttribute.Emissive);
        u_emissiveColor = materialPart.colorUniform;
        u_emissiveTexture = materialPart.textureUniform;
        u_emissiveUVTransform = materialPart.uVTransformUniform;
        materialPart = initMaterialPart("reflection", ColorAttribute.Reflection, TextureAttribute.Reflection);
        u_reflectionColor = materialPart.colorUniform;
        u_reflectionTexture = materialPart.textureUniform;
        u_reflectionUVTransform = materialPart.uVTransformUniform;

        materialPart = initMaterialPart("normal", false, -1, TextureAttribute.Normal);
        u_normalTexture = materialPart.textureUniform;
        u_normalUVTransform = materialPart.uVTransformUniform;

        materialPart = initMaterialPart("ambient", false, -1, TextureAttribute.Normal);
        u_ambientTexture = materialPart.textureUniform;
        u_ambientUVTransform = materialPart.uVTransformUniform;

        u_alphaTest = register(new Uniform("u_alphaTest"));
    }

    private void initLightUniforms() {
        if (lighting) {
            u_ambientCubemap = register(new Uniform("u_ambientCubemap"),
                    new ACubemapSetter(config.getNumDirectionalLights(), config.getNumPointLights())
            );
        }
        if (environmentCubemap) {
            u_environmentCubemap = register(new Uniform("u_environmentCubemap"), new LocalSetter() {
                @Override
                public void set(BaseShader shader, int inputID, Renderable renderable,
                                Attributes combinedAttributes) {
                    if (combinedAttributes.has(CubemapAttribute.EnvironmentMap)) {
                        shader.set(inputID, shader.context.textureBinder
                                .bind(((CubemapAttribute) combinedAttributes.get(
                                        CubemapAttribute.EnvironmentMap)).textureDescription));
                    }
                }
            });
        }
    }

    private MaterialPartUniformHolder initMaterialPart(String prefix, long colorAttribute, long textureAttribute) {
        return initMaterialPart(prefix, true, colorAttribute, textureAttribute);
    }

    private MaterialPartUniformHolder initMaterialPart(String prefix, boolean colorNeeded, long colorAttribute,
                                                       long textureAttribute) {
        int colorAttr = 0;
        if (colorNeeded) {
            colorAttr = registerUniformLocal(String.format("u_%sColor", prefix), colorAttribute,
                    (shader, inputID, renderable1, combinedAttributes) -> shader.set(inputID,
                            ((ColorAttribute) (combinedAttributes.get(colorAttribute))).color));
        }
        return new MaterialPartUniformHolder(
                colorAttr,
                registerUniformLocal(String.format("u_%sTexture", prefix), textureAttribute,
                        (shader, inputID, renderable1, combinedAttributes) -> shader.set(inputID,
                                shader.context.textureBinder
                                        .bind(((TextureAttribute) (combinedAttributes.get(
                                                textureAttribute))).textureDescription))),
                registerUniformLocal(String.format("u_%sUVTransform", prefix), textureAttribute,
                        (shader, inputID, renderable1, combinedAttributes) -> {
                            final TextureAttribute ta = (TextureAttribute) combinedAttributes.get(textureAttribute);
                            shader.set(inputID, ta.offsetU, ta.offsetV, ta.scaleU, ta.scaleV);
                        })
        );
    }

    protected void compile() {
        program = new ShaderProgram(vertexShader, fragmentShader);
        if (!program.isCompiled()) {
            throw new GdxRuntimeException(program.getLog());
        }
    }

    @Override
    public void init(Renderable renderable) {
        if (initialized) {
            throw new IllegalStateException("Try to call init for initialized shader");
        }
        initialized = true;

        preprocessShaderContents(renderable);
        compile();
        init(program, renderable);

        dirLightsLoc = loc(u_dirLights0color);
        dirLightsColorOffset = loc(u_dirLights0color) - dirLightsLoc;
        dirLightsDirectionOffset = loc(u_dirLights0direction) - dirLightsLoc;
        dirLightsSize = loc(u_dirLights1color) - dirLightsLoc;
        if (dirLightsSize < 0) {
            dirLightsSize = 0;
        }

        pointLightsLoc = loc(u_pointLights0color);
        pointLightsColorOffset = loc(u_pointLights0color) - pointLightsLoc;
        pointLightsPositionOffset = loc(u_pointLights0position) - pointLightsLoc;
        pointLightsIntensityOffset = has(u_pointLights0intensity) ? loc(u_pointLights0intensity) - pointLightsLoc : -1;
        pointLightsSize = loc(u_pointLights1color) - pointLightsLoc;
        if (pointLightsSize < 0) {
            pointLightsSize = 0;
        }

        spotLightsLoc = loc(u_spotLights0color);
        spotLightsColorOffset = loc(u_spotLights0color) - spotLightsLoc;
        spotLightsPositionOffset = loc(u_spotLights0position) - spotLightsLoc;
        spotLightsDirectionOffset = loc(u_spotLights0direction) - spotLightsLoc;
        spotLightsIntensityOffset = has(u_spotLights0intensity) ? loc(u_spotLights0intensity) - spotLightsLoc : -1;
        spotLightsCutoffAngleOffset = loc(u_spotLights0cutoffAngle) - spotLightsLoc;
        spotLightsExponentOffset = loc(u_spotLights0exponent) - spotLightsLoc;
        spotLightsSize = loc(u_spotLights1color) - spotLightsLoc;
        if (spotLightsSize < 0) {
            spotLightsSize = 0;
        }
    }

    @Override
    public void begin(final Camera camera, final RenderContext context) {
        super.begin(camera, context);

        for (final DirectionalLight dirLight : directionalLights) {
            dirLight.set(0, 0, 0, 0, -1, 0);
        }
        for (final PointLight pointLight : pointLights) {
            pointLight.set(0, 0, 0, 0, 0, 0, 0);
        }
        for (final SpotLight spotLight : spotLights) {
            spotLight.set(0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 1, 0);
        }
        lightsSet = false;

        if (has(u_time)) {
            set(u_time, time += Gdx.graphics.getDeltaTime());
        }
    }

    @Override
    public void render(Renderable renderable, Attributes combinedAttributes) {
        if (!combinedAttributes.has(BlendingAttribute.Type)) {
            context.setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
        bindMaterial(combinedAttributes);
        if (lighting) {
            bindLights(renderable, combinedAttributes);
        }
        super.render(renderable, combinedAttributes);
    }

    @Override
    public boolean canRender(final Renderable renderable) {
        if (renderable.bones != null && renderable.bones.length > config.getNumBones()) {
            return false;
        }

        final long renderableMask = combineAttributeMasks(renderable);
        return (attributesMask == (renderableMask | optionalAttributes))
                && (vertexMask == renderable.meshPart.mesh.getVertexAttributes().getMaskWithSizePacked())
                && (renderable.environment != null) == lighting;
    }

    private long combineAttributeMasks(final Renderable renderable) {
        long mask = 0;
        if (renderable.environment != null) {
            mask |= renderable.environment.getMask();
        }
        if (renderable.material != null) {
            mask |= renderable.material.getMask();
        }
        return mask;
    }


    protected void bindMaterial(final Attributes attributes) {
        int cullFace = config.getDefaultCullFace();
        int depthFunc = config.getDefaultDepthFunc();
        float depthRangeNear = 0f;
        float depthRangeFar = 1f;
        boolean depthMask = true;

        for (final Attribute attr : attributes) {
            final long t = attr.type;
            if (BlendingAttribute.is(t)) {
                context.setBlending(true, ((BlendingAttribute) attr).sourceFunction,
                        ((BlendingAttribute) attr).destFunction);
                set(u_opacity, ((BlendingAttribute) attr).opacity);
            } else if ((t & IntAttribute.CullFace) == IntAttribute.CullFace) {
                cullFace = ((IntAttribute) attr).value;
            } else if ((t & FloatAttribute.AlphaTest) == FloatAttribute.AlphaTest) {
                set(u_alphaTest, ((FloatAttribute) attr).value);
            } else if ((t & DepthTestAttribute.Type) == DepthTestAttribute.Type) {
                DepthTestAttribute dta = (DepthTestAttribute) attr;
                depthFunc = dta.depthFunc;
                depthRangeNear = dta.depthRangeNear;
                depthRangeFar = dta.depthRangeFar;
                depthMask = dta.depthMask;
            } else if (!config.isIgnoreUnimplemented()) {
                throw new GdxRuntimeException("Unknown material attribute: " + attr);
            }
        }

        context.setCullFace(cullFace);
        context.setDepthTest(depthFunc, depthRangeNear, depthRangeFar);
        context.setDepthMask(depthMask);
    }

    protected void bindLights(final Renderable renderable, final Attributes attributes) {
        final Environment lights = renderable.environment;
        final DirectionalLightsAttribute dla =
                attributes.get(DirectionalLightsAttribute.class, DirectionalLightsAttribute.Type);
        final Array<DirectionalLight> dirs = dla == null ? null : dla.lights;
        final PointLightsAttribute pla = attributes.get(PointLightsAttribute.class, PointLightsAttribute.Type);
        final Array<PointLight> points = pla == null ? null : pla.lights;
        final SpotLightsAttribute sla = attributes.get(SpotLightsAttribute.class, SpotLightsAttribute.Type);
        final Array<SpotLight> spots = sla == null ? null : sla.lights;

        if (dirLightsLoc >= 0) {
            processDirectionalLightLocations(dirs);
        }

        if (pointLightsLoc >= 0) {
            processPointLightLocations(points);
        }

        if (spotLightsLoc >= 0) {
            processSpotLightLocations(spots);
        }

        if (attributes.has(ColorAttribute.Fog)) {
            set(u_fogColor, ((ColorAttribute) attributes.get(ColorAttribute.Fog)).color);
        }

        if (lights != null && lights.shadowMap != null) {
            set(u_shadowMapProjViewTrans, lights.shadowMap.getProjViewTrans());
            set(u_shadowTexture, lights.shadowMap.getDepthMap());
            set(u_shadowPCFOffset, 1.f / (2f * lights.shadowMap.getDepthMap().texture.getWidth()));
        }

        lightsSet = true;
    }

    private void processSpotLightLocations(Array<SpotLight> spots) {
        for (int i = 0; i < spotLights.length; i++) {
            if (spots == null || i >= spots.size) {
                if (lightsSet && spotLights[i].intensity == 0f) {
                    continue;
                }
                spotLights[i].intensity = 0f;
            } else if (lightsSet && spotLights[i].equals(spots.get(i))) {
                continue;
            } else {
                spotLights[i].set(spots.get(i));
            }

            int idx = spotLightsLoc + i * spotLightsSize;
            program.setUniformf(idx + spotLightsColorOffset, spotLights[i].color.r * spotLights[i].intensity,
                    spotLights[i].color.g * spotLights[i].intensity,
                    spotLights[i].color.b * spotLights[i].intensity);
            program.setUniformf(idx + spotLightsPositionOffset, spotLights[i].position);
            program.setUniformf(idx + spotLightsDirectionOffset, spotLights[i].direction);
            program.setUniformf(idx + spotLightsCutoffAngleOffset, spotLights[i].cutoffAngle);
            program.setUniformf(idx + spotLightsExponentOffset, spotLights[i].exponent);
            if (spotLightsIntensityOffset >= 0) {
                program.setUniformf(idx + spotLightsIntensityOffset, spotLights[i].intensity);
            }
            if (spotLightsSize <= 0) {
                break;
            }
        }
    }

    private void processPointLightLocations(Array<PointLight> points) {
        for (int i = 0; i < pointLights.length; i++) {
            if (points == null || i >= points.size) {
                if (lightsSet && pointLights[i].intensity == 0f) {
                    continue;
                }
                pointLights[i].intensity = 0f;
            } else if (lightsSet && pointLights[i].equals(points.get(i))) {
                continue;
            } else {
                pointLights[i].set(points.get(i));
            }

            int idx = pointLightsLoc + i * pointLightsSize;
            program.setUniformf(idx + pointLightsColorOffset, pointLights[i].color.r * pointLights[i].intensity,
                    pointLights[i].color.g * pointLights[i].intensity,
                    pointLights[i].color.b * pointLights[i].intensity);
            program.setUniformf(idx + pointLightsPositionOffset, pointLights[i].position.x,
                    pointLights[i].position.y,
                    pointLights[i].position.z);
            if (pointLightsIntensityOffset >= 0) {
                program.setUniformf(idx + pointLightsIntensityOffset, pointLights[i].intensity);
            }
            if (pointLightsSize <= 0) {
                break;
            }
        }
    }

    private void processDirectionalLightLocations(Array<DirectionalLight> dirs) {
        for (int i = 0; i < directionalLights.length; i++) {
            if (dirs == null || i >= dirs.size) {
                if (lightsSet && directionalLights[i].color.r == 0f && directionalLights[i].color.g == 0f
                        && directionalLights[i].color.b == 0f) {
                    continue;
                }
                directionalLights[i].color.set(0, 0, 0, 1);
            } else if (lightsSet && directionalLights[i].equals(dirs.get(i))) {
                continue;
            } else {
                directionalLights[i].set(dirs.get(i));
            }

            int idx = dirLightsLoc + i * dirLightsSize;
            program.setUniformf(idx + dirLightsColorOffset, directionalLights[i].color.r,
                    directionalLights[i].color.g,
                    directionalLights[i].color.b);
            program.setUniformf(idx + dirLightsDirectionOffset, directionalLights[i].direction.x,
                    directionalLights[i].direction.y, directionalLights[i].direction.z);
            if (dirLightsSize <= 0) {
                break;
            }
        }
    }


    /**
     * Method can preprocess shader contents before shader compile.
     *
     * @param renderable for getting attributes
     */
    protected void preprocessShaderContents(Renderable renderable) {
        var attributes = combineAttributes(renderable);
        var sb = new StringBuilder();
        final long attributesMask = attributes.getMask();
        final long vertexMask = renderable.meshPart.mesh.getVertexAttributes().getMask();
        processVertexAttributes(renderable, config, vertexMask, sb, attributes);
        if ((attributesMask & BlendingAttribute.Type) == BlendingAttribute.Type) {
            sb.append("#define " + BlendingAttribute.Alias + "Flag\n");
        }
        processTextureAttributes(attributesMask, sb);
        processColorAttributes(attributesMask, sb);
        if (renderable.bones != null && config.getNumBones() > 0) {
            sb.append("#define numBones ").append(config.getNumBones()).append("\n");
        }

        vertexShader = sb + vertexShader;
        fragmentShader = sb + fragmentShader;
    }

    private void processVertexAttributes(Renderable renderable, ShaderConfig config, long vertexMask,
                                         StringBuilder prefix, Attributes attributes) {
        if (and(vertexMask, VertexAttributes.Usage.Position)) {
            prefix.append("#define positionFlag\n");
        }
        if (or(vertexMask, VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.ColorPacked)) {
            prefix.append("#define colorFlag\n");
        }
        if (and(vertexMask, VertexAttributes.Usage.BiNormal)) {
            prefix.append("#define binormalFlag\n");
        }
        if (and(vertexMask, VertexAttributes.Usage.Tangent)) {
            prefix.append("#define tangentFlag\n");
        }
        if (and(vertexMask, VertexAttributes.Usage.Normal)) {
            prefix.append("#define normalFlag\n");
        }
        if (and(vertexMask, VertexAttributes.Usage.Normal) ||
                and(vertexMask, VertexAttributes.Usage.Tangent | VertexAttributes.Usage.BiNormal)) {
            if (renderable.environment != null) {
                prefix.append("#define lightingFlag\n");
                prefix.append("#define ambientCubemapFlag\n");
                prefix.append("#define numDirectionalLights ").append(config.getNumDirectionalLights()).append("\n");
                prefix.append("#define numPointLights ").append(config.getNumPointLights()).append("\n");
                prefix.append("#define numSpotLights ").append(config.getNumSpotLights()).append("\n");
                if (attributes.has(ColorAttribute.Fog)) {
                    prefix.append("#define fogFlag\n");
                }
                if (renderable.environment.shadowMap != null) {
                    prefix.append("#define shadowMapFlag\n");
                }
                if (attributes.has(CubemapAttribute.EnvironmentMap)) {
                    prefix.append("#define environmentCubemapFlag\n");
                }
            }
        }
        final int n = renderable.meshPart.mesh.getVertexAttributes().size();
        for (int i = 0; i < n; i++) {
            final VertexAttribute attr = renderable.meshPart.mesh.getVertexAttributes().get(i);
            if (attr.usage == VertexAttributes.Usage.BoneWeight) {
                prefix.append("#define boneWeight").append(attr.unit).append("Flag\n");
            } else if (attr.usage == VertexAttributes.Usage.TextureCoordinates) {
                prefix.append("#define texCoord").append(attr.unit).append("Flag\n");
            }
        }
    }

    private static void processTextureAttributes(long attributesMask, StringBuilder prefix) {
        if ((attributesMask & TextureAttribute.Diffuse) == TextureAttribute.Diffuse) {
            // TODO implement UV mapping
            prefix.append("#define " + TextureAttribute.DiffuseAlias + "Flag\n");
            prefix.append("#define " + TextureAttribute.DiffuseAlias + "Coord texCoord0\n");
        }
        if ((attributesMask & TextureAttribute.Specular) == TextureAttribute.Specular) {
            // TODO implement UV mapping
            prefix.append("#define " + TextureAttribute.SpecularAlias + "Flag\n");
            prefix.append("#define " + TextureAttribute.SpecularAlias + "Coord texCoord0\n");
        }
        if ((attributesMask & TextureAttribute.Normal) == TextureAttribute.Normal) {
            // TODO implement UV mapping
            prefix.append("#define " + TextureAttribute.NormalAlias + "Flag\n");
            prefix.append("#define " + TextureAttribute.NormalAlias + "Coord texCoord0\n");
        }
        if ((attributesMask & TextureAttribute.Emissive) == TextureAttribute.Emissive) {
            // TODO implement UV mapping
            prefix.append("#define " + TextureAttribute.EmissiveAlias + "Flag\n");
            prefix.append("#define " + TextureAttribute.EmissiveAlias + "Coord texCoord0\n");
        }
        if ((attributesMask & TextureAttribute.Reflection) == TextureAttribute.Reflection) {
            // TODO implement UV mapping
            prefix.append("#define " + TextureAttribute.ReflectionAlias + "Flag\n");
            prefix.append("#define " + TextureAttribute.ReflectionAlias + "Coord texCoord0\n");
        }
        if ((attributesMask & TextureAttribute.Ambient) == TextureAttribute.Ambient) {
            // TODO implement UV mapping
            prefix.append("#define " + TextureAttribute.AmbientAlias + "Flag\n");
            prefix.append("#define " + TextureAttribute.AmbientAlias + "Coord texCoord0\n");
        }
    }

    private static void processColorAttributes(long attributesMask, StringBuilder prefix) {
        if ((attributesMask & ColorAttribute.Diffuse) == ColorAttribute.Diffuse) {
            prefix.append("#define " + ColorAttribute.DiffuseAlias + "Flag\n");
        }
        if ((attributesMask & ColorAttribute.Specular) == ColorAttribute.Specular) {
            prefix.append("#define " + ColorAttribute.SpecularAlias + "Flag\n");
        }
        if ((attributesMask & ColorAttribute.Emissive) == ColorAttribute.Emissive) {
            prefix.append("#define " + ColorAttribute.EmissiveAlias + "Flag\n");
        }
        if ((attributesMask & ColorAttribute.Reflection) == ColorAttribute.Reflection) {
            prefix.append("#define " + ColorAttribute.ReflectionAlias + "Flag\n");
        }
        if ((attributesMask & FloatAttribute.Shininess) == FloatAttribute.Shininess) {
            prefix.append("#define " + FloatAttribute.ShininessAlias + "Flag\n");
        }
        if ((attributesMask & FloatAttribute.AlphaTest) == FloatAttribute.AlphaTest) {
            prefix.append("#define " + FloatAttribute.AlphaTestAlias + "Flag\n");
        }
    }

    // TODO: Perhaps move responsibility for combining attributes to RenderableProvider?
    private Attributes combineAttributes(final Renderable renderable) {
        tmpAttributes.clear();
        if (renderable.environment != null) {
            tmpAttributes.set(renderable.environment);
        }
        if (renderable.material != null) {
            tmpAttributes.set(renderable.material);
        }
        return tmpAttributes;
    }

    private boolean and(final long mask, final long flag) {
        return (mask & flag) == flag;
    }

    private boolean or(final long mask, final long flag) {
        return (mask & flag) != 0;
    }

    @Override
    public void end() {
        if (context != null) {
            context.end();
        }
    }

    @Override
    public void dispose() {
        if (program != null) {
            program.dispose();
        }
        super.dispose();
    }

    public int registerUniformGlobal(String alias, SetterFunction func) {
        return registerUniformGlobal(alias, 0, func);
    }

    public int registerUniformGlobal(String alias, long overallMask, SetterFunction func) {
        return register(new Uniform(alias, overallMask), func::set);
    }

    public int registerUniformLocal(String alias, SetterFunction func) {
        return registerUniformLocal(alias, 0, func);
    }

    public int registerUniformLocal(String alias, long overallMask, SetterFunction func) {
        return register(new Uniform(alias, overallMask), new LocalSetter() {
            @Override
            public void set(BaseShader shader, int inputID, Renderable renderable,
                            Attributes combinedAttributes) {
                func.set(shader, inputID, renderable, combinedAttributes);
            }
        });
    }

    @RequiredArgsConstructor
    private abstract static class LocalSetterWithAttr<T> implements Setter {
        protected final T attr;

        @Override
        public boolean isGlobal(BaseShader shader, int inputID) {
            return false;
        }
    }

    @RequiredArgsConstructor
    private static class MaterialPartUniformHolder {
        private final int colorUniform;
        private final int textureUniform;
        private final int uVTransformUniform;
    }

    @RequiredArgsConstructor
    private static class ACubemapSetter extends LocalSetter {
        private static final float[] ones = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        private static final Vector3 tmpV1 = new Vector3();
        private final AmbientCubemap cacheAmbientCubemap = new AmbientCubemap();
        private final int dirLightsOffset;
        private final int pointLightsOffset;


        @Override
        public void set(BaseShader shader, int inputID, Renderable renderable,
                        Attributes combinedAttributes) {
            if (renderable.environment == null) {
                shader.program.setUniform3fv(shader.loc(inputID), ones, 0, ones.length);
                return;
            }

            renderable.worldTransform.getTranslation(tmpV1);
            if (combinedAttributes.has(ColorAttribute.AmbientLight)) {
                cacheAmbientCubemap.set(((ColorAttribute) combinedAttributes.get(ColorAttribute.AmbientLight)).color);
            }

            if (combinedAttributes.has(DirectionalLightsAttribute.Type)) {
                Array<DirectionalLight> lights = ((DirectionalLightsAttribute) combinedAttributes
                        .get(DirectionalLightsAttribute.Type)).lights;
                for (int i = dirLightsOffset; i < lights.size; i++) {
                    cacheAmbientCubemap.add(lights.get(i).color, lights.get(i).direction);
                }
            }

            if (combinedAttributes.has(PointLightsAttribute.Type)) {
                Array<PointLight> lights =
                        ((PointLightsAttribute) combinedAttributes.get(PointLightsAttribute.Type)).lights;
                for (int i = pointLightsOffset; i < lights.size; i++) {
                    cacheAmbientCubemap.add(lights.get(i).color, lights.get(i).position, tmpV1,
                            lights.get(i).intensity);
                }
            }

            cacheAmbientCubemap.clamp();
            shader.program.setUniform3fv(shader.loc(inputID), cacheAmbientCubemap.data, 0,
                    cacheAmbientCubemap.data.length);
        }
    }

}
