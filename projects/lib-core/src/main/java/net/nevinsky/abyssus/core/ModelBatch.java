/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package net.nevinsky.abyssus.core;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FlushablePool;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool;
import net.nevinsky.abyssus.core.shader.DefaultShaderProvider;
import net.nevinsky.abyssus.core.shader.Shader;
import net.nevinsky.abyssus.core.shader.ShaderProvider;

/**
 * Batches {@link Renderable} instances, fetches {@link Shader}s for them, sorts them and then renders them. Fetching
 * the shaders is done using a {@link ShaderProvider}, which defaults to {@link DefaultShaderProvider}. Sorting the
 * renderables is done using a {@link RenderableSorter}, which default to {@link DefaultRenderableSorter}.
 * <p>
 * The OpenGL context between the {@link #begin(Camera)} and {@link #end()} call is maintained by the
 * {@link RenderContext}.
 * <p>
 * To provide multiple {@link Renderable}s at once a {@link RenderableProvider} can be used, e.g. a
 * {@link ModelInstance}.
 *
 * @author xoppa, badlogic
 */
public class ModelBatch {
    protected static class RenderablePool extends FlushablePool<Renderable> {
        @Override
        protected Renderable newObject() {
            return new Renderable();
        }

        @Override
        public Renderable obtain() {
            Renderable renderable = super.obtain();
            renderable.cleanup();
            return renderable;
        }
    }

    protected Camera camera;
    protected final RenderablePool renderablesPool = new RenderablePool();
    /**
     * list of Renderables to be rendered in the current batch
     **/
    protected final Array<Renderable> renderables = new Array<>();
    /**
     * the {@link RenderContext}
     **/
    protected final RenderContext context;
    private final boolean ownContext;
    /**
     * the {@link RenderableSorter}
     **/
    protected final RenderableSorter sorter;

    protected final ShaderProvider shaderProvider;

    public ModelBatch(ShaderProvider shaderProvider) {
        this(null, null, shaderProvider);
    }

