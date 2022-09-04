package com.mbrlabs.mundus.editor.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.mbrlabs.mundus.editor.ui.widgets.RenderWidget;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class AppUi extends Stage {

    //    private final Registry registry;
    @Getter
    private final RenderWidget sceneWidget;
//    private final Separator.SeparatorStyle greenSeparatorStyle;
////    private final MundusMenuBar menuBar;
//    private final MundusToolbar toolbar;
//    private final Outline outline;
//    private final Inspector inspector;
//
//    @Getter
//    private final DockBar docker;

    public AppUi() {
        super(new ScreenViewport());
//        this.registry = registry;
//
////        this.menuBar = new MundusMenuBar(registry, );
//        this.toolbar = toolbar;
//        this.outline = outline;
//        this.inspector = inspector;
//
        sceneWidget = new RenderWidget();
//
//        FileChooser.setDefaultPrefsName("com.mbrlabs.mundus.editor");
//        greenSeparatorStyle = new Separator.SeparatorStyle(VisUI.getSkin().getDrawable("mundus-separator-green"), 1);
//
//        var root = new VisTable();
//        addActor(root);
//        root.setFillParent(true);
//
//        var mainContainer = new VisTable();
//        var splitPane = new MundusSplitPane(mainContainer, null, true);
//
//        // row 1: add menu
////        root.add(menuBar.getTable()).fillX().expandX().row();
//
//        // row 2: toolbar
//        root.add(toolbar.getRoot()).fillX().expandX().row();
//
//        // row 3: sidebar & 3d viewport & inspector
//        var multiSplit = new MundusMultiSplitPane(false);
//        multiSplit.setDraggable(false);
//        multiSplit.setWidgets(outline, sceneWidget, this.inspector);
//        multiSplit.setSplit(0, 0.2f);
//        multiSplit.setSplit(1, 0.8f);
//        mainContainer.add(multiSplit).grow().row();
//
//        root.add(splitPane).grow().row();
//
//        // row 4: DOCKER
//        docker = new DockBar(splitPane);
//        root.add(docker).bottom().expandX().fillX().height(30f).row();
//
//        // row 5: status bar
//        var statusBar = new StatusBar();
//        root.add(statusBar).expandX().fillX().height(25f).row();
    }

    public ClickListener createOpenDialogListener(VisDialog dialog) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AppUi.this.showDialog(dialog);
            }
        };
    }

    public void showDialog(VisDialog dialog) {
        dialog.show(this);
    }

    @Override
    public void act() {
        super.act();
//        docker.update();
    }
}
