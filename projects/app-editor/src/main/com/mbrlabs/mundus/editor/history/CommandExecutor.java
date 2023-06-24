package com.mbrlabs.mundus.editor.history;

import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.core.scene.SceneService;
import com.mbrlabs.mundus.editor.history.commands.DeleteCommand;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandExecutor {

    private final EditorCtx ctx;
    private final SceneService sceneService;

    public void execute(DeleteCommand deleteCommand) {
        ctx.getCurrentWorld().delete(deleteCommand.getEntityId());
        sceneService.deleteNode(ctx.getCurrent().getCurrentScene().getRootNode(), deleteCommand.getEntityId());
    }
}
