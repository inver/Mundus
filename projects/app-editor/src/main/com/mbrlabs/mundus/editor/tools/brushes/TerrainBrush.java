/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.editor.tools.brushes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mbrlabs.mundus.commons.assets.terrain.TerrainAsset;
import com.mbrlabs.mundus.commons.env.SceneEnvironment;
import com.mbrlabs.mundus.commons.terrain.SplatTexture;
import com.mbrlabs.mundus.editor.core.project.EditorCtx;
import com.mbrlabs.mundus.editor.history.CommandHistory;
import com.mbrlabs.mundus.editor.history.commands.TerrainHeightCommand;
import com.mbrlabs.mundus.editor.history.commands.TerrainPaintCommand;
import com.mbrlabs.mundus.editor.tools.Tool;
import net.nevinsky.abyssus.core.ModelBatch;
import net.nevinsky.abyssus.core.shader.ShaderProvider;

/**
 * A Terrain Brush can modify the terrainAsset in various ways (BrushMode).
 * <p>
 * This includes the height of every vertex in the terrainAsset grid & according splatmap.
 *
 * @author Marcus Brummer
 * @version 30-01-2016
 */
public abstract class TerrainBrush extends Tool {

    /**
     * Defines the draw mode of a brush.
     */
    public enum BrushMode {
        /**
         * Raises or lowers the terrainAsset height.
         */
        RAISE_LOWER,
        /**
         * Sets all vertices of the selection to a specified height.
         */
        FLATTEN,
        /**
         * TBD
         */
        SMOOTH,
        /**
         * Paints on the splatmap of the terrainAsset.
         */
        PAINT
    }

    /**
     * Defines two actions (and it's key codes) every brush and every mode can have.
     * <p>
     * For instance the RAISE_LOWER mode has 'raise' has PRIMARY action and 'lower' as secondary. Pressing the keycode
     * of the secondary & the primary key enables the secondary action.
     **/
    public enum BrushAction {
        PRIMARY(Input.Buttons.LEFT), SECONDARY(Input.Keys.SHIFT_LEFT);

        public final int code;

        BrushAction(int levelCode) {
            this.code = levelCode;
        }

    }

    /**
     * Thrown if a the brush is set to a mode, which it currently does not support.
     */
    public static class ModeNotSupportedException extends RuntimeException {
        public ModeNotSupportedException(String message) {
            super(message);
        }
    }

    // used for calculations
    protected static final Vector2 c = new Vector2();
    protected static final Vector2 p = new Vector2();
    protected static final Vector2 v = new Vector2();
    protected static final Color c0 = new Color();
    protected static final Vector3 tVec0 = new Vector3();
    protected static final Vector3 tVec1 = new Vector3();

    private static float strength = 0.5f;
    private static float heightSample = 0f;
    private static SplatTexture.Channel paintChannel;

    // individual brush settings
    protected final Vector3 brushPos = new Vector3();
    protected float radius = 25f;
    protected BrushMode mode;
    protected TerrainAsset terrainAsset;
    private BrushAction action;

    private boolean mouseMoved = false;

    // the pixmap brush
    private final Pixmap brushPixmap;
    private final int pixmapCenter;

    // undo/redo system
    private final TerrainHeightCommand heightCommand = null;
    private final TerrainPaintCommand paintCommand = null;
    private final boolean terrainHeightModified = false;
    private final boolean splatmapModified = false;

    public TerrainBrush(EditorCtx ctx, String shaderKey, CommandHistory history, FileHandle pixmapBrush, String name) {
        super(ctx, shaderKey, history, name);

        brushPixmap = new Pixmap(pixmapBrush);
        pixmapCenter = brushPixmap.getWidth() / 2;
    }

    @Override
    public void act() {
        if (action == null) {
            return;
        }
        if (terrainAsset == null) {
            return;
        }

        // sample height
        if (action == BrushAction.SECONDARY && mode == BrushMode.FLATTEN) {
            heightSample = brushPos.y;
            return;
        }

        // only act if mouse has been moved
        if (!mouseMoved) {
            return;
        }
        mouseMoved = false;

        if (mode == BrushMode.PAINT) {
            paint();
        } else if (mode == BrushMode.RAISE_LOWER) {
            raiseLower(action);
        } else if (mode == BrushMode.FLATTEN) {
            flatten();
        }
    }

