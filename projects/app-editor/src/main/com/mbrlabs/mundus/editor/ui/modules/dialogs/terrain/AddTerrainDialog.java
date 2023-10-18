///*
// * Copyright (c) 2016. See AUTHORS file.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//

package com.mbrlabs.mundus.editor.ui.modules.dialogs.terrain;


import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.mbrlabs.mundus.commons.terrain.TerrainObject;
import com.mbrlabs.mundus.editor.ui.modules.dialogs.BaseDialog;
import com.mbrlabs.mundus.editor.ui.widgets.FloatFieldWithLabel;
import com.mbrlabs.mundus.editor.ui.widgets.IntegerFieldWithLabel;
import com.mbrlabs.mundus.editor.ui.widgets.ToolTipLabel;
import org.springframework.stereotype.Component;

/**
 * @author Marcus Brummer
 * @version 01-12-2015
 */
@Component
public class AddTerrainDialog extends BaseDialog {

    private final TerrainDialogPresenter terrainDialogPresenter;

    final VisTextField name = new VisTextField("Terrain");
    final IntegerFieldWithLabel vertexResolution = new IntegerFieldWithLabel("", -1, false);
    final IntegerFieldWithLabel terrainWidth = new IntegerFieldWithLabel("", -1, false);
    final FloatFieldWithLabel positionX = new FloatFieldWithLabel("", -1, true);
    final FloatFieldWithLabel positionY = new FloatFieldWithLabel("", -1, true);
    final FloatFieldWithLabel positionZ = new FloatFieldWithLabel("", -1, true);
    final VisSelectBox<String> splatMapSelectBox = new VisSelectBox<>();

    VisTextButton generateBtn = new VisTextButton("Generate Terrain");

//    private var projectManager :ProjectManager
//    private var ioManager :IOManager


    public AddTerrainDialog(TerrainDialogPresenter terrainDialogPresenter) {
        super("Add Terrain");
        this.terrainDialogPresenter = terrainDialogPresenter;

        setResizable(true);
        setupUI();
        setDefaults();

        terrainDialogPresenter.initGenerateButton(this);
    }

    private void setDefaults() {
        vertexResolution.setText(TerrainObject.DEFAULT_VERTEX_RESOLUTION + "");
        terrainWidth.setText(TerrainObject.DEFAULT_SIZE + "");
        positionX.setText("0");
        positionY.setText("0");
        positionZ.setText("0");
    }

    private void setupUI() {
        var root = new Table();
        // root.debugAll();
        root.padTop(6f).padRight(6f).padBottom(22f);
        add(root);

        // left table
        var content = new VisTable();
        content.defaults().pad(4f);
        content.left().top();
        content.add(new VisLabel("Name: ")).left().padBottom(10f);
        content.add(name).fillX().expandX().row();
        content.add(new ToolTipLabel(
                        "Vertex resolution ",
                        "This will determine the vertices count when squared. 180 = 32,400 vertices. \n" +
                                "The default value (or lower) is recommended for performance. \n" +
                                "Settings this over 180 may cause issues on some devices."
                ))
                .left().padBottom(10f);
        content.add(vertexResolution).fillX().expandX().row();
        content.add(new ToolTipLabel("Terrain width", "Size of the terrain, in meters.")).left().padBottom(10f);
        content.add(terrainWidth).fillX().expandX().row();
        content.add(new VisLabel("Position on x-axis")).left().padBottom(10f);
        content.add(positionX).fillX().expandX().row();
        content.add(new VisLabel("Position on y-axis")).left().padBottom(10f);
        content.add(positionY).fillX().expandX().row();
        content.add(new VisLabel("Position on z-axis")).left().padBottom(10f);
        content.add(positionZ).fillX().expandX().row();

        var selectorsTable = new VisTable(true);
//        splatMapSelectBox.setItems(
//                SplatMapResolution._512.getValue(),
//                SplatMapResolution._1024.getValue(),
//                SplatMapResolution._2048.getValue()
//        );
        selectorsTable.add(splatMapSelectBox);

        content.add(new ToolTipLabel("SplatMap Resolution: ",
                "The resolution of the splatmap for texture painting on the terrain.\n" +
                        "Higher resolution results in smoother texture painting at the cost of more memory usage and " +
                        "performance slowdowns when painting.\n" +
                        "If you are targeting HTML, 512 is recommended")).left().padBottom(10f);
        content.add(selectorsTable).left().padBottom(10f).row();

        content.add(generateBtn).fillX().expand().colspan(2).bottom();
        root.add(content);
    }

}
