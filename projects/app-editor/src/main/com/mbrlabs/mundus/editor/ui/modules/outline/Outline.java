package com.mbrlabs.mundus.editor.ui.modules.outline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTree;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.scene3d.HierarchyNode;
import com.mbrlabs.mundus.editor.ui.AppUi;
import com.mbrlabs.mundus.editor.utils.TextureUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Outline extends VisTable {
    private final AppUi appUi;
    private final OutlinePresenter outlinePresenter;

    @Setter
    @Getter
    int selectedEntityId = -1;
    @Getter
    private final VisTree<IdNode, Integer> tree = new VisTree<>();
    private final ScrollPane scrollPane = new ScrollPane(tree);
    private final OutlineDragAndDrop dragAndDrop;
    private final PopupMenu rightClickMenu = new PopupMenu();
    @Getter
    final MenuItem rcmAddGroup = new MenuItem("Add group");
    @Getter
    private final MenuItem rcmAddCamera = new MenuItem("Add camera");
    @Getter
    private final MenuItem rcmAddTerrain = new MenuItem("Add terrain");
    @Getter
    private final MenuItem rcmAddLight = new MenuItem("Add light");
    @Getter
    private final MenuItem rcmAddShader = new MenuItem("Add Shader");
    @Getter
    private final MenuItem rcmDuplicate = new MenuItem("Duplicate");
    @Getter
    private final MenuItem rcmRename = new MenuItem("Rename");
    @Getter
    private final MenuItem rcmDelete = new MenuItem("Delete");
    private final PopupMenu lightsPopupMenu = new PopupMenu();
    @Getter
    private final MenuItem addDirectionalLight = new MenuItem("Directional Light");


    public Outline(AppUi appUi, OutlinePresenter outlinePresenter) {
        this.appUi = appUi;
        this.outlinePresenter = outlinePresenter;

        setBackground("window-bg");
        add(new VisLabel("Outline")).expandX().fillX().pad(3f).row();
        addSeparator().row();

//        tree.debugAll()
        //todo migrate to SymbolIcon
        tree.getStyle().plus = TextureUtils.load("ui/icons/expand.png", 20, 20);
        tree.getStyle().minus = TextureUtils.load("ui/icons/collapse.png", 20, 20);
        tree.getSelection().setProgrammaticChangeEvents(false);

        dragAndDrop = new OutlineDragAndDrop(tree, outlinePresenter.getDropListener(this));

        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);

        var content = new VisTable();
        content.align(Align.left | Align.top);
        content.add(scrollPane).fill().expand();
        add(content).fill().expand();

        initRightClickMenu();
        initScrollPaneListener();
        initTreeClickListener();

        outlinePresenter.init(this);
    }

    private void initScrollPaneListener() {
        scrollPane.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                appUi.setScrollFocus(scrollPane);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                appUi.setScrollFocus(null);
            }
        });
    }

    private void initTreeClickListener() {
        tree.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button != Input.Buttons.LEFT) {
                    return true;
                }
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (Input.Buttons.RIGHT != button) {
                    super.touchUp(event, x, y, pointer, button);
                    return;
                }
                var node = tree.getNodeAt(y);
                var id = -1;
                if (node != null) {
                    id = node.getValue();
                }
                show(id, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (getTapCount() != 2) {
                    return;
                }
                var clickedNode = tree.getNodeAt(y);
                if (clickedNode == null) {
                    return;
                }
                var entityId = clickedNode.getValue();
                if (entityId < 0) {
                    return;
                }
                outlinePresenter.moveCameraToSelectedEntity(entityId);
            }
        });
    }

    private void initRightClickMenu() {
        rightClickMenu.addItem(rcmAddGroup);
        rightClickMenu.addItem(rcmAddCamera);
        rightClickMenu.addItem(rcmAddTerrain);
        rightClickMenu.addItem(rcmAddLight);
        rightClickMenu.addItem(rcmAddShader);
        rightClickMenu.addItem(rcmDuplicate);
        rightClickMenu.addItem(rcmRename);
        rightClickMenu.addItem(rcmDelete);

        lightsPopupMenu.addItem(addDirectionalLight);
        rcmAddLight.setSubMenu(lightsPopupMenu);
    }


    void show(int entityId, float x, float y) {
        selectedEntityId = entityId;
        rightClickMenu.showMenu(appUi, x, y);

        rcmRename.setDisabled(selectedEntityId <= 0);
        rcmDelete.setDisabled(selectedEntityId <= 0);

        //todo
        // terrainAsset can not be duplicated
//            duplicate.isDisabled =
//                selectedGO == null || ctx.current.currentScene.world.getEntity(selectedGO).getComponent()selectedGO!!
//                .findComponentByType(Component.Type.TERRAIN) != null
    }

    /**
     * Building tree from game objects in sceneGraph, clearing previous
     * sceneGraph
     *
     * @param scene: current scene
     */
    void buildTree(Scene scene) {
        tree.clearChildren();
        var rootNode = new IdNode.RootNode();
        tree.add(rootNode);

        scene.getRootNode().getChildren().forEach(n -> addNodeToTree(rootNode.getHierarchy(), n));
    }

    private void addNodeToTree(IdNode treeParentNode, HierarchyNode node) {
        var leaf = new IdNode(node.getId(), node.getName());
        if (treeParentNode == null) {
            tree.add(leaf);
        } else {
            treeParentNode.add(leaf);
        }
        // Always expand after adding new node
        leaf.expandTo();
        if (CollectionUtils.isEmpty(node.getChildren())) {
            return;
        }

        node.getChildren().forEach(n -> addNodeToTree(leaf, n));
    }

    public void onEntitySelected(int entityId) {
        tree.getSelection().clear();

        if (entityId < 0) {
            return;
        }

        var node = tree.findNode(entityId);
        log.trace("Selected game object [{}] with id {}.", node == null ? "" : node.getValue(), entityId);
        if (node != null) {
            tree.getSelection().add(node);
            node.expandTo();
        }
    }

    void showRenameDialog() {
        //TODO
//            val node = tree.findNode(selectedGO!!)
//
//            val renameDialog = Dialogs.showInputDialog(appUi, "Rename", "",
//                object : InputDialogAdapter() {
//                    override fun finished(input: String?) {
//                        log.trace("Rename game object [{}] to [{}].", selectedGO, input)
//                        // update sceneGraph
//                        selectedGO!!.name = input
//                        // update Outline
//                        //goNode.name.setText(input + " [" + selectedGO.id + "]");
//                        node.label.setText(input)
//
//                        eventBus.post(SceneGraphChangedEvent())
//                    }
//                })
//            // set position of dialog to menuItem position
//            val nodePosX = node.actor.x
//            val nodePosY = node.actor.y
//            renameDialog.setPosition(nodePosX, nodePosY)
    }

    /**
     * Deep copy of all game objects
     * @param go            the game object for cloning, with children
     * *
     * @param parent         game object on which clone will be added
     */
//    private fun duplicateGO(go: GameObject, parent: GameObject) {
//        log.trace("Duplicate [{}] with parent [{}]", go, parent)
//        val goCopy = GameObject(go, ctx.current.obtainID())
//        TODO()
//        // add copy to tree
//        val n = tree.findNode(parent)
//        addGoToTree(n, goCopy)
//
//        // add copy to scene graph
//        parent.addChild(goCopy)
//
//        // recursively clone child objects
//        if (go.children != null) {
//            for (child in go.children) {
//                duplicateGO(child, goCopy)
//            }
//        }
//    }
}
