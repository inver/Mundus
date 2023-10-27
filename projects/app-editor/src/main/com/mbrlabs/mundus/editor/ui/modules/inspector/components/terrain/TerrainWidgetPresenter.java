package com.mbrlabs.mundus.editor.ui.modules.inspector.components.terrain;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.layout.GridGroup;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.mbrlabs.mundus.commons.assets.meta.MetaService;
import com.mbrlabs.mundus.commons.assets.terrain.TerrainAsset;
import com.mbrlabs.mundus.commons.assets.texture.TextureAsset;
import com.mbrlabs.mundus.commons.core.ecs.component.TypeComponent;
import com.mbrlabs.mundus.commons.terrain.SplatTexture;
import com.mbrlabs.mundus.commons.terrain.TerrainObject;
import com.mbrlabs.mundus.editor.assets.AssetTextureFilter;
import com.mbrlabs.mundus.editor.core.assets.AssetWriter;
import com.mbrlabs.mundus.editor.core.assets.EditorAssetManager;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.events.AssetImportEvent;
import com.mbrlabs.mundus.editor.events.EntitySelectedEvent;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.events.GlobalBrushSettingsChangedEvent;
import com.mbrlabs.mundus.editor.history.CommandHistory;
import com.mbrlabs.mundus.editor.input.InputService;
import com.mbrlabs.mundus.editor.tools.ToolManager;
import com.mbrlabs.mundus.editor.tools.brushes.TerrainBrush;
import com.mbrlabs.mundus.editor.ui.AppUi;
import com.mbrlabs.mundus.editor.ui.UiComponentHolder;
import com.mbrlabs.mundus.editor.ui.modules.dialogs.assets.AssetPickerDialog;
import com.mbrlabs.mundus.editor.ui.modules.inspector.UiComponentPresenter;
import com.mbrlabs.mundus.editor.ui.modules.inspector.components.UiComponentWidget;
import com.mbrlabs.mundus.editor.ui.modules.inspector.components.terrain.tabs.TerrainGenTab;
import com.mbrlabs.mundus.editor.ui.modules.inspector.components.terrain.tabs.TerrainPaintTab;
import com.mbrlabs.mundus.editor.ui.modules.inspector.components.terrain.tabs.TerrainSettingsTab;
import com.mbrlabs.mundus.editor.ui.modules.outline.ClickButtonListener;
import com.mbrlabs.mundus.editor.ui.widgets.RadioButtonGroup;
import com.mbrlabs.mundus.editor.ui.widgets.chooser.file.FileChooserFieldPresenter;
import com.mbrlabs.mundus.editor.utils.Toaster;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.mbrlabs.mundus.editor.utils.FileFormatUtils.isImage;

@Component
@RequiredArgsConstructor
@Slf4j
public class TerrainWidgetPresenter implements UiComponentPresenter<UiComponentWidget> {

    private final EditorAssetManager assetManager;
    private final AssetWriter assetWriter;
    private final EventBus eventBus;
    private final AppUi appUi;
    private final InputService inputService;
    private final ToolManager toolManager;
    private final AssetPickerDialog assetPickerDialog;
    private final Toaster toaster;
    private final FileChooserFieldPresenter fileChooserFieldPresenter;
    private final CommandHistory history;
    private final EditorCtx ctx;
    private final UiComponentHolder uiComponentHolder;
    private final MetaService metaService;

