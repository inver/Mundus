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

package com.mbrlabs.mundus.ui.modules.inspector;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.core.Inject;
import com.mbrlabs.mundus.core.Mundus;
import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.core.project.ProjectManager;
import com.mbrlabs.mundus.history.CommandHistory;
import com.mbrlabs.mundus.history.commands.RotateCommand;
import com.mbrlabs.mundus.history.commands.ScaleCommand;
import com.mbrlabs.mundus.history.commands.TranslateCommand;
import com.mbrlabs.mundus.ui.widgets.FloatFieldWithLabel;
import com.mbrlabs.mundus.utils.StringUtils;

/**
 * @author Marcus Brummer
 * @version 16-01-2016
 */
public class TransformWidget extends BaseInspectorWidget {

    private static final Vector3 tempV3 = new Vector3();
    private static final Quaternion tempQuat = new Quaternion();

    private FloatFieldWithLabel posX;
    private FloatFieldWithLabel posY;
    private FloatFieldWithLabel posZ;

    private FloatFieldWithLabel rotX;
    private FloatFieldWithLabel rotY;
    private FloatFieldWithLabel rotZ;

    private FloatFieldWithLabel scaleX;
    private FloatFieldWithLabel scaleY;
    private FloatFieldWithLabel scaleZ;

    @Inject
    private CommandHistory history;
    @Inject
    private ProjectManager projectManager;

    public TransformWidget() {
        super("Transform");
        Mundus.inject(this);
        setDeletable(false);
        init();
        setupUI();
        setupListeners();
    }

    private void init() {
        int size = 65;
        posX = new FloatFieldWithLabel("x", size);
        posY = new FloatFieldWithLabel("y", size);
        posZ = new FloatFieldWithLabel("z", size);
        rotX = new FloatFieldWithLabel("x", size);
        rotY = new FloatFieldWithLabel("y", size);
        rotZ = new FloatFieldWithLabel("z", size);
        scaleX = new FloatFieldWithLabel("x", size);
        scaleY = new FloatFieldWithLabel("y", size);
        scaleZ = new FloatFieldWithLabel("z", size);
    }

    private void setupUI() {
        int pad = 4;
        collapsibleContent.add(new VisLabel("Position: ")).padRight(5).padBottom(pad).left();
        collapsibleContent.add(posX).padBottom(pad).padRight(pad);
        collapsibleContent.add(posY).padBottom(pad).padRight(pad);
        collapsibleContent.add(posZ).padBottom(pad).row();

        collapsibleContent.add(new VisLabel("Rotation: ")).padRight(5).padBottom(pad).left();
        collapsibleContent.add(rotX).padBottom(pad).padRight(pad);
        collapsibleContent.add(rotY).padBottom(pad).padRight(pad);
        collapsibleContent.add(rotZ).padBottom(pad).row();

        collapsibleContent.add(new VisLabel("Scale: ")).padRight(5).padBottom(pad).left();
        collapsibleContent.add(scaleX).padBottom(pad).padRight(pad);
        collapsibleContent.add(scaleY).padBottom(pad).padRight(pad);
        collapsibleContent.add(scaleZ).padBottom(pad).row();
    }

    private void setupListeners() {
        final ProjectContext projectContext = projectManager.current();

        // position
        posX.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameObject go = projectContext.currScene.currentSelection;
                if(go == null) return;
                TranslateCommand command = new TranslateCommand(go);
                command.setBefore(go.position);
                Vector3 pos = go.getPositionRel(tempV3);
                go.setPositionRel(posX.getFloat(), pos.y, pos.z);
                command.setAfter(go.position);
                history.add(command);
            }
        });
        posY.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameObject go = projectContext.currScene.currentSelection;
                if(go == null) return;
                TranslateCommand command = new TranslateCommand(go);
                command.setBefore(go.position);
                Vector3 pos = go.getPositionRel(tempV3);
                go.setPositionRel(pos.x, posY.getFloat(), pos.z);
                command.setAfter(go.position);
                history.add(command);
            }
        });
        posZ.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameObject go = projectContext.currScene.currentSelection;
                if(go == null) return;
                TranslateCommand command = new TranslateCommand(go);
                command.setBefore(go.position);
                Vector3 pos = go.getPositionRel(tempV3);
                go.setPositionRel(pos.x, pos.y, posZ.getFloat());
                command.setAfter(go.position);
                history.add(command);
            }
        });

        // rotation
