package net.nevinsky.abyssus.core.shader;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lombok.RequiredArgsConstructor;
import net.nevinsky.abyssus.core.Renderable;

@RequiredArgsConstructor
public class DefaultShader extends BaseShader {

    protected final int UNIFORM_PROJ_VIEW_MATRIX = register(new Uniform("u_projViewMatrix"));
    protected final int UNIFORM_TRANS_MATRIX = register(new Uniform("u_transMatrix"));
    protected final int UNIFORM_CAM_POS = register(new Uniform("u_camPos"));
    protected final int UNIFORM_MATERIAL_DIFFUSE_USE_TEXTURE = register(new Uniform("u_diffuseUseTexture"));
    protected final int UNIFORM_MATERIAL_DIFFUSE_TEXTURE = register(new Uniform("u_diffuseTexture"));
    protected final int UNIFORM_MATERIAL_DIFFUSE_COLOR = register(new Uniform("u_diffuseColor"));

    protected String vertexShader;
    protected String fragmentShader;
    private boolean initialized = false;
    private final Attributes tmpAttributes = new Attributes();

    public DefaultShader(String vertexShader, String fragmentShader) {
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
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
    }

    /**
     * Method can preprocess shader contents before shader compile.
     *
     * @param renderable
     */
    protected void preprocessShaderContents(Renderable renderable) {
        final Attributes attributes = combineAttributes(renderable);
        final long attributesMask = attributes.getMask();

        var sb = new StringBuilder();
        if ((attributesMask & TextureAttribute.Diffuse) == TextureAttribute.Diffuse) {
            sb.append("#define " + TextureAttribute.DiffuseAlias + "Flag\n");
        }
        vertexShader = sb + vertexShader;
        fragmentShader = sb + fragmentShader;
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

    @Override
    public DefaultShader clone() {
        return new DefaultShader(vertexShader, fragmentShader);
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        super.begin(camera, context);

        set(UNIFORM_PROJ_VIEW_MATRIX, camera.combined);
        set(UNIFORM_CAM_POS, camera.position);
    }

    @Override
    public void render(Renderable renderable) {
        set(UNIFORM_TRANS_MATRIX, renderable.worldTransform);
        // texture uniform
        var diffuseTexture = ((TextureAttribute) (renderable.material.get(TextureAttribute.Diffuse)));
        var diffuseColor = ((ColorAttribute) (renderable.material.get(ColorAttribute.Diffuse)));

        if (diffuseTexture != null) {
            set(UNIFORM_MATERIAL_DIFFUSE_TEXTURE, diffuseTexture.textureDescription.texture);
            set(UNIFORM_MATERIAL_DIFFUSE_USE_TEXTURE, 1);
        } else if (diffuseColor != null) {
            set(UNIFORM_MATERIAL_DIFFUSE_COLOR, diffuseColor.color);
            set(UNIFORM_MATERIAL_DIFFUSE_USE_TEXTURE, 0);
        }
        renderable.meshPart.render(program);
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
}