    public void initBrushGrid(TerrainBrushGrid grid) {
        eventBus.register(grid);

        grid.getStrengthSlider().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                TerrainBrush.setStrength(grid.getStrengthSlider().getValue());
                eventBus.post(new GlobalBrushSettingsChangedEvent());
            }
        });

        for (var brush : toolManager.getTerrainBrushes()) {
            grid.addBrush(brush).addListener(new ClickButtonListener(() -> {
                grid.activateBrush(brush);
                try {
                    inputService.activateTool(brush);
                } catch (TerrainBrush.ModeNotSupportedException e) {
                    log.error("ERROR", e);
                    Dialogs.showErrorDialog(appUi, e.getMessage());
                }
            }));
        }

    }

    public void initSettingsTab(@NotNull TerrainSettingsTab settingsTab) {
        settingsTab.getUvSlider().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
//                var entityId = settingsTab.getParentWidget().getEntityId();
//                assetManager.dirty(entityId);
//
//                var value = settingsTab.getUvSlider().getValue();
//                ctx.getCurrent().getRenderableObject(TerrainObject.class, entityId)
//                        .updateUvScale(new Vector2(value, value));
            }
        });
    }

    public void initPaintTab(TerrainComponentWidget parent, @NotNull TerrainPaintTab paintTab) {
        initBrushGrid(paintTab.getGrid());
//        paintTab.getAddTextureBtn().addListener(new ClickButtonListener(() -> assetPickerDialog.show(
//                false,
//                new AssetTextureFilter(),
//                asset -> {
//                    try {
//                        addTexture(parent, paintTab.getTextureGrid(), (TextureAsset) asset);
//                    } catch (Exception e) {
//                        log.error("ERROR", e);
//                        toaster.error("Error while creating the splatmap");
//                    }
//                })));
        paintTab.getRightClickMenu().addRemoveListener(channel -> {
//            var assetName =
//                    ctx.getCurrent().getRenderableObject(TerrainObject.class, parent.getEntityId()).getAssetName();
//            TerrainAsset asset = assetManager.loadCurrentProjectAsset(assetName);
//            if (channel == SplatTexture.Channel.R) {
//                asset.setSplatR(null);
//            } else if (channel == SplatTexture.Channel.G) {
//                asset.setSplatG(null);
//            } else if (channel == SplatTexture.Channel.B) {
//                asset.setSplatB(null);
//            } else if (channel == SplatTexture.Channel.A) {
//                asset.setSplatA(null);
//            } else {
//                toaster.error("Can't remove the base texture");
//                return;
//            }
//            assetWriter.writeAsset(asset);
//            assetManager.dirty(parent.getEntityId());
        });
        paintTab.getRightClickMenu().addChangeListener(channel -> assetPickerDialog.show(
                false,
                new AssetTextureFilter(),
                asset -> {
                    var terrainAsset = (TerrainAsset) asset;
                    if (channel == SplatTexture.Channel.BASE) {
                        terrainAsset.setSplatBase((TextureAsset) asset);
                    } else if (channel == SplatTexture.Channel.R) {
                        terrainAsset.setSplatR((TextureAsset) asset);
                    } else if (channel == SplatTexture.Channel.G) {
                        terrainAsset.setSplatG((TextureAsset) asset);
                    } else if (channel == SplatTexture.Channel.B) {
                        terrainAsset.setSplatB((TextureAsset) asset);
                    } else if (channel == SplatTexture.Channel.A) {
                        terrainAsset.setSplatA((TextureAsset) asset);
                    }
                    assetWriter.writeAsset(asset);
                    paintTab.setTexturesInUiGrid();
//                    assetManager.dirty(parent.getEntityId());
                }));
    }

    private void addTexture(int entityId, GridGroup textureGrid, TextureAsset textureAsset) {
        var terrain = ctx.getCurrent().getRenderableObject(TerrainObject.class, entityId);
        TerrainAsset terrainAsset = assetManager.loadCurrentProjectAsset(terrain.getAssetName());

        var terrainTexture = terrain.getTerrainTexture();
        assetManager.dirty(entityId);

        // channel base
        if (terrainAsset.getSplatBase() == null) {
            terrainAsset.setSplatBase(textureAsset);
            terrainAsset.applyDependencies();
            terrain.getTerrainTexture().setSplatTexture(new SplatTexture(SplatTexture.Channel.BASE, textureAsset));
            textureGrid.addActor(new VisImage(terrainTexture.getTexture(SplatTexture.Channel.BASE).getTexture()));
            return;
        }

        // create splatmap
        if (terrainAsset.getSplatMap() == null) {
            try {
                var splatmap = assetManager.createPixmapTextureAsset(512);
                terrainAsset.setSplatMap(splatmap);
                terrainAsset.applyDependencies();
                metaService.save(terrainAsset.getMeta());
                eventBus.post(new AssetImportEvent(splatmap));
            } catch (Exception e) {
                log.error("ERROR", e);
                return;
            }

        }

        // channel r
        if (terrainAsset.getSplatR() == null) {
            terrainAsset.setSplatR(textureAsset);
            terrainAsset.applyDependencies();
            textureGrid.addActor(new VisImage(terrainTexture.getTexture(SplatTexture.Channel.R).getTexture()));
            return;
        }

        // channel g
        if (terrainAsset.getSplatG() == null) {
            terrainAsset.setSplatG(textureAsset);
            terrainAsset.applyDependencies();
            textureGrid.addActor(new VisImage(terrainTexture.getTexture(SplatTexture.Channel.G).getTexture()));
            return;
        }

        // channel b
        if (terrainAsset.getSplatB() == null) {
            terrainAsset.setSplatB(textureAsset);
            terrainAsset.applyDependencies();
            textureGrid.addActor(new VisImage(terrainTexture.getTexture(SplatTexture.Channel.B).getTexture()));
            return;
        }

        // channel a
        if (terrainAsset.getSplatA() == null) {
            terrainAsset.setSplatA(textureAsset);
            terrainAsset.applyDependencies();
            textureGrid.addActor(new VisImage(terrainTexture.getTexture(SplatTexture.Channel.A).getTexture()));
            return;
        }

        Dialogs.showErrorDialog(appUi, "Not more than 5 textures per terrainAsset, please :)");
    }

    public void initGenTab(TerrainComponentWidget parent, @NotNull TerrainGenTab genTab) {
        fileChooserFieldPresenter.initFileChooserField(genTab.getHeightmapTab().getFileField());
        genTab.getHeightmapTab().getLoadHeightMapBtn().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                var file = genTab.getHeightmapTab().getSelectedFile();
                if (file != null && file.exists() && isImage(file)) {
                    var command = genTab.getHeightmapTab().loadHeightMap(file);
//                    assetManager.dirty(parent.getComponent().getTerrain());
                    history.add(command);
                } else {
                    Dialogs.showErrorDialog(appUi, "Please select a heightmap image");
                }
            }
        });
        genTab.getPerlinNoiseTab().getPerlinNoiseBtn().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                var seed = genTab.getPerlinNoiseTab().getPerlinNoiseSeed().getInt();
                var min = genTab.getPerlinNoiseTab().getPerlinNoiseMinHeight().getFloat();
                var max = genTab.getPerlinNoiseTab().getPerlinNoiseMaxHeight().getFloat();

                //        Terraformer.perlin(terrain).minHeight(min).maxHeight(max).seed(seed.toLong()).terraform()
                var command = genTab.getPerlinNoiseTab().generatePerlinNoise(seed, min, max);
