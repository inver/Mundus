package com.mbrlabs.mundus.editor.tools.picker;

import com.artemis.Aspect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.PixmapIO;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.editor.core.ecs.PickableComponent;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.shader.ShaderStorage;
import com.mbrlabs.mundus.editor.utils.PickerColorEncoder;
import net.nevinsky.abyssus.core.ModelBatch;
import org.springframework.stereotype.Component;

@Component
public class EntityPicker extends BasePicker {
    private final EditorCtx ctx;
    private final ShaderStorage shaderStorage;

    private final ModelBatch batch;

    public EntityPicker(EditorCtx ctx, ShaderStorage shaderStorage) {
        this.ctx = ctx;
        this.shaderStorage = shaderStorage;
        batch = new ModelBatch(shaderStorage);
    }

    public int pick(Scene scene, int screenX, int screenY) {
        begin(ctx.getViewport());
        renderPickableScene(scene);
        end();
        var pm = getFrameBufferPixmap(ctx.getViewport());

        PixmapIO.writePNG(new FileHandle(
                        "/home/inv3r/Development/gamedev/Mundus/projects/app-editor/src/main/com/mbrlabs/mundus" +
                                "/editor/tools/picker/entity_image.png"),
                pm);

        int x = screenX - ctx.getViewport().getScreenX();
        int y = screenY -
                (Gdx.graphics.getHeight() - (ctx.getViewport().getScreenY() + ctx.getViewport().getScreenHeight()));

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
        var mapper = scene.getWorld().getMapper(PickableComponent.class);
        for (int i = 0; i < entityIds.size(); i++) {
            mapper.get(entityIds.get(i)).getRenderable().render(batch, scene.getEnvironment(), shaderStorage, 0);
        }
        batch.end();
    }
}
