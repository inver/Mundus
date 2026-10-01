package net.nevinsky.abyssus.core.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMaterial;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNode;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNodePart;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.GdxRuntimeException;
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;
import net.nevinsky.abyssus.core.mesh.MeshPart;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelTest {

    private static ModelMaterial material(String id) {
        var m = new ModelMaterial();
        m.id = id;
        return m;
    }

    private static ModelNode node(String id, String meshPartId, String materialId) {
        var part = new ModelNodePart();
        part.meshPartId = meshPartId;
        part.materialId = materialId;
        var node = new ModelNode();
        node.id = id;
        node.parts = new ModelNodePart[]{part};
        return node;
    }

    private static MeshPart meshPart(String id) {
        var part = new MeshPart();
        part.id = id;
        return part;
    }

    @Test
    void materialsAreKeyedByExactId() {
        var model = new Model();
        model.loadMaterials(List.of(material("Red"), material("red")), new TextureProvider.FileTextureProvider());

        assertEquals(2, model.getMaterials().size());
        assertNotSame(model.getMaterials().get("Red"), model.getMaterials().get("red"));
    }

    @Test
    void nodeReferencesMaterialByExactId() {
        var model = new Model();
        model.loadMaterials(List.of(material("Red"), material("red")), new TextureProvider.FileTextureProvider());

        model.loadNodes(Map.of("p", meshPart("p")), List.of(node("n", "p", "red")));

        assertEquals("red", model.getNodes().get(0).parts.get(0).material.id);
    }

    @Test
    void invalidNodeErrorNamesTheMissingReferences() {
        var model = new Model();
        model.loadMaterials(List.of(material("m")), new TextureProvider.FileTextureProvider());

        var ex = assertThrows(GdxRuntimeException.class,
                () -> model.loadNodes(Map.of(), List.of(node("n", "missing_part", "missing_material"))));
        assertTrue(ex.getMessage().contains("missing_part"), ex.getMessage());
        assertTrue(ex.getMessage().contains("missing_material"), ex.getMessage());
    }

    @Test
    void pbrMaterialPropertiesBecomeAttributes() {
        var pbr = new PbrModelMaterial();
        pbr.id = "pbr";
        pbr.diffuse = Color.RED;
        pbr.baseColor = new Color(0.5f, 0.5f, 0.5f, 1f);
        pbr.metallic = 0.25f;
        pbr.roughness = 0.75f;
        pbr.doubleSided = true;
        pbr.alphaMode = PbrModelMaterial.AlphaMode.MASK;
        pbr.alphaCutoff = 0.3f;

        var model = new Model();
        model.loadMaterials(List.of(pbr), new TextureProvider.FileTextureProvider());
        var result = model.getMaterials().get("pbr");

        assertNotNull(result.get(ColorAttribute.Diffuse), "legacy attributes are kept");
        assertEquals(0.5f, ((PBRColorAttribute) result.get(PBRColorAttribute.BaseColorFactor)).color.r, 1e-5f);
        assertEquals(0.25f, ((PBRFloatAttribute) result.get(PBRFloatAttribute.Metallic)).value, 1e-5f);
        assertEquals(0.75f, ((PBRFloatAttribute) result.get(PBRFloatAttribute.Roughness)).value, 1e-5f);
        assertEquals(GL20.GL_NONE, ((IntAttribute) result.get(IntAttribute.CullFace)).value);
        assertEquals(0.3f, ((FloatAttribute) result.get(FloatAttribute.AlphaTest)).value, 1e-5f);
    }

    @Test
    void pbrBlendModeAddsBlending() {
        var pbr = new PbrModelMaterial();
        pbr.id = "blend";
        pbr.alphaMode = PbrModelMaterial.AlphaMode.BLEND;
        pbr.opacity = 0.6f;

        var model = new Model();
        model.loadMaterials(List.of(pbr), new TextureProvider.FileTextureProvider());

        var blending = (BlendingAttribute) model.getMaterials().get("blend").get(BlendingAttribute.Type);
        assertEquals(0.6f, blending.opacity, 1e-5f);
    }

    @Test
    void maxBonesIsLargestBoneCountOfNodeParts() {
        var model = new Model();
        model.loadMaterials(List.of(material("m")), new TextureProvider.FileTextureProvider());
        assertEquals(0, model.getMaxBones());

        var bone1 = new ModelNode();
        bone1.id = "b1";
        var bone2 = new ModelNode();
        bone2.id = "b2";
        var skinned = node("skinned", "p", "m");
        var bones = new ArrayMap<String, Matrix4>();
        bones.put("b1", new Matrix4());
        bones.put("b2", new Matrix4());
        skinned.parts[0].bones = bones;
        skinned.children = new ModelNode[]{bone1, bone2};

        model.loadNodes(Map.of("p", meshPart("p")), List.of(skinned));

        assertEquals(2, model.getMaxBones());
    }

    @Test
    void baseColorFactorFallsBackToDiffuse() {
        var pbr = new PbrModelMaterial();
        pbr.id = "pbr";
        pbr.baseColor = new Color(0.5f, 0.25f, 0.125f, 1f);
        pbr.metallic = 0f;

        var model = new Model();
        model.loadMaterials(List.of(pbr), new TextureProvider.FileTextureProvider());

        var diffuse = (ColorAttribute) model.getMaterials().get("pbr").get(ColorAttribute.Diffuse);
        assertNotNull(diffuse);
        assertEquals(0.25f, diffuse.color.g, 1e-5f);
    }

    @Test
    void hasPbrMaterialsOnlyForPbrMaterials() {
        var model = new Model();
        model.loadMaterials(List.of(material("plain")), new TextureProvider.FileTextureProvider());
        assertTrue(!model.hasPbrMaterials());

        var pbr = new PbrModelMaterial();
        pbr.id = "pbr";
        pbr.roughness = 0.5f;
        model.loadMaterials(List.of(pbr), new TextureProvider.FileTextureProvider());
        assertTrue(model.hasPbrMaterials());
    }
}
