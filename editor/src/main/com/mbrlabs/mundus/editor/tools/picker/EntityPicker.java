package com.mbrlabs.mundus.editor.tools.picker;

import com.artemis.Aspect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.editor.core.ecs.PickableComponent;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.shader.ShaderStorage;
import com.mbrlabs.mundus.editor.utils.PickerColorEncoder;
import lombok.RequiredArgsConstructor;
import net.nevinsky.abyssus.core.ModelBatch;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntityPicker extends BasePicker {
    private final EditorCtx ctx;
    private final ModelBatch batch;
    private final ShaderStorage shaderStorage;

    public int pick(Scene scene, int screenX, int screenY) {
        begin(ctx.getViewport());
        renderPickableScene(scene);
        end();
        Pixmap pm = getFrameBufferPixmap(ctx.getViewport());

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

//        PixmapIO.writePNG(new FileHandle("/home/inv3r/Development/gamedev/Mundus/editor/src/main/"+
//        "com/mbrlabs/mundus/editor/tools/picker/image.png"), pm);
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
