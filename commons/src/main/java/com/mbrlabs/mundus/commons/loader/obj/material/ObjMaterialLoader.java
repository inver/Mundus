package com.mbrlabs.mundus.commons.loader.obj.material;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.model.data.ModelTexture;
import com.badlogic.gdx.utils.Array;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import static com.badlogic.gdx.graphics.g3d.model.data.ModelTexture.*;

@Slf4j
public class ObjMaterialLoader {
    private final static String MTL_NEWMTL = "newmtl";
    private final static String MTL_KA = "ka";
    private final static String MTL_KD = "kd";
    private final static String MTL_KS = "ks";
    private final static String MTL_KE = "ke";
    private final static String MTL_TF = "tf";
    private final static String MTL_ILLUM = "illum";
    private final static String MTL_D = "d";
    private final static String MTL_TR = "tr";
    private final static String MTL_D_DASHHALO = "-halo";
    private final static String MTL_NS = "ns";
    private final static String MTL_SHARPNESS = "sharpness";
    private final static String MTL_NI = "ni";
    private final static String MTL_MAP_KA = "map_ka";
    private final static String MTL_MAP_KD = "map_kd";
    private final static String MTL_MAP_KS = "map_ks";
    private final static String MTL_MAP_NS = "map_ns";
    private final static String MTL_MAP_D = "map_d";
    private final static String MTL_MAP_BUMP = "map_bump";
    private final static String MTL_DISP = "disp";
    private final static String MTL_DECAL = "decal";
    private final static String MTL_BUMP = "bump";
    private final static String MTL_REFL = "refl";
    public final static String MTL_REFL_TYPE_SPHERE = "sphere";
    public final static String MTL_REFL_TYPE_CUBE_TOP = "cube_top";
    public final static String MTL_REFL_TYPE_CUBE_BOTTOM = "cube_bottom";
    public final static String MTL_REFL_TYPE_CUBE_FRONT = "cube_front";
    public final static String MTL_REFL_TYPE_CUBE_BACK = "cube_back";
    public final static String MTL_REFL_TYPE_CUBE_LEFT = "cube_left";
    public final static String MTL_REFL_TYPE_CUBE_RIGHT = "cube_right";

    private final boolean failedOnUnknown;

    public ObjMaterialLoader() {
        this(false);
    }

    public ObjMaterialLoader(boolean failedOnUnknown) {
        this.failedOnUnknown = failedOnUnknown;
    }