    /**
     * Construct a ModelBatch, using this constructor makes you responsible for calling context.begin() and
     * context.end() yourself.
     *
     * @param context        The {@link RenderContext} to use.
     * @param sorter         The {@link RenderableSorter} to use.
     * @param shaderProvider
     */
    public ModelBatch(final RenderContext context, final RenderableSorter sorter, ShaderProvider shaderProvider) {
        this.sorter = (sorter == null) ? new DefaultRenderableSorter() : sorter;
        this.ownContext = (context == null);
        this.context =
                (context == null) ? new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.LRU, 1)) : context;
        this.shaderProvider = shaderProvider;
    }

    /**
     * Construct a ModelBatch, using this constructor makes you responsible for calling context.begin() and
     * context.end() yourself.
     *
     * @param context        The {@link RenderContext} to use.
     * @param shaderProvider
     */
    public ModelBatch(final RenderContext context, ShaderProvider shaderProvider) {
        this(context, null, shaderProvider);
    }

    /**
     * Construct a ModelBatch
     *
     * @param sorter         The {@link RenderableSorter} to use.
     * @param shaderProvider
     */
    public ModelBatch(final RenderableSorter sorter, ShaderProvider shaderProvider) {
        this(null, sorter, shaderProvider);
    }

    /**
     * Start rendering one or more {@link Renderable}s. Use one of the render() methods to provide the renderables. Must
     * be followed by a call to {@link #end()}. The OpenGL context must not be altered between {@link #begin(Camera)}
     * and {@link #end()}.
     *
     * @param cam The {@link Camera} to be used when rendering and sorting.
     */
    public void begin(final Camera cam) {
        if (camera != null) {
            throw new GdxRuntimeException("Call end() first.");
        }
        camera = cam;
        if (ownContext) {
            context.begin();
        }
    }

    /**
     * Change the camera in between {@link #begin(Camera)} and {@link #end()}. This causes the batch to be flushed. Can
     * only be called after the call to {@link #begin(Camera)} and before the call to {@link #end()}.
     *
     * @param cam The new camera to use.
     */
    public void setCamera(final Camera cam) {
        if (camera == null) {
            throw new GdxRuntimeException("Call begin() first.");
        }
        if (renderables.size > 0) {
            flush();
        }
        camera = cam;
    }

    /**
     * Provides access to the current camera in between {@link #begin(Camera)} and {@link #end()}. Do not change the
     * camera's values. Use {@link #setCamera(Camera)}, if you need to change the camera.
     *
     * @return The current camera being used or null if called outside {@link #begin(Camera)} and {@link #end()}.
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * Checks whether the {@link RenderContext} returned by {@link #getRenderContext()} is owned and managed by this
     * ModelBatch. When the RenderContext isn't owned by the ModelBatch, you are responsible for calling the
     * {@link RenderContext#begin()} and {@link RenderContext#end()} methods yourself, as well as disposing the
     * RenderContext.
     *
     * @return True if this ModelBatch owns the RenderContext, false otherwise.
     */
    public boolean ownsRenderContext() {
        return ownContext;
    }

    /**
     * @return the {@link RenderContext} used by this ModelBatch.
     */
    public RenderContext getRenderContext() {
        return context;
    }

    /**
     * @return the {@link RenderableSorter} used by this ModelBatch.
     */
    public RenderableSorter getRenderableSorter() {
        return sorter;
    }

    /**
     * Flushes the batch, causing all {@link Renderable}s in the batch to be rendered. Can only be called after the call
     * to {@link #begin(Camera)} and before the call to {@link #end()}.
     */
    public void flush() {
        sorter.sort(camera, renderables);
        Shader currentShader = null;
        for (int i = 0; i < renderables.size; i++) {
            final Renderable renderable = renderables.get(i);
            if (currentShader != renderable.shader) {
                if (currentShader != null) {
                    currentShader.end();
                }
                currentShader = renderable.shader;
                currentShader.begin(camera, context);
            }
            currentShader.render(renderable);
        }
        if (currentShader != null) {
            currentShader.end();
        }
        renderablesPool.flush();
        renderables.clear();
    }

    /**
     * End rendering one or more {@link Renderable}s. Must be called after a call to {@link #begin(Camera)}. This will
     * flush the batch, causing any renderables provided using one of the render() methods to be rendered. After a call
     * to this method the OpenGL context can be altered again.
     */
    public void end() {
        flush();
        if (ownContext) {
            context.end();
        }
        camera = null;
    }

    /**
     * Calls {@link RenderableProvider#getRenderables(Array, Pool)} and adds all returned {@link Renderable} instances
     * to the current batch to be rendered. Any shaders set on the returned renderables will be replaced with the given
     * {@link Shader}. Can only be called after a call to {@link #begin(Camera)} and before a call to {@link #end()}.
     *
     * @param renderableProvider the renderable provider
     * @param shaderKey          the shader key to get shader to use for the renderables
     */
    public void render(final RenderableProvider renderableProvider, String shaderKey) {
        final int offset = renderables.size;
        renderableProvider.getRenderables(renderables, renderablesPool);
        for (int i = offset; i < renderables.size; i++) {
            Renderable renderable = renderables.get(i);
            renderable.shader = shaderProvider.get(shaderKey, renderable);
        }
    }

    /**
     * Calls {@link RenderableProvider#getRenderables(Array, Pool)} and adds all returned {@link Renderable} instances
     * to the current batch to be rendered. Any shaders set on the returned renderables will be replaced with the given
     * {@link Shader}. Can only be called after a call to {@link #begin(Camera)} and before a call to {@link #end()}.
     *
     * @param renderableProviders one or more renderable providers
     * @param shaderKey           the shader key to get shader to use for the renderables
     */
    public <T extends RenderableProvider> void render(final Iterable<T> renderableProviders, String shaderKey) {
        for (final RenderableProvider renderableProvider : renderableProviders) {
            render(renderableProvider, shaderKey);
        }
    }

    /**
     * Calls {@link RenderableProvider#getRenderables(Array, Pool)} and adds all returned {@link Renderable} instances
     * to the current batch to be rendered. Any environment set on the returned renderables will be replaced with the
     * given environment. Any shaders set on the returned renderables will be replaced with the given {@link Shader}.
     * Can only be called after a call to {@link #begin(Camera)} and before a call to {@link #end()}.
     *
     * @param renderableProvider the renderable provider
     * @param environment        the {@link Environment} to use for the renderables
     * @param shaderKey          the shader key to get shader to use for the renderables
     */
    public void render(final RenderableProvider renderableProvider, final Environment environment,
                       final String shaderKey) {
        final int offset =renderables.size;
        renderableProvider.getRenderables(renderables, renderablesPool);
        for (int i = offset; i < renderables.size; i++) {
            Renderable renderable = renderables.get(i);
            renderable.environment = environment;
            renderable.shader = shaderProvider.get(shaderKey, renderable);
        }
    }

    /**
     * Calls {@link RenderableProvider#getRenderables(Array, Pool)} and adds all returned {@link Renderable} instances
     * to the current batch to be rendered. Any environment set on the returned renderables will be replaced with the
     * given environment. Any shaders set on the returned renderables will be replaced with the given {@link Shader}.
     * Can only be called after a call to {@link #begin(Camera)} and before a call to {@link #end()}.
     *
     * @param renderableProviders one or more renderable providers
     * @param environment         the {@link Environment} to use for the renderables
     * @param shaderKey           the shader key to get shader to use for the renderables
     */
    public <T extends RenderableProvider> void render(final Iterable<T> renderableProviders,
                                                      final Environment environment,
                                                      final String shaderKey) {
        for (final RenderableProvider renderableProvider : renderableProviders) {
            render(renderableProvider, environment, shaderKey);
        }
    }
}