//                history.add(command);
//                assetManager.dirty(parent.getEntityId());
            }
        });
    }

    @Override
    public void init(UiComponentWidget uiComponent) {
        eventBus.register((EntitySelectedEvent.EntitySelectedListener) event -> {
            if (!ctx.entityExists(event.getEntityId())) {
                uiComponent.setVisible(false);
                return;
            }

            var typeComponent = ctx.getComponentByEntityId(event.getEntityId(), TypeComponent.class);
            uiComponent.setVisible(typeComponent != null && typeComponent.getType() == TypeComponent.Type.TERRAIN);
            uiComponent.setEntityId(event.getEntityId());
        });

        initBrushesTab(uiComponent);
    }

    private void initBrushesTab(UiComponentWidget uiComponent) {
        var brushMode = uiComponent.getField("brushMode", RadioButtonGroup.class);
        Arrays.stream(TerrainBrush.BrushMode.values()).forEach(mode -> brushMode.addOption(mode.getText(), mode));

        var upDownButtonGrid = uiComponent.getField("brushButtonGrid", GridGroup.class);
        for (var brush : toolManager.getTerrainBrushes()) {
            var btn = uiComponentHolder.getButtonFactory().createButton(brush.getIcon());
            upDownButtonGrid.addActor(btn);
            btn.addListener(new ClickButtonListener(() -> {
                var terrain = ctx.getCurrent().getRenderableObject(TerrainObject.class, uiComponent.getEntityId());
                TerrainAsset terrainAsset = assetManager.loadCurrentProjectAsset(terrain.getAssetName());

                toolManager.activateBrush(brush,
                        (TerrainBrush.BrushMode) brushMode.getButtonGroup().getChecked().getRefObject(),
                        terrainAsset
                );
                try {
                    inputService.activateTool(brush);
                } catch (TerrainBrush.ModeNotSupportedException e) {
                    log.error("ERROR", e);
                    Dialogs.showErrorDialog(appUi, e.getMessage());
                }
            }));
        }
        uiComponent.getField("selectTexture", VisTextButton.class)
                .addListener(new ClickButtonListener(() -> assetPickerDialog.show(
                        false,
                        new AssetTextureFilter(),
                        asset -> {
                            try {
                                addTexture(uiComponent.getEntityId(),
                                        uiComponent.getField("textureGrid", GridGroup.class),
                                        (TextureAsset) asset);
                            } catch (Exception e) {
                                log.error("ERROR", e);
                                toaster.error("Error while creating the splatmap");
                            }
                        })
                ));
    }
}
