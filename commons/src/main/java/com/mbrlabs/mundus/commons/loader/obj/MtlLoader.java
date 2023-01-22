package com.mbrlabs.mundus.commons.loader.obj;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMaterial;
import com.badlogic.gdx.graphics.g3d.model.data.ModelTexture;
import com.badlogic.gdx.utils.Array;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class MtlLoader {
    public Array<ModelMaterial> materials = new Array<>();

    /**
     * loads .mtl file
     */
    public void load(FileHandle file) {
        String line;
        String[] tokens;

        ObjMaterial currentMaterial = new ObjMaterial();

        if (file == null || !file.exists()) return;

        BufferedReader reader = new BufferedReader(new InputStreamReader(file.read()), 4096);
        try {
            while ((line = reader.readLine()) != null) {

                if (line.length() > 0 && line.charAt(0) == '\t') line = line.substring(1).trim();

                tokens = line.split("\\s+");

                if (tokens[0].length() == 0) {
                    continue;
                } else if (tokens[0].charAt(0) == '#')
                    continue;
                else {
                    final String key = tokens[0].toLowerCase();
                    if (key.equals("newmtl")) {
                        ModelMaterial mat = currentMaterial.build();
                        materials.add(mat);

                        if (tokens.length > 1) {
                            currentMaterial.materialName = tokens[1];
                            currentMaterial.materialName = currentMaterial.materialName.replace('.', '_');
                        } else {
                            currentMaterial.materialName = "default";
                        }

                        currentMaterial.reset();
                    } else if (key.equals("ka")) {
                        currentMaterial.ambientColor = parseColor(tokens);
                    } else if (key.equals("kd")) {
                        currentMaterial.diffuseColor = parseColor(tokens);
                    } else if (key.equals("ks")) {
                        currentMaterial.specularColor = parseColor(tokens);
                    } else if (key.equals("tr") || key.equals("d")) {
                        currentMaterial.opacity = Float.parseFloat(tokens[1]);
                    } else if (key.equals("ns")) {
                        currentMaterial.shininess = Float.parseFloat(tokens[1]);
                    } else if (key.equals("map_d")) {
                        currentMaterial.alphaTexFilename = file.parent().child(tokens[1]).path();
                    } else if (key.equals("map_ka")) {
                        currentMaterial.ambientTexFilename = file.parent().child(tokens[1]).path();
                    } else if (key.equals("map_kd")) {
                        currentMaterial.diffuseTexFilename = file.parent().child(tokens[1]).path();
                    } else if (key.equals("map_ks")) {
                        currentMaterial.specularTexFilename = file.parent().child(tokens[1]).path();
                    } else if (key.equals("map_ns")) {
                        currentMaterial.shininessTexFilename = file.parent().child(tokens[1]).path();
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            return;
        }

        // last material
        ModelMaterial mat = currentMaterial.build();
        materials.add(mat);

        return;
    }

    private Color parseColor(String[] tokens) {
        float r = Float.parseFloat(tokens[1]);
        float g = Float.parseFloat(tokens[2]);
        float b = Float.parseFloat(tokens[3]);
        float a = 1;
        if (tokens.length > 4) {
            a = Float.parseFloat(tokens[4]);
        }

        return new Color(r, g, b, a);
    }

    public ModelMaterial getMaterial(final String name) {
        for (final ModelMaterial m : materials)
            if (m.id.equals(name)) return m;
        ModelMaterial mat = new ModelMaterial();
        mat.id = name;
        mat.diffuse = new Color(Color.WHITE);
        materials.add(mat);
        return mat;
    }

    private static class ObjMaterial {
        String materialName = "default";
        Color ambientColor;
        Color diffuseColor;
        Color specularColor;
        float opacity;
        float shininess;
        String alphaTexFilename;
        String ambientTexFilename;
        String diffuseTexFilename;
        String shininessTexFilename;
        String specularTexFilename;

        public ObjMaterial() {
            reset();
        }

        public ModelMaterial build() {
            ModelMaterial mat = new ModelMaterial();
            mat.id = materialName;
            mat.ambient = ambientColor == null ? null : new Color(ambientColor);
            mat.diffuse = new Color(diffuseColor);
            mat.specular = new Color(specularColor);
            mat.opacity = opacity;
            mat.shininess = shininess;
            addTexture(mat, alphaTexFilename, ModelTexture.USAGE_TRANSPARENCY);
            addTexture(mat, ambientTexFilename, ModelTexture.USAGE_AMBIENT);
            addTexture(mat, diffuseTexFilename, ModelTexture.USAGE_DIFFUSE);
            addTexture(mat, specularTexFilename, ModelTexture.USAGE_SPECULAR);
            addTexture(mat, shininessTexFilename, ModelTexture.USAGE_SHININESS);

            return mat;
        }

        private void addTexture(ModelMaterial mat, String texFilename, int usage) {
            if (texFilename != null) {
                ModelTexture tex = new ModelTexture();
                tex.usage = usage;
                tex.fileName = texFilename;
                if (mat.textures == null) mat.textures = new Array<ModelTexture>(1);
                mat.textures.add(tex);
            }
        }

        public void reset() {
            ambientColor = null;
            diffuseColor = Color.WHITE;
            specularColor = Color.WHITE;
            opacity = 1.f;
            shininess = 0.f;
            alphaTexFilename = null;
            ambientTexFilename = null;
            diffuseTexFilename = null;
            shininessTexFilename = null;
            specularTexFilename = null;
        }
    }
}
