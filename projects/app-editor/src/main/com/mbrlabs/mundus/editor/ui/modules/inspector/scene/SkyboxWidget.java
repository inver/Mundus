package com.mbrlabs.mundus.editor.ui.modules.inspector.scene;

import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.mbrlabs.mundus.editor.config.UiComponentHolder;
import com.mbrlabs.mundus.editor.ui.AppUi;
import com.mbrlabs.mundus.editor.ui.modules.inspector.BaseInspectorWidget;
import com.mbrlabs.mundus.editor.ui.modules.outline.ClickButtonListener;
import com.mbrlabs.mundus.editor.ui.widgets.ImageChooserField;
import org.jetbrains.annotations.NotNull;

public class SkyboxWidget extends BaseInspectorWidget {


    private final ImageChooserField positiveX;
    private final ImageChooserField negativeX;
    private final ImageChooserField positiveY;
    private final ImageChooserField negativeY;
    private final ImageChooserField positiveZ;
    private final ImageChooserField negativeZ;

    private final VisTextButton createBtn;
    private final VisTextButton defaultBtn;
    private final VisTextButton deleteBtn;

    private final AppUi appUi;

    public SkyboxWidget(AppUi appUi, @NotNull UiComponentHolder uiComponentHolder, FileChooser fileChooser) {
        super(uiComponentHolder, "Skybox");
        this.appUi = appUi;

        //todo replace file chooser to FileChooserFieldPresenter
        positiveX = new ImageChooserField(appUi, 100, fileChooser);
        negativeX = new ImageChooserField(appUi, 100, fileChooser);
        positiveY = new ImageChooserField(appUi, 100, fileChooser);
        negativeY = new ImageChooserField(appUi, 100, fileChooser);
        positiveZ = new ImageChooserField(appUi, 100, fileChooser);
        negativeZ = new ImageChooserField(appUi, 100, fileChooser);

        createBtn = uiComponentHolder.getButtonFactory().createButton("Create");
        deleteBtn = uiComponentHolder.getButtonFactory().createButton("Remove");
        defaultBtn = uiComponentHolder.getButtonFactory().createButton("Create default");

        //todo
        //eventBus.register(this)
        setupUI();
        setupListeners();
    }

    private void setupUI() {
        positiveX.setButtonText("positiveX");
        negativeX.setButtonText("negativeX");
        positiveY.setButtonText("positiveY");
        negativeY.setButtonText("negativeY");
        positiveZ.setButtonText("positiveZ");
        negativeZ.setButtonText("negativeZ");

        var root = new VisTable();
        // root.debugAll();
        root.padTop(6f).padRight(6f).padBottom(22f);
        getCollapsibleContent().add(root).left().top();
        root.add(new VisLabel("The 6 images must be square and of equal size")).colspan(3).row();
        root.addSeparator().colspan(3).row();
        root.add(positiveX);
        root.add(negativeX);
        root.add(positiveY).row();
        root.add(negativeY);
        root.add(positiveZ);
        root.add(negativeZ).row();
        root.add(createBtn).padTop(15f).padLeft(6f).padRight(6f).expandX().fillX().colspan(3).row();

        var tab = new VisTable();
        tab.add(defaultBtn).expandX().padRight(3f).fillX();
        tab.add(deleteBtn).expandX().fillX().padLeft(3f).row();
        root.add(tab).fillX().expandX().padTop(5f).padLeft(6f).padRight(6f).colspan(3).row();
    }

    private void setupListeners() {
        createBtn.addListener(new ClickButtonListener(() -> {
//                val scene = ctx.current.currentScene;
//                val oldSkybox = scene.skyboxName
//                oldSkybox?.dispose()

//                scene.skybox = Skybox(
//                    positiveX.file, negativeX.file,
//                    positiveY.file, negativeY.file, positiveZ.file, negativeZ.file
//                )
            resetImages();
        }));

        // default skybox btn
        defaultBtn.addListener(new ClickButtonListener(() -> {
//            var scene = ctx.current.currentScene;

//                if (scene.skyboxName != null) {
//                    scene.skyboxName.dispose()
//                }
//                scene.skybox = createDefaultSkybox()
            resetImages();
        }));

        // delete skybox btn
        deleteBtn.addListener(new ClickButtonListener(() -> {
//            var scene = ctx.current.currentScene

//                scene.skyboxName.dispose()
//                scene.skybox = null
            resetImages();
        }));

    }

    private void resetImages() {
//        var skybox = ctx.current.getCurrentScene().skyboxName
//        if (skybox != null) {
//            positiveX.setImage(skybox.positiveX)
//            negativeX.setImage(skybox.negativeX)
//            positiveY.setImage(skybox.positiveY)
//            negativeY.setImage(skybox.negativeY)
//            positiveZ.setImage(skybox.positiveY)
//            negativeZ.setImage(skybox.negativeZ)
//        } else {
//            positiveX.setImage(null)
//            negativeX.setImage(null)
//            positiveY.setImage(null)
//            negativeY.setImage(null)
//            positiveZ.setImage(null)
//            negativeZ.setImage(null)
//        }
    }
//
//    override fun
//
//    onProjectChanged(event:ProjectChangedEvent) {
//        resetImages()
//    }
//
//    override fun
//
//    onSceneChanged(event:SceneChangedEvent) {
//        resetImages()
//    }


    @Override
    public void onDelete() {

    }

    @Override
    public void setValues(int entityId) {

    }
}