    private void paint() {
        //todo
//        Terrain terrain = terrainAsset.getTerrain();
//        SplatMap sm = terrain.getTerrainTexture().getSplatMap();
//        if (sm == null) return;
//
//        Vector3 terrainPos = terrain.getPosition(tVec1);
//        final float splatX = ((brushPos.x - terrainPos.x) / (float) terrain.terrainWidth) * sm.getWidth();
//        final float splatY = ((brushPos.z - terrainPos.z) / (float) terrain.terrainDepth) * sm.getHeight();
//        final float splatRad = (radius / terrain.terrainWidth) * sm.getWidth();
//        final Pixmap pixmap = sm.getPixmap();
//
//        for (int smX = 0; smX < pixmap.getWidth(); smX++) {
//            for (int smY = 0; smY < pixmap.getHeight(); smY++) {
//                final float dst = MathUtils.dst(splatX, splatY, smX, smY);
//                if (dst <= splatRad) {
//                    final float opacity = getValueOfBrushPixmap(splatX, splatY, smX, smY, splatRad) * 0.5f * strength;
//                    int newPixelColor = sm.additiveBlend(pixmap.getPixel(smX, smY), paintChannel, opacity);
//                    pixmap.drawPixel(smX, smY, newPixelColor);
//                }
//            }
//        }
//
//        sm.updateTexture();
//        splatmapModified = true;
//        assetManager.dirty(terrainAsset);
    }

    private void flatten() {
//        Terrain terrain = terrainAsset.getTerrain();
//        final Vector3 terPos = terrain.getPosition(tVec1);
//        for (int x = 0; x < terrain.vertexResolution; x++) {
//            for (int z = 0; z < terrain.vertexResolution; z++) {
//                final Vector3 vertexPos = terrain.getVertexPosition(tVec0, x, z);
//                vertexPos.x += terPos.x;
//                vertexPos.z += terPos.z;
//                float distance = vertexPos.dst(brushPos);
//
//                if (distance <= radius) {
//                    final int index = z * terrain.vertexResolution + x;
//                    final float diff = Math.abs(terrain.heightData[index] - heightSample);
//                    if (diff <= 1f) {
//                        terrain.heightData[index] = heightSample;
//                    } else if (diff > 1f) {
//                        final float elevation = getValueOfBrushPixmap(brushPos.x, brushPos.z, vertexPos.x,
//                        vertexPos.z,
//                                radius);
//                        // current height is lower than sample
//                        if (heightSample > terrain.heightData[index]) {
//                            terrain.heightData[index] += elevation * strength;
//                        } else {
//                            float newHeight = terrain.heightData[index] - elevation * strength;
//                            if (diff > Math.abs(newHeight) || terrain.heightData[index] > heightSample) {
//                                terrain.heightData[index] = newHeight;
//                            }
//
//                        }
//                    }
//                }
//            }
//        }
//
//        terrain.update();
//        terrainHeightModified = true;
        //todo
//        assetManager.dirty(terrainAsset);
    }

    private void raiseLower(BrushAction action) {
//        Terrain terrain = terrainAsset.getTerrain();
//        final Vector3 terPos = terrain.getPosition(tVec1);
//        float dir = (action == BrushAction.PRIMARY) ? 1 : -1;
//        for (int x = 0; x < terrain.vertexResolution; x++) {
//            for (int z = 0; z < terrain.vertexResolution; z++) {
//                final Vector3 vertexPos = terrain.getVertexPosition(tVec0, x, z);
//                vertexPos.x += terPos.x;
//                vertexPos.z += terPos.z;
//                float distance = vertexPos.dst(brushPos);
//
//                if (distance <= radius) {
//                    float elevation = getValueOfBrushPixmap(brushPos.x, brushPos.z, vertexPos.x, vertexPos.z, radius);
//                    terrain.heightData[z * terrain.vertexResolution + x] += dir * elevation * strength;
//                }
//            }
//        }
//
//        terrain.update();
//        terrainHeightModified = true;
        //todo
//        assetManager.dirty(terrainAsset);
    }

    /**
     * Interpolates the brush texture in the range of centerX - radius to centerX + radius and centerZ - radius to
     * centerZ + radius. PointZ & pointX lies between these ranges.
     * <p>
     * Interpolation is necessary, since the brush pixmap is fixed sized, whereas the input values can scale. (Input
     * points can be vertices or splatmap texture coordinates)
     *
     * @param centerX
     * @param centerZ
     * @param pointX
     * @param pointZ
     * @param radius
     * @return the interpolated r-channel value of brush pixmap at pointX, pointZ, which can be interpreted as
     * terrainAsset height (raise/lower) or opacity (paint)
     */
    private float getValueOfBrushPixmap(float centerX, float centerZ, float pointX, float pointZ, float radius) {
        c.set(centerX, centerZ);
        p.set(pointX, pointZ);
        v.set(p.sub(c));

        final float progress = v.len() / radius;
        v.nor().scl(pixmapCenter * progress);

        final float mapX = pixmapCenter + (int) v.x;
        final float mapY = pixmapCenter + (int) v.y;
        c0.set(brushPixmap.getPixel((int) mapX, (int) mapY));

        return c0.r;
    }

    public void scale(float amount) {
        radius *= amount;
    }

    public static float getStrength() {
        return strength;
    }

