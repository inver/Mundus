package com.mbrlabs.mundus.editor.ui.modules.inspector.components.terrain;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.mbrlabs.mundus.commons.assets.meta.MetaService;
import com.mbrlabs.mundus.commons.assets.texture.TextureAsset;
import com.mbrlabs.mundus.commons.terrain.SplatTexture;
import com.mbrlabs.mundus.editor.assets.AssetTextureFilter;
import com.mbrlabs.mundus.editor.core.project.ProjectManager;
import com.mbrlabs.mundus.editor.events.AssetImportEvent;
import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.events.GlobalBrushSettingsChangedEvent;
import com.mbrlabs.mundus.editor.history.CommandHistory;
import com.mbrlabs.mundus.editor.tools.ToolManager;
import com.mbrlabs.mundus.editor.tools.brushes.TerrainBrush;
import com.mbrlabs.mundus.editor.ui.AppUi;
import com.mbrlabs.mundus.editor.ui.modules.dialogs.assets.AssetPickerDialog;
import com.mbrlabs.mundus.editor.ui.widgets.TextureGrid;
import com.mbrlabs.mundus.editor.ui.widgets.presenter.FileChooserFieldPresenter;
import com.mbrlabs.mundus.editor.utils.Toaster;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import static com.mbrlabs.mundus.editor.utils.FileFormatUtils.isImage;

@Component
@RequiredArgsConstructor
@Slf4j
public class TerrainWidgetPresenter {

    private final EventBus eventBus;
    private final AppUi appUi;
    private final ToolManager toolManager;
    private final ProjectManager projectManager;
    private final AssetPickerDialog assetPickerDialog;
    private final Toaster toaster;
    private final MetaService metaService;
    private final FileChooserFieldPresenter fileChooserFieldPresenter;
    private final CommandHistory history;

    public void initBrushGrid(TerrainBrushGrid grid) {
        eventBus.register(grid);

        grid.getStrengthSlider().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                TerrainBrush.setStrength(grid.getStrengthSlider().getValue());
                eventBus.post(new GlobalBrushSettingsChangedEvent());
            }
        });

        for (var brush : toolManager.terrainBrushes) {
            grid.addBrush(brush).addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    grid.activateBrush(brush);
                    try {
                        toolManager.activateTool(brush);
                    } catch (TerrainBrush.ModeNotSupportedException e) {
                        log.error("ERROR", e);
                        Dialogs.showErrorDialog(appUi, e.getMessage());
                    }

                }
            });
        }
    }

    public void initSettingsTab(@NotNull TerrainSettingsTab settingsTab) {
        settingsTab.getUvSlider().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                var assetManager = projectManager.getCurrent().assetManager;
                var component = settingsTab.getParentWidget().getComponent();
                assetManager.dirty(component.getTerrain());

                var value = settingsTab.getUvSlider().getValue();
                component.updateUVs(new Vector2(value, value));
            }
        });
    }

    public void initPaintTab(TerrainComponentWidget parent, @NotNull TerrainPaintTab paintTab) {
        initBrushGrid(paintTab.getGrid());
        paintTab.getAddTextureBtn().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                assetPickerDialog.show(
                        false,
                        new AssetTextureFilter(),
                        asset -> {
                            try {
                                addTexture(parent, paintTab.getTextureGrid(), (TextureAsset) asset);
                            } catch (Exception e) {
                                log.error("ERROR", e);
                                toaster.error("Error while creating the splatmap");
                            }
                        });
            }
        });
        paintTab.getRightClickMenu().addRemoveListener(channel -> {
            var terrain = parent.getComponent().getTerrain();
            if (channel == SplatTexture.Channel.R) {
                terrain.setSplatR(null);
            } else if (channel == SplatTexture.Channel.G) {
                terrain.setSplatG(null);
            } else if (channel == SplatTexture.Channel.B) {
                terrain.setSplatB(null);
            } else if (channel == SplatTexture.Channel.A) {
                terrain.setSplatA(null);
            } else {
                toaster.error("Can't remove the base texture");
                return;
            }

            terrain.applyDependencies();
            projectManager.getCurrent().assetManager.dirty(terrain);
        });
        paintTab.getRightClickMenu().addChangeListener(channel -> assetPickerDialog.show(
                false,
                new AssetTextureFilter(),
                asset -> {
                    var terrain = parent.getComponent().getTerrain();
                    if (channel == SplatTexture.Channel.BASE) {
                        terrain.setSplatBase((TextureAsset) asset);
                    } else if (channel == SplatTexture.Channel.R) {
                        terrain.setSplatR((TextureAsset) asset);
                    } else if (channel == SplatTexture.Channel.G) {
                        terrain.setSplatG((TextureAsset) asset);
                    } else if (channel == SplatTexture.Channel.B) {
                        terrain.setSplatB((TextureAsset) asset);
                    } else if (channel == SplatTexture.Channel.A) {
                        terrain.setSplatA((TextureAsset) asset);
                    }
                    terrain.applyDependencies();
                    paintTab.setTexturesInUiGrid();
                    projectManager.getCurrent().assetManager.dirty(terrain);
                }));
    }

    private void addTexture(TerrainComponentWidget parent, TextureGrid<SplatTexture> textureGrid,
                            TextureAsset textureAsset) {
        var assetManager = projectManager.getCurrent().assetManager;

        var terrainAsset = parent.getComponent().getTerrain();
        var terrainTexture = terrainAsset.getTerrain().getTerrainTexture();

        assetManager.dirty(terrainAsset);

        // channel base
        if (terrainAsset.getSplatBase() == null) {
            terrainAsset.setSplatBase(textureAsset);
            terrainAsset.applyDependencies();
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.BASE));
            return;
        }

        // create splatmap
        if (terrainAsset.getSplatmap() == null) {
            try {
                var splatmap = assetManager.createPixmapTextureAsset(512);
                terrainAsset.setSplatmap(splatmap);
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
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.R));
            return;
        }

        // channel g
        if (terrainAsset.getSplatG() == null) {
            terrainAsset.setSplatG(textureAsset);
            terrainAsset.applyDependencies();
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.G));
            return;
        }

        // channel b
        if (terrainAsset.getSplatB() == null) {
            terrainAsset.setSplatB(textureAsset);
            terrainAsset.applyDependencies();
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.B));
            return;
        }

        // channel a
        if (terrainAsset.getSplatA() == null) {
            terrainAsset.setSplatA(textureAsset);
            terrainAsset.applyDependencies();
            textureGrid.addTexture(terrainTexture.getTexture(SplatTexture.Channel.A));
            return;
        }

        Dialogs.showErrorDialog(appUi, "Not more than 5 textures per terrainAsset please :)");
    }

    public void initGenTab(TerrainComponentWidget parent, @NotNull TerrainGenTab genTab) {
        fileChooserFieldPresenter.initFileChooserField(genTab.getHeightmapTab().getFileField());
        genTab.getHeightmapTab().getLoadHeightMapBtn().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                var file = genTab.getHeightmapTab().getSelectedFile();
                if (file != null && file.exists() && isImage(file)) {
                    var command = genTab.getHeightmapTab().loadHeightMap(file);
                    projectManager.getCurrent().assetManager.dirty(parent.getComponent().getTerrain());
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
                var command = genTab.getPerlinNoiseTab().generatePerlinNoise(seed, min, max);
                history.add(command);
                projectManager.getCurrent().assetManager.dirty(parent.getComponent().getTerrain());
            }
        });
    }
}
