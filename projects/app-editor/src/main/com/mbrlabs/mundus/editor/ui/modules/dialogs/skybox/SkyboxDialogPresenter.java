package com.mbrlabs.mundus.editor.ui.modules.dialogs.skybox;

import com.mbrlabs.mundus.commons.assets.meta.Meta;
import com.mbrlabs.mundus.commons.assets.meta.MetaService;
import com.mbrlabs.mundus.commons.assets.skybox.SkyboxAsset;
import com.mbrlabs.mundus.commons.assets.skybox.SkyboxMeta;
import com.mbrlabs.mundus.editor.core.assets.EditorAssetManager;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.ui.ClickButtonListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SkyboxDialogPresenter {

    private final EditorCtx ctx;
    private final EditorAssetManager assetManager;
    private final MetaService metaService;

    public void initSaveButton(SkyboxDialog dialog) {
        dialog.getSaveBtn().addListener(new ClickButtonListener(() -> {
            SkyboxAsset asset = assetManager.loadCurrentProjectAsset(dialog.getNameField().getText());
            Meta<SkyboxMeta> meta;
            if (asset != null) {
                //update existing
                meta = (Meta<SkyboxMeta>) asset.getMeta();
                var skyboxMeta = meta.getAdditional();
//                checkFile(meta.getFile(), skyboxMeta.getBack())
//                skyboxMeta.setBack(dialog.getBack().getFile().);

            } else {
                //create new skybox
            }


        }));
    }
}