    public static void setStrength(float strength) {
        TerrainBrush.strength = strength;
    }

    public static float getHeightSample() {
        return heightSample;
    }

    public static void setHeightSample(float heightSample) {
        //todo post event
        TerrainBrush.heightSample = heightSample;
//        eventBus.post(brushSettingsChangedEvent);
    }

    public static SplatTexture.Channel getPaintChannel() {
        return paintChannel;
    }

    public static void setPaintChannel(SplatTexture.Channel paintChannel) {
        //todo post event
        TerrainBrush.paintChannel = paintChannel;
//        eventBus.post(brushSettingsChangedEvent);
    }

    public BrushMode getMode() {
        return mode;
    }

    public void setMode(BrushMode mode) throws ModeNotSupportedException {
        if (!supportsMode(mode)) {
            throw new ModeNotSupportedException(getName() + " does not support " + mode);
        }
        this.mode = mode;
    }

    public TerrainAsset getTerrainAsset() {
        return terrainAsset;
    }

    public void setTerrainAsset(TerrainAsset terrainAsset) {
        this.terrainAsset = terrainAsset;
    }

    public boolean supportsMode(BrushMode mode) {
        switch (mode) {
            case RAISE_LOWER:
            case FLATTEN:
            case PAINT:
                return true;
        }

        return false;
    }

    @Override
    public void render(ModelBatch batch, SceneEnvironment environment, ShaderProvider shaders, float delta) {
        // rendering of the brush is done in the editor terrain shader
    }

    @Override
    public void dispose() {
        brushPixmap.dispose();
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        //todo
//        if (terrainHeightModified && heightCommand != null) {
//            heightCommand.setHeightDataAfter(terrainAsset.getTerrain().heightData);
//            getHistory().add(heightCommand);
//        }
//        if (splatmapModified && paintCommand != null) {
//            final SplatMap sm = terrainAsset.getTerrain().getTerrainTexture().getSplatMap();
//            paintCommand.setAfter(sm.getPixmap());
//            getHistory().add(paintCommand);
//        }
//        splatmapModified = false;
//        terrainHeightModified = false;
//        heightCommand = null;
//        paintCommand = null;
//
//        action = null;

        return false;
    }

    private BrushAction getAction() {
        final boolean primary = Gdx.input.isButtonPressed(BrushAction.PRIMARY.code);
        final boolean secondary = Gdx.input.isKeyPressed(BrushAction.SECONDARY.code);

        if (primary && secondary) {
            return BrushAction.SECONDARY;
        } else if (primary) {
            return BrushAction.PRIMARY;
        }

        return null;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        //todo
//        // get action
//        final boolean primary = Gdx.input.isButtonPressed(BrushAction.PRIMARY.code);
//        final boolean secondary = Gdx.input.isKeyPressed(BrushAction.SECONDARY.code);
//        if (primary && secondary) {
//            action = BrushAction.SECONDARY;
//        } else if (primary) {
//            action = BrushAction.PRIMARY;
//        } else {
//            action = null;
//        }
//
//        if (mode == BrushMode.FLATTEN || mode == BrushMode.RAISE_LOWER || mode == BrushMode.SMOOTH) {
//            heightCommand = new TerrainHeightCommand(terrainAsset.getTerrain());
//            heightCommand.setHeightDataBefore(terrainAsset.getTerrain().heightData);
//        } else if (mode == BrushMode.PAINT) {
//            final SplatMap sm = terrainAsset.getTerrain().getTerrainTexture().getSplatMap();
//            if (sm != null) {
//                paintCommand = new TerrainPaintCommand(terrainAsset.getTerrain());
//                paintCommand.setBefore(sm.getPixmap());
//            }
//        }

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        //todo
//        if (terrainAsset != null) {
//            Ray ray = getCtx().getViewport().getPickRay(screenX, screenY);
//            terrainAsset.getTerrain().getRayIntersection(brushPos, ray);
//        }
//
//        mouseMoved = true;

        //todo
//        ((EditorTerrainShader) getShader()).setPickerPosition(brushPos.x, brushPos.y, brushPos.z);

        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (amountX < 0) {
            scale(0.9f);
        } else {
            scale(1.1f);
        }
        //todo
//        ((EditorTerrainShader) getShader()).setPickerRadius(radius);

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return mouseMoved(screenX, screenY);
    }

    @Override
    public void onDisabled() {
        //todo
//        ((EditorTerrainShader) getShader()).activatePicker(false);
    }

    @Override
    public void onActivated() {
        //todo
//        ((EditorTerrainShader) getShader()).activatePicker(true);
//        ((EditorTerrainShader) getShader()).setPickerPosition(brushPos.x, brushPos.y, brushPos.z);
//        ((EditorTerrainShader) getShader()).setPickerRadius(radius);
    }

}
