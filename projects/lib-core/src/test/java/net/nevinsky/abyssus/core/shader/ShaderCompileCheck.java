package net.nevinsky.abyssus.core.shader;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.nevinsky.abyssus.core.Renderable;
import net.nevinsky.abyssus.core.mesh.Mesh;
import net.nevinsky.abyssus.core.mesh.MeshPart;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Compiles shaders for many combinations of vertex layout, material and environment in a hidden OpenGL window. GLSL
 * errors only show up at runtime, so this is the safety net for shader changes. It needs a display, so it is not part
 * of {@code check}: run {@code ./gradlew :lib-core:shaderCheck}. Exits with a non-zero status if a variant fails.
 * <p>
 * It also renders a lit quad into an offscreen buffer and checks the pixel values (see {@link #renderChecks()}),
 * because a shader that compiles can still light wrongly.
 */
public final class ShaderCompileCheck extends ApplicationAdapter {

    /** Shader name to factory. The GLSL comes from the defaults of lib-core, like in production. */
    static final Map<String, BiFunction<ShaderConfig, Renderable, DefaultShader>> SHADERS = new LinkedHashMap<>();

    static {
        SHADERS.put("default", DefaultShader::new);
        SHADERS.put("pbr", PbrShader::new);
    }

    private final List<String> failures = new ArrayList<>();
    private int compiled;

    public static void main(String[] args) {
        var config = new Lwjgl3ApplicationConfiguration();
        config.setInitialVisible(false);
        config.setWindowedMode(16, 16);
        var check = new ShaderCompileCheck();
        new Lwjgl3Application(check, config);
        System.exit(check.failures.isEmpty() ? 0 : 1);
    }

    @Override
    public void create() {
        try {
            run();
            renderChecks();
            pbrRenderChecks();
            bundledShaderChecks();
        } catch (Throwable t) {
            t.printStackTrace();
            failures.add("harness error: " + t);
        } finally {
            System.out.println("SHADERCHECK compiled=" + compiled + " failed=" + failures.size());
            failures.forEach(f -> System.out.println("SHADERCHECK FAIL " + f));
            Gdx.app.exit();
        }
    }

    private void run() {
        var texture = new Texture(new Pixmap(1, 1, Pixmap.Format.RGBA8888));

        Map<String, Supplier<VertexAttribute[]>> layouts = new LinkedHashMap<>();
        layouts.put("pos", () -> new VertexAttribute[]{VertexAttribute.Position()});
        layouts.put("pos+normal", () -> new VertexAttribute[]{VertexAttribute.Position(), VertexAttribute.Normal()});
        layouts.put("pos+normal+uv", () -> new VertexAttribute[]{VertexAttribute.Position(), VertexAttribute.Normal(),
                VertexAttribute.TexCoords(0)});
        layouts.put("pos+normal+uv+tangent", () -> new VertexAttribute[]{VertexAttribute.Position(),
                VertexAttribute.Normal(), VertexAttribute.TexCoords(0), VertexAttribute.Tangent(),
                VertexAttribute.Binormal()});
        layouts.put("pos+normal+color+uv", () -> new VertexAttribute[]{VertexAttribute.Position(),
                VertexAttribute.Normal(), VertexAttribute.ColorUnpacked(), VertexAttribute.TexCoords(0)});
        layouts.put("skinned", () -> new VertexAttribute[]{VertexAttribute.Position(), VertexAttribute.Normal(),
                VertexAttribute.TexCoords(0), VertexAttribute.BoneWeight(0), VertexAttribute.BoneWeight(1),
                VertexAttribute.BoneWeight(2), VertexAttribute.BoneWeight(3)});

        Map<String, Supplier<Material>> materials = new LinkedHashMap<>();
        materials.put("empty", Material::new);
        materials.put("diffuseColor", () -> new Material(ColorAttribute.createDiffuse(Color.RED)));
        materials.put("diffuseTex", () -> new Material(TextureAttribute.createDiffuse(texture)));
        materials.put("diffuseTex+normalTex", () -> new Material(TextureAttribute.createDiffuse(texture),
                TextureAttribute.createNormal(texture)));
        materials.put("diffuse+specular+shininess", () -> new Material(ColorAttribute.createDiffuse(Color.RED),
                ColorAttribute.createSpecular(Color.WHITE), FloatAttribute.createShininess(32f)));
        materials.put("diffuseTex+specularTex+emissiveTex", () -> new Material(TextureAttribute.createDiffuse(texture),
                TextureAttribute.createSpecular(texture), TextureAttribute.createEmissive(texture)));
        materials.put("blended+alphaTest", () -> new Material(ColorAttribute.createDiffuse(Color.RED),
                new BlendingAttribute(0.5f), FloatAttribute.createAlphaTest(0.3f)));
        materials.put("normalTexOnly", () -> new Material(TextureAttribute.createNormal(texture)));
        materials.put("pbr factors", () -> new Material(ColorAttribute.createDiffuse(Color.RED),
                PBRFloatAttribute.createMetallic(0.5f), PBRFloatAttribute.createRoughness(0.5f)));
        materials.put("pbr all textures", () -> new Material(ColorAttribute.createDiffuse(Color.WHITE),
                ColorAttribute.createEmissive(Color.BLACK), TextureAttribute.createDiffuse(texture),
                TextureAttribute.createNormal(texture), TextureAttribute.createEmissive(texture),
                PBRFloatAttribute.createMetallic(1f), PBRFloatAttribute.createRoughness(1f),
                PBRTextureAttribute.createMetallicRoughnessTexture(texture),
                PBRTextureAttribute.createOcclusionTexture(texture)));
        materials.put("pbr blended+alphaTest", () -> new Material(ColorAttribute.createDiffuse(Color.RED),
                PBRFloatAttribute.createMetallic(0f), PBRFloatAttribute.createRoughness(1f),
                new BlendingAttribute(0.5f), FloatAttribute.createAlphaTest(0.3f)));

        Map<String, Supplier<Environment>> environments = new LinkedHashMap<>();
        environments.put("noEnv", () -> null);
        environments.put("lights", () -> {
            var env = new Environment();
            env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1f));
            env.add(new DirectionalLight().set(1f, 1f, 1f, -1f, -1f, -1f));
            env.add(new PointLight().set(1f, 1f, 1f, 0f, 2f, 0f, 5f));
            return env;
        });
        environments.put("lights+fog", () -> {
            var env = new Environment();
            env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1f));
            env.set(new ColorAttribute(ColorAttribute.Fog, 0.5f, 0.5f, 0.5f, 1f));
            env.add(new DirectionalLight().set(1f, 1f, 1f, -1f, -1f, -1f));
            return env;
        });

        for (var shaderEntry : SHADERS.entrySet()) {
            for (var layout : layouts.entrySet()) {
                for (var material : materials.entrySet()) {
                    for (var env : environments.entrySet()) {
                        var name = shaderEntry.getKey() + " | " + layout.getKey() + " | " + material.getKey() + " | "
                                + env.getKey();
                        compile(name, shaderEntry.getValue(), layout.getValue().get(), material.getValue().get(),
                                env.getValue().get(), layout.getKey().equals("skinned"));
                    }
                }
            }
        }
    }

    // ---------------------------------------------------------------------------------------------------------------
    // Render checks
    // ---------------------------------------------------------------------------------------------------------------

    private static final int SIZE = 8;

    private void renderChecks() {
        var white = Color.WHITE;
        var down45 = new Vector3(0f, -1f, -1f);

        // no lighting: plain diffuse color
        expect("unlit diffuse red", renderDefault(false, new Material(ColorAttribute.createDiffuse(Color.RED)), null),
                255, 0, 0, 2);

        // directional light travelling along -Z lights a quad facing +Z fully
        expect("directional 0deg", renderDefault(false, diffuse(white), light(0, 0, -1, 0f)), 255, 255, 255, 3);
        // 45 degrees: cos = 0.7071
        expect("directional 45deg", renderDefault(false, diffuse(white), light(down45.x, down45.y, down45.z, 0f)),
                180, 180, 180, 3);
        // light from behind: black
        expect("directional from behind", renderDefault(false, diffuse(white), light(0, 0, 1, 0f)), 0, 0, 0, 2);
        // ambient only
        expect("ambient only", renderDefault(false, diffuse(white), ambientOnly(0.2f)), 51, 51, 51, 3);
        // diffuse color multiplies the light
        expect("directional red", renderDefault(false, diffuse(Color.RED), light(0, 0, -1, 0f)), 255, 0, 0, 3);

        for (boolean tangents : new boolean[]{true, false}) {
            var mode = tangents ? "tangent attributes" : "derivative frame";
            // flat normal map == no normal map
            expect("flat normal map, " + mode,
                    renderDefault(tangents, normalMapped(128, 128, 255), light(0, 0, -1, 0f)), 255, 255, 255, 4);
            // normal tilted fully towards +X (the tangent): perpendicular to the light
            expect("normal map +X lit along -Z, " + mode,
                    renderDefault(tangents, normalMapped(255, 128, 128), light(0, 0, -1, 0f)), 0, 0, 0, 4);
            // normal tilted fully towards +Y (the bitangent): lit by a light travelling along -Y
            expect("normal map +Y lit from above, " + mode,
                    renderDefault(tangents, normalMapped(128, 255, 128), light(0, -1, 0, 0f)), 255, 255, 255, 4);
            expect("normal map -Y lit from above, " + mode,
                    renderDefault(tangents, normalMapped(128, 0, 128), light(0, -1, 0, 0f)), 0, 0, 0, 4);
            // +X tilt lit by a light travelling along -X
            expect("normal map +X lit from +X, " + mode,
                    renderDefault(tangents, normalMapped(255, 128, 128), light(-1, 0, 0, 0f)), 255, 255, 255, 4);
        }
    }

    private void pbrRenderChecks() {
        // white dielectric lit head on: (1 - F) + specular ~ 0.97
        expect("pbr dielectric", renderQuad("pbr", false, pbr(Color.WHITE, 0f, 1f), light(0, 0, -1, 0f)), 247, 247,
                247, 4);
        // white metal, rough: only the specular lobe, 0.25
        expect("pbr rough metal", renderQuad("pbr", false, pbr(Color.WHITE, 1f, 1f), light(0, 0, -1, 0f)), 64, 64, 64,
                4);
        // the metal tints the reflection
        expect("pbr red metal", renderQuad("pbr", false, pbr(Color.RED, 1f, 1f), light(0, 0, -1, 0f)), 64, 0, 0, 4);
        // light from behind
        expect("pbr from behind", renderQuad("pbr", false, pbr(Color.WHITE, 0f, 1f), light(0, 0, 1, 0f)), 0, 0, 0, 2);
        // ambient only: 0.2 diffuse + a little environment specular
        expect("pbr ambient", renderQuad("pbr", false, pbr(Color.WHITE, 0f, 1f), ambientOnly(0.2f)), 52, 52, 52, 3);
        // smooth metal has a much brighter highlight than rough metal head on
        var smooth = renderQuad("pbr", false, pbr(Color.WHITE, 1f, 0f), light(0, 0, -1, 0f));
        if (smooth != null && smooth[0] < 200) {
            failures.add("render: pbr smooth metal highlight should be bright but was " + smooth[0]);
        }
        // occlusion texture 0 removes the ambient light
        expect("pbr occlusion", renderQuad("pbr", false, pbrWithOcclusion(0), ambientOnly(0.2f)), 0, 0, 0, 2);
        // roughness comes from the green and metallic from the blue channel of the metallic-roughness texture
        // green 255 keeps roughness 1, blue 0 removes the metallic factor: rough dielectric
        var mr = renderQuad("pbr", false, pbrWithMetallicRoughness(0, 255, 0), light(0, 0, -1, 0f));
        expect("pbr metallic-roughness texture: blue 0 means dielectric", mr, 247, 247, 247, 4);
        // green 0 means roughness 0 (smooth): a smooth dielectric highlight head on saturates
        expect("pbr metallic-roughness texture: green 0 means smooth",
                renderQuad("pbr", false, pbrWithMetallicRoughness(0, 0, 0), light(0, 0, -1, 0f)), 255, 255, 255, 2);

        for (boolean tangents : new boolean[]{true, false}) {
            var mode = tangents ? "tangent attributes" : "derivative frame";
            // texel 128 decodes to 0.004, not 0, so the normal is not exactly perpendicular to the light and the
            // physically based grazing specular is not exactly 0 (about 6 of 255): hence the larger tolerance
            expect("pbr normal map +X lit along -Z, " + mode,
                    renderQuad("pbr", tangents, pbrNormalMapped(255, 128, 128), light(0, 0, -1, 0f)), 0, 0, 0, 8);
            expect("pbr normal map +Y lit from above, " + mode,
                    renderQuad("pbr", tangents, pbrNormalMapped(128, 255, 128), light(0, -1, 0, 0f)), 249, 249, 249,
                    6);
            expect("pbr normal map -Y lit from above, " + mode,
                    renderQuad("pbr", tangents, pbrNormalMapped(128, 0, 128), light(0, -1, 0, 0f)), 0, 0, 0, 4);
        }
    }

    private static Material pbr(Color base, float metallic, float roughness) {
        return new Material(ColorAttribute.createDiffuse(base), PBRFloatAttribute.createMetallic(metallic),
                PBRFloatAttribute.createRoughness(roughness));
    }

    private Texture solid(int r, int g, int b) {
        var pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(r / 255f, g / 255f, b / 255f, 1f);
        pixmap.fill();
        return new Texture(pixmap);
    }

    private Material pbrNormalMapped(int r, int g, int b) {
        var material = pbr(Color.WHITE, 0f, 1f);
        material.set(TextureAttribute.createNormal(solid(r, g, b)));
        return material;
    }

    private Material pbrWithOcclusion(int value) {
        var material = pbr(Color.WHITE, 0f, 1f);
        material.set(PBRTextureAttribute.createOcclusionTexture(solid(value, value, value)));
        return material;
    }

    private Material pbrWithMetallicRoughness(int r, int g, int b) {
        var material = pbr(Color.WHITE, 1f, 1f);
        material.set(PBRTextureAttribute.createMetallicRoughnessTexture(solid(r, g, b)));
        return material;
    }

    private int[] renderDefault(boolean tangents, Material material, Environment environment) {
        return renderQuad("default", tangents, material, environment);
    }

    // ---------------------------------------------------------------------------------------------------------------
    // Bundled editor shaders (not based on DefaultShader): compiled as is, the terrain also rendered
    // ---------------------------------------------------------------------------------------------------------------

    private static final Path BUNDLED = Path.of(System.getProperty("bundledShaders",
            "../app-editor/assets/bundled/shaders"));

    private void bundledShaderChecks() {
        if (!Files.isDirectory(BUNDLED)) {
            failures.add("bundled shaders not found: " + BUNDLED.toAbsolutePath());
            return;
        }
        // name, vertex, fragment, prefix
        var programs = new String[][]{
                {"terrain/terrain.vert.glsl", "terrain/terrain.frag.glsl", ""},
                {"terrain/terrain.vert.glsl", "terrain/terrain.frag.glsl", "#define PICKER\n"},
                {"sky/sky.vert.glsl", "sky/sky.frag.glsl", ""},
                {"skyboxShader/skybox.vert.glsl", "skyboxShader/skybox.frag.glsl", ""},
                {"picker/picker.vert.glsl", "picker/picker.frag.glsl", ""},
                {"wireframe/wire.vert.glsl", "wireframe/wire.frag.glsl", ""},
        };
        for (var p : programs) {
            var name = "bundled " + p[0] + " + " + p[1] + (p[2].isEmpty() ? "" : " (" + p[2].trim() + ")");
            var program = new ShaderProgram(p[2] + readBundled(p[0]), p[2] + readBundled(p[1]));
            if (program.isCompiled()) {
                compiled++;
            } else {
                failures.add(name + "\n" + program.getLog());
            }
            program.dispose();
        }
        terrainNormalCheck();
    }

    /**
     * A plane with the normal (0, 1, 1) / sqrt(2) scaled by 2 along Y: the world normal is (0, 1, 2) / sqrt(5). Lit
     * along that normal the terrain shader must give the full brightness of its neutral base color (0.8); transforming
     * the normal with the model matrix instead gives (0, 2, 1) / sqrt(5) and only 0.64.
     */
    private void terrainNormalCheck() {
        var program = new ShaderProgram(readBundled("terrain/terrain.vert.glsl"),
                readBundled("terrain/terrain.frag.glsl"));
        if (!program.isCompiled()) {
            return; // reported by the compile check
        }
        var mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.Normal(),
                VertexAttribute.TexCoords(0));
        float n = (float) (1 / Math.sqrt(2));
        mesh.setVertices(new float[]{
                -1, -1, 1, 0, n, n, 0, 0,
                1, -1, 1, 0, n, n, 1, 0,
                1, 1, -1, 0, n, n, 1, 1,
                -1, 1, -1, 0, n, n, 0, 1});
        mesh.setIndices(new int[]{0, 1, 2, 2, 3, 0});

        var camera = new OrthographicCamera(2f, 2f);
        camera.position.set(0f, 0f, 5f);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 0.1f;
        camera.far = 10f;
        camera.update();

        var buffer = new FrameBuffer(Pixmap.Format.RGBA8888, SIZE, SIZE, true);
        try {
            buffer.begin();
            Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            Gdx.gl.glDisable(GL20.GL_CULL_FACE);
            program.bind();
            program.setUniformMatrix("u_transMatrix", new Matrix4().scl(1f, 2f, 1f));
            program.setUniformMatrix("u_projViewMatrix", camera.combined);
            program.setUniformf("u_camPos", camera.position);
            program.setUniformf("u_fogDensity", 0f);
            program.setUniformf("u_fogGradient", 0f);
            program.setUniformf("u_fogColor", 0f, 0f, 0f, 1f);
            program.setUniformf("u_terrainSize", 1f, 1f);
            program.setUniformi("u_texture_has_splatmap", 0);
            program.setUniformi("u_texture_has_diffuse", 0);
            program.setUniformf("u_ambientLight.color", 0f, 0f, 0f, 1f);
            program.setUniformf("u_ambientLight.intensity", 0f);
            program.setUniformf("u_directionalLight.color", 1f, 1f, 1f, 1f);
            program.setUniformf("u_directionalLight.direction", 0f, -1f / (float) Math.sqrt(5),
                    -2f / (float) Math.sqrt(5));
            program.setUniformf("u_directionalLight.intensity", 1f);
            mesh.render(program, GL20.GL_TRIANGLES);
            var pixmap = Pixmap.createFromFrameBuffer(0, 0, SIZE, SIZE);
            buffer.end();
            int pixel = pixmap.getPixel(SIZE / 2, SIZE / 2);
            pixmap.dispose();
            expect("terrain normal under non uniform scale",
                    new int[]{(pixel >>> 24) & 0xff, (pixel >>> 16) & 0xff, (pixel >>> 8) & 0xff}, 204, 204, 204, 5);
        } catch (Throwable t) {
            t.printStackTrace();
            failures.add("terrain render error: " + t);
        } finally {
            buffer.dispose();
            mesh.dispose();
            program.dispose();
        }
    }

    private static String readBundled(String file) {
        try {
            return Files.readString(BUNDLED.resolve(file));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Material diffuse(Color color) {
        return new Material(ColorAttribute.createDiffuse(color));
    }

    private Material normalMapped(int r, int g, int b) {
        var pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(r / 255f, g / 255f, b / 255f, 1f);
        pixmap.fill();
        return new Material(ColorAttribute.createDiffuse(Color.WHITE),
                TextureAttribute.createNormal(new Texture(pixmap)));
    }

    private static Environment light(float dx, float dy, float dz, float ambient) {
        var env = new Environment();
        env.set(new ColorAttribute(ColorAttribute.AmbientLight, ambient, ambient, ambient, 1f));
        env.add(new DirectionalLight().set(1f, 1f, 1f, dx, dy, dz));
        return env;
    }

    private static Environment ambientOnly(float ambient) {
        var env = new Environment();
        env.set(new ColorAttribute(ColorAttribute.AmbientLight, ambient, ambient, ambient, 1f));
        return env;
    }

    private void expect(String name, int[] actual, int r, int g, int b, int tolerance) {
        if (actual == null) {
            return; // render error is already recorded
        }
        if (Math.abs(actual[0] - r) > tolerance || Math.abs(actual[1] - g) > tolerance
                || Math.abs(actual[2] - b) > tolerance) {
            failures.add("render: " + name + ": expected (" + r + "," + g + "," + b + ") +-" + tolerance
                    + " but was (" + actual[0] + "," + actual[1] + "," + actual[2] + ")");
        } else {
            compiled++;
        }
    }

    /**
     * Renders a quad in the XY plane facing +Z (u along +X, glTF style v pointing down) and returns the RGB of the
     * center pixel. With {@code tangents} the quad has tangent (+X) and binormal (+Y) attributes.
     */
    private int[] renderQuad(String shaderName, boolean tangents, Material material, Environment environment) {
        var attributes = new ArrayList<VertexAttribute>();
        attributes.add(VertexAttribute.Position());
        attributes.add(VertexAttribute.Normal());
        attributes.add(VertexAttribute.TexCoords(0));
        if (tangents) {
            attributes.add(VertexAttribute.Tangent());
            attributes.add(VertexAttribute.Binormal());
        }
        var corners = new float[][]{{-1, -1}, {1, -1}, {1, 1}, {-1, 1}};
        var vertices = new ArrayList<Float>();
        for (var c : corners) {
            vertices.addAll(List.of(c[0], c[1], 0f, 0f, 0f, 1f, (c[0] + 1) / 2, 1 - (c[1] + 1) / 2));
            if (tangents) {
                vertices.addAll(List.of(1f, 0f, 0f, 0f, 1f, 0f));
            }
        }
        var data = new float[vertices.size()];
        for (int i = 0; i < data.length; i++) {
            data[i] = vertices.get(i);
        }
        var mesh = new Mesh(true, 4, 6, attributes.toArray(new VertexAttribute[0]));
        mesh.setVertices(data);
        mesh.setIndices(new int[]{0, 1, 2, 2, 3, 0});

        var renderable = new Renderable();
        renderable.meshPart.set("quad", mesh, 0, 6, GL20.GL_TRIANGLES);
        renderable.material = material;
        renderable.environment = environment;

        var camera = new OrthographicCamera(2f, 2f);
        camera.position.set(0f, 0f, 5f);
        camera.lookAt(0f, 0f, 0f);
        camera.near = 0.1f;
        camera.far = 10f;
        camera.update();

        var buffer = new FrameBuffer(Pixmap.Format.RGBA8888, SIZE, SIZE, true);
        DefaultShader shader = null;
        try {
            shader = SHADERS.get(shaderName).apply(new ShaderConfig(), renderable);
            shader.init(renderable);

            var context = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.ROUNDROBIN, 1));
            buffer.begin();
            Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            context.begin();
            shader.begin(camera, context);
            shader.render(renderable);
            shader.end();
            context.end();
            var pixmap = Pixmap.createFromFrameBuffer(0, 0, SIZE, SIZE);
            buffer.end();

            int pixel = pixmap.getPixel(SIZE / 2, SIZE / 2);
            pixmap.dispose();
            return new int[]{(pixel >>> 24) & 0xff, (pixel >>> 16) & 0xff, (pixel >>> 8) & 0xff};
        } catch (Throwable t) {
            t.printStackTrace();
            failures.add("render error: " + t);
            return null;
        } finally {
            if (shader != null) {
                shader.dispose();
            }
            buffer.dispose();
            mesh.dispose();
        }
    }

    private void compile(String name, BiFunction<ShaderConfig, Renderable, DefaultShader> factory,
                         VertexAttribute[] attributes, Material material, Environment environment, boolean skinned) {
        var mesh = new Mesh(true, 3, 3, attributes);
        var renderable = new Renderable();
        renderable.meshPart.set("part", mesh, 0, 3, com.badlogic.gdx.graphics.GL20.GL_TRIANGLES);
        renderable.material = material;
        renderable.environment = environment;
        if (skinned) {
            renderable.bones = new Matrix4[]{new Matrix4(), new Matrix4()};
        }
        try {
            var shader = factory.apply(new ShaderConfig(), renderable);
            shader.init(renderable);
            shader.dispose();
            compiled++;
        } catch (Throwable t) {
            failures.add(name + "\n" + t.getMessage());
        } finally {
            mesh.dispose();
        }
    }
}