    public Array<ObjModelMaterial> load(File file) {
        var res = new Array<ObjModelMaterial>();

        ObjModelMaterial current = null;
        try (var bufferedReader = new BufferedReader(new FileReader(file))) {
            var lineCount = 0;
            String line;
            String[] tokens;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();

                if (line.length() == 0 || line.startsWith("#")) {
                    continue;
                }
                tokens = line.split("\\s+");
                final String key = tokens[0].toLowerCase();

                if (MTL_NEWMTL.equals(key)) {
                    current = new ObjModelMaterial();
                    res.add(current);
                    if (tokens.length > 1) {
                        current.id = tokens[1];
                    } else {
                        current.id = "default";
                    }
                    continue;
                }
                if (current == null) {
                    break;
                }

                switch (key) {
                    case MTL_KA:
                    case MTL_KD:
                    case MTL_KS:
                    case MTL_KE:
                        processReflectivityTransmissivity(key, tokens, current);
                        break;
                    case MTL_TF:
                        processTf(tokens, current);
                        break;
                    case MTL_ILLUM:
                        processIllum(line, current);
                        break;
                    case MTL_D:
                    case MTL_TR:
                        current.opacity = Float.parseFloat(tokens[1]);
                        break;
                    case MTL_NS:
                        current.shininess = Float.parseFloat(tokens[1]);
                        break;
                    case MTL_SHARPNESS:
                        current.sharpness = Float.parseFloat(tokens[1]);
                        break;
                    case MTL_NI:
                        processNi(tokens, current);
                        break;
                    case MTL_MAP_KA:
                    case MTL_MAP_KD:
                    case MTL_MAP_KS:
                    case MTL_MAP_NS:
                    case MTL_DECAL:
                    case MTL_BUMP:
                    case MTL_MAP_BUMP:
                    case MTL_DISP:
                    case MTL_MAP_D:
                        processMapDecalDispBump(key, tokens, current);
                        break;
                    case MTL_REFL:
                        processRefl(tokens, current);
                        break;
                    default:
                        if (failedOnUnknown) {
                            throw new IllegalStateException("Fail to parse '" + key + "' at line " + lineCount
                                    + " unknown line |" + line + "|");
                        }
                        log.warn("line " + lineCount + " unknown line |" + line + "|");
                        break;
                }
                lineCount++;
            }
        } catch (Exception e) {
            log.error("ERROR", e);
            throw new RuntimeException(e);
        }
        return res;
    }

    private void processIllum(String line, ObjModelMaterial material) {
        line = line.substring(MTL_ILLUM.length()).trim();
        var illumModel = Integer.parseInt(line);
        if ((illumModel < 0) || (illumModel > 10)) {
            log.error("Got illum model value out of range (0 to 10 inclusive is allowed), value=" + illumModel + ", line=" + line);
            return;
        }
        material.illumination = MaterialIllumination.of(illumModel);
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

    private void processReflectivityTransmissivity(String fieldName, String[] tokens, ObjModelMaterial material) {
        if (tokens.length < 2) {
            return;
        }

        if ("xyz".equals(tokens[1])) {
            log.warn("Sorry Charlie, this parse doesn't handle 'xyz' parsing. Mostly bcz I don't know that to " +
                    "do with it at this moment");

//            if (tokens.length < 2) {
//                log.error("Got xyz line with not enough x/y/z tokens, need at least one value for x, found " + (tokens.length - 1) + " line = |" + line + "|");
//                return;
//            }
//            float x = Float.parseFloat(tokens[1]);
//            float y = x;
//            float z = x;
//            if (tokens.length > 2) {
//                y = Float.parseFloat(tokens[2]);
//            }
//            if (tokens.length > 3) {
//                z = Float.parseFloat(tokens[3]);
//            }
            return;
        }
        if ("spectral".equals(tokens[1])) {
            log.warn("Sorry Charlie, this parse doesn't handle 'spectral' parsing.  (Mostly because I can't find" +
                    " any info on the spectra.rfl file.)");
            return;
        }

        if (MTL_KA.equals(fieldName)) {
            material.ambient = parseColor(tokens);
            return;
        }
        if (MTL_KD.equals(fieldName)) {
            material.diffuse = parseColor(tokens);
            return;
        }
        if (MTL_KS.equals(fieldName)) {
            material.specular = parseColor(tokens);
            return;
        }
        if (MTL_KE.equals(fieldName)) {
            material.emissive = parseColor(tokens);
        }
    }

    /**
     * Ni optical_density
     * <p>
     * Specifies the optical density for the surface.  This is also known as
     * index of refraction.
     * <p>
     * "optical_density" is the value for the optical density.  The values can
     * range from 0.001 to 10.  A value of 1.0 means that light does not bend
     * as it passes through an object.  Increasing the optical_density
     * increases the amount of bending.  Glass has an index of refraction of
     * about 1.5.  Values of less than 1.0 produce bizarre results and are not
     * recommended.
     */
    private void processNi(String[] tokens, ObjModelMaterial current) {
        log.warn("I don't know how connect this parameter and libgdx now");
    }

    /**
     * Tf r g b
     * Tf spectral file.rfl factor
     * Tf xyz x y z
     * <p>
     * To specify the transmission filter of the current material, you can use
     * the "Tf" statement, the "Tf spectral" statement, or the "Tf xyz"
     * statement.
     * <p>
     * Any light passing through the object is filtered by the transmission
     * filter, which only allows the specifiec colors to pass through.  For
     * example, Tf 0 1 0 allows all the green to pass through and filters out
     * all the red and blue.
     * <p>
     * Tip	These statements are mutually exclusive.  They cannot be used
     * concurrently in the same material.
     * <p>
     * Tf r g b
     * <p>
     * The Tf statement specifies the transmission filter using RGB values.
     * <p>
     * "r g b" are the values for the red, green, and blue components of the
     * atmosphere.  The g and b arguments are optional.  If only r is
     * specified, then g, and b are assumed to be equal to r.  The r g b values
     * are normally in the range of 0.0 to 1.0.  Values outside this range
     * increase or decrease the relectivity accordingly.
     * <p>
     * Tf spectral file.rfl factor
     * <p>
     * The "Tf spectral" statement specifies the transmission filterusing a
     * spectral curve.
     * <p>
     * "file.rfl" is the name of the .rfl file.
     * "factor" is an optional argument.
     * "factor" is a multiplier for the values in the .rfl file and defaults
     * to 1.0, if not specified.
     * <p>
     * Tf xyz x y z
     * <p>
     * The "Ks xyz" statement specifies the specular reflectivity using CIEXYZ
     * values.
     * <p>
     * "x y z" are the values of the CIEXYZ color space.  The y and z
     * arguments are optional.  If only x is specified, then y and z are
     * assumed to be equal to x.  The x y z values are normally in the range of
     * 0 to 1.  Values outside this range will increase or decrease the
     * intensity of the light transmission accordingly.
     *
     * @param tokens
     * @param current
     */
    private void processTf(String[] tokens, ObjModelMaterial current) {
        log.warn("I don't know how connect this parameter and libgdx now");
        // this is very similar to processReflectivityTransmissivity method for parsing
    }

    // NOTE: From what I can tell, nobody ever implements these
    // options.  In fact I suspect most people only implement map_Kd,
    // if even that.
    //
    // For map_Ka, map_Kd, or map_Ks the options are;
    //
    // 	-blendu on | off
    // 	-blendv on | off
    // 	-cc on | off
    // 	-clamp on | off
    // 	-mm base gain
    // 	-o u v w
    // 	-s u v w
    // 	-t u v w
    // 	-texres value
    //
    // For map_Ns, map_d, decal, or disp they are;
    //
    // 	-blendu on | off
    // 	-blendv on | off
    // 	-clamp on | off
    // 	-imfchan r | g | b | m | l | z
    // 	-mm base gain
    // 	-o u v w
    // 	-s u v w
    // 	-t u v w
    // 	-texres value
    //
    // Note the absence of -cc adn the addition of imfchan
    //
    // For bump the options are;
    //
    // 	-bm mult
    // 	-clamp on | off
    // 	-blendu on | off
    // 	-blendv on | off
    // 	-imfchan r | g | b | m | l | z
    // 	-mm base gain
    // 	-o u v w
    // 	-s u v w
    // 	-t u v w
    // 	-texres value
    //
    // Note the addition of -bm.
    private void processMapDecalDispBump(String key, String[] tokens, ObjModelMaterial material) {
        if (tokens.length < 2) {
            return;
        }

        var texture = new ModelTexture();
        texture.fileName = tokens[1].trim().replace("\\", "/").replace("\\\\", "/");
        texture.id = texture.fileName;
        if (MTL_MAP_KD.equals(key)) {
            texture.usage = USAGE_DIFFUSE;
        } else if (MTL_MAP_KS.equals(key)) {
            texture.usage = USAGE_SPECULAR;
        } else if (MTL_MAP_NS.equals(key)) {
            texture.usage = USAGE_SHININESS;
        } else if (MTL_MAP_KA.equals(key)) {
            texture.usage = USAGE_AMBIENT;
        } else if (MTL_MAP_D.equals(key)) {
            texture.usage = USAGE_TRANSPARENCY;
        } else if (MTL_DISP.equals(key)) {
            log.warn("Displacement map is here?");
            texture.usage = USAGE_UNKNOWN;
        } else if (MTL_DECAL.equals(key)) {
            log.warn("Displacement map is here?");
            texture.usage = USAGE_UNKNOWN;
        } else if (MTL_BUMP.equals(key) || MTL_MAP_BUMP.equals(key)) {
            texture.usage = USAGE_BUMP;
        } else {
            texture.usage = USAGE_NORMAL;
        }
        if (material.textures == null) {
            material.textures = new Array<>();
        }
        material.textures.add(texture);
        // @TODO: Add processing of the options...?
    }

    // ------------------------------------------------------
    // From the wavefront OBJ file spec;
    // ------------------------------------------------------
    //
    // refl -type sphere -mm 0 1 clouds.mpc
    //
    // refl -type sphere -options -args filename
    //
    //  Specifies an infinitely large sphere that casts reflections onto the
    // material.  You specify one texture file.
    //
    //  "filename" is the color texture file, color procedural texture file, or
    // image file that will be mapped onto the inside of the shape.
    //
    //  refl -type cube_side -options -args filenames
    //
    //  Specifies an infinitely large sphere that casts reflections onto the
    // material.  You can specify different texture files for the "top",
    // "bottom", "front", "back", "left", and "right" with the following
    // statements:
    //
    //  refl -type cube_top
    //  refl -type cube_bottom
    //  refl -type cube_front
    //  refl -type cube_back
    //  refl -type cube_left
    //  refl -type cube_right
    //
    //  "filenames" are the color texture files, color procedural texture
    // files, or image files that will be mapped onto the inside of the shape.
    //
    //  The "refl" statements for sphere and cube can be used alone or with
    //  any combination of the following options.  The options and their
    // arguments are inserted between "refl" and "filename".
    private void processRefl(String[] tokens, ObjModelMaterial material) {
        log.warn("We don't parse reflections directive now, but you could do pull request=)");
    }
}