//        rotX.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                GameObject go = projectContext.currScene.currentSelection;
//                if(go == null) return;
//                RotateCommand rotateCommand = new RotateCommand(go);
//                rotateCommand.setBefore(go.rotation);
//                tempQuat.setEulerAngles(rotY.getFloat(), rotX.getFloat(), rotZ.getFloat());
//                go.getRotationRel(tempV3);
//                go.setRotationRel(rotX.getFloat(), tempV3.y, tempV3.z);
//                rotateCommand.setAfter(go.rotation);
//                history.add(rotateCommand);
//            }
//        });
//        rotY.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                GameObject go = projectContext.currScene.currentSelection;
//                if(go == null) return;
//                RotateCommand rotateCommand = new RotateCommand(go);
//                rotateCommand.setBefore(go.rotation);
//                go.getRotationRel(tempV3);
//                go.setRotationRel(tempV3.x, rotY.getFloat(), tempV3.z);
//                rotateCommand.setAfter(go.rotation);
//                history.add(rotateCommand);
//            }
//        });
//        rotZ.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                GameObject go = projectContext.currScene.currentSelection;
//                if(go == null) return;
//                RotateCommand rotateCommand = new RotateCommand(go);
//                rotateCommand.setBefore(go.rotation);
//                go.getRotationRel(tempV3);
//                go.setRotationRel(tempV3.x, tempV3.y, rotZ.getFloat());
//                rotateCommand.setAfter(go.rotation);
//                history.add(rotateCommand);
//            }
//        });

        // scale
        scaleX.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameObject go = projectContext.currScene.currentSelection;
                if(go != null && scaleX.getFloat() > 0f) {
                    ScaleCommand scaleCommand = new ScaleCommand(go);
                    scaleCommand.setBefore(go.scale);
                    go.getScaleRel(tempV3);
                    go.setScaleRel(scaleX.getFloat(), tempV3.y, tempV3.z);
                    scaleCommand.setAfter(go.scale);
                    history.add(scaleCommand);
                }
            }
        });
        scaleY.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameObject go = projectContext.currScene.currentSelection;
                if(go != null && scaleY.getFloat() > 0f) {
                    ScaleCommand scaleCommand = new ScaleCommand(go);
                    scaleCommand.setBefore(go.scale);
                    go.getScaleRel(tempV3);
                    go.setScaleRel(tempV3.x, scaleY.getFloat(), tempV3.z);
                    scaleCommand.setAfter(go.scale);
                    history.add(scaleCommand);
                }
            }
        });
        scaleZ.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameObject go = projectContext.currScene.currentSelection;
                if(go != null && scaleZ.getFloat() > 0f) {
                    ScaleCommand scaleCommand = new ScaleCommand(go);
                    scaleCommand.setBefore(go.scale);
                    go.getScaleRel(tempV3);
                    go.setScaleRel(tempV3.x, tempV3.y, scaleZ.getFloat());
                    scaleCommand.setAfter(go.scale);
                    history.add(scaleCommand);
                }
            }
        });

    }

    @Override
    public void setValues(GameObject go) {
        Vector3 pos = go.getPositionRel(tempV3);
        posX.setText(StringUtils.formatFloat(pos.x, 2));
        posY.setText(StringUtils.formatFloat(pos.y, 2));
        posZ.setText(StringUtils.formatFloat(pos.z, 2));

        rotX.setText(StringUtils.formatFloat(go.rotation.getPitch(), 2));
        rotY.setText(StringUtils.formatFloat(go.rotation.getYaw(), 2));
        rotZ.setText(StringUtils.formatFloat(go.rotation.getRoll(), 2));

        Vector3 scl = go.getScaleRel(tempV3);
        scaleX.setText(StringUtils.formatFloat(scl.x, 2));
        scaleY.setText(StringUtils.formatFloat(scl.y, 2));
        scaleZ.setText(StringUtils.formatFloat(scl.z, 2));
    }

    @Override
    public void onDelete() {
        // The transform component can't be deleted.
        // Every game object has a transformation
    }

}
