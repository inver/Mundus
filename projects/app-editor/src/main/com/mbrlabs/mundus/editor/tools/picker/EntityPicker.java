package com.mbrlabs.mundus.editor.tools.picker;

import com.artemis.Aspect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.utils.FlushablePool;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.core.ecs.base.RenderComponent;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.editor.core.ecs.PickableComponent;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.shader.ShaderStorage;
import com.mbrlabs.mundus.editor.utils.PickerColorEncoder;
import lombok.extern.slf4j.Slf4j;
import net.nevinsky.abyssus.core.ModelBatch;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EntityPicker extends BasePicker {
    private final EditorCtx ctx;
    private final ModelBatch batch;

    public EntityPicker(EditorCtx ctx, ShaderStorage shaderStorage) {
        this.ctx = ctx;
        batch = new ModelBatch(shaderStorage);
    }

    public int pick(Scene scene, int screenX, int screenY) {
        begin(ctx.getViewport());
        renderPickableScene(scene);
        end();

        var pm = getFrameBufferPixmap(ctx.getViewport());
        //todo write image to home dir if debug enabled
//        PixmapIO.writePNG(new FileHandle(
//                        "/Users/inv3r/Development/gamedev/Mundus/projects/" +
//                                "app-editor/src/main/com/mbrlabs/mundus/editor/tools/picker/entity.png"),
//                pm);

        int x = HdpiUtils.toBackBufferX(screenX - ctx.getViewport().getScreenX());
        int y = HdpiUtils.toBackBufferY(screenY -
                (Gdx.graphics.getHeight() - (ctx.getViewport().getScreenY() + ctx.getViewport().getScreenHeight())));

        int id = PickerColorEncoder.decode(pm.getPixel(x, y));
        try {
            if (scene.getWorld().getEntity(id) != null) {
                return id;
            }
        } catch (IndexOutOfBoundsException e) {
            //ignore
        }

        pm.dispose();
        return -1;
    }

    private void renderPickableScene(Scene scene) {
        batch.begin(ctx.getCurrent().getCamera());
        var entityIds = scene.getWorld().getAspectSubscriptionManager().get(Aspect.all(PickableComponent.class))
                .getEntities();
        if (entityIds.isEmpty()) {
            batch.end();
            return;
        }

        var pickableMapper = scene.getWorld().getMapper(PickableComponent.class);
        var modelMapper = scene.getWorld().getMapper(RenderComponent.class);
        for (int i = 0; i < entityIds.size(); i++) {
            var pickableComponent = pickableMapper.get(entityIds.get(i));
            var renderComponent = modelMapper.get(entityIds.get(i));
            if (renderComponent == null) {
                log.warn("Entity({}) has pickableComponent, but doesn't have render component. Check this entity",
                        entityIds.get(i));
                continue;
            }
            var environment = environmentPool.obtain();
            environment.set(pickableComponent.getPickerIdAttribute());
            renderComponent.getRenderable().render(batch, environment, "picker", 0);
        }
        batch.end();
    }
}
