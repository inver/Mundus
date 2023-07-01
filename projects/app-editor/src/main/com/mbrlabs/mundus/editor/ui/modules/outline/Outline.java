package com.mbrlabs.mundus.editor.ui.modules.outline;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTree;
import com.mbrlabs.mundus.commons.Scene;
import com.mbrlabs.mundus.commons.core.ecs.component.NameComponent;
import com.mbrlabs.mundus.commons.core.ecs.component.ParentComponent;
import com.mbrlabs.mundus.editor.ui.AppUi;
import com.mbrlabs.mundus.editor.utils.TextureUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Slf4j
@Component
public class Outline extends VisTable {
    private final AppUi appUi;
    private final OutlinePresenter outlinePresenter;

    @Setter
    @Getter
    private int selectedEntityId = -1;
    @Getter
    private final VisTree<IdNode, Integer> tree = new VisTree<>();
    private final ScrollPane scrollPane = new ScrollPane(tree);
    private final OutlineDragAndDropController dragAndDrop;
    private final PopupMenu rightClickMenu = new PopupMenu();
    @Getter
    private final MenuItem rcmAddGroup = new MenuItem("Add group");
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

        dragAndDrop = outlinePresenter.initDragAndDropController(tree);

        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);

        var content = new VisTable();
        content.align(Align.left | Align.top);
        content.add(scrollPane).fill().expand();
        add(content).fill().expand();

        initRightClickMenu();
        initScrollPaneListener();
        initTreeClickListener();

        addDirectionalLight.addListener(new ClickButtonListener(outlinePresenter.addDirectionLightAction(this)));
        rcmAddGroup.addListener(new ClickButtonListener(outlinePresenter.addGroupAction(this)));
        rcmAddShader.addListener(new ClickButtonListener(outlinePresenter.addShaderAction(this)));
        rcmAddCamera.addListener(new ClickButtonListener(outlinePresenter.addCameraAction(this)));
        rcmDelete.addListener(new ClickButtonListener(outlinePresenter.deleteAction(this)));
        rcmDuplicate.addListener(new ClickButtonListener(outlinePresenter.duplicateAction(this)));
        rcmAddTerrain.addListener(new ClickButtonListener(outlinePresenter.addTerrainAction()));
        rcmRename.addListener(new ClickButtonListener(() -> {
            if (selectedEntityId >= 0) {
                var dialog = Dialogs.showInputDialog(appUi, "Rename", "", outlinePresenter.renameListener(this));
                var node = tree.findNode(selectedEntityId);
                dialog.setPosition(node.getActor().getX(), node.getActor().getY());
            }
        }));

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
                show(node, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
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


    void show(IdNode node, float x, float y) {
        var entityId = -1;
        if (node != null) {
            entityId = node.getValue();
            tree.getSelection().clear();
            tree.getSelection().add(node);
        }

        selectedEntityId = Math.max(entityId, -1);
        rightClickMenu.showMenu(appUi, x, y);

        rcmRename.setDisabled(selectedEntityId < 0);
        rcmDelete.setDisabled(selectedEntityId < 0);


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

        var world = scene.getWorld();
        //process world for update all entities
        world.process();

        var entityIds = world.getAspectSubscriptionManager().get(Aspect.all(ParentComponent.class)).getEntities();
        if (entityIds.isEmpty()) {
            return;
        }
        var parentMapper = world.getMapper(ParentComponent.class);
        var nameMapper = world.getMapper(NameComponent.class);

        var nodeMap = new HashMap<Integer, IdNode>();
        nodeMap.put(-1, rootNode.getHierarchy());

        for (int i = 0; i < entityIds.size(); i++) {
            var entityId = entityIds.get(i);
            var node = nodeMap.get(entityId);
            if (node != null) {
                node.setName(nameMapper.get(entityId).getName());
                addToParent(parentMapper, nodeMap, entityId, node);
                continue;
            } else {
                node = new IdNode(entityId, nameMapper.get(entityId).getName(), IdNode.Type.NONE);
                nodeMap.put(entityId, node);
            }

            addToParent(parentMapper, nodeMap, entityId, node);

            if (entityId == selectedEntityId) {
                node.expandTo();
                node.setExpanded(true);
            }
        }
    }

    private void addToParent(ComponentMapper<ParentComponent> parentMapper, HashMap<Integer, IdNode> nodeMap,
                             int entityId, IdNode existNode) {
        var parentId = parentMapper.get(entityId).getParentEntityId();
        var parent = nodeMap.get(parentId);
        if (parent != null) {
            parent.add(existNode);
        } else {
            parent = new IdNode(parentId, "UNKNOWN NAME", IdNode.Type.NONE);
            nodeMap.put(parentId, parent);
            parent.add(existNode);
        }
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
