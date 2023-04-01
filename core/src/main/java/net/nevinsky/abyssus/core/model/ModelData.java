package net.nevinsky.abyssus.core.model;

import com.badlogic.gdx.graphics.g3d.model.data.ModelAnimation;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMaterial;
import com.badlogic.gdx.graphics.g3d.model.data.ModelNode;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class ModelData {
    private final String id;
    private final short[] version = new short[2];
    public final List<ModelMesh> meshes = new ArrayList<>();
    private final List<ModelMaterial> materials = new ArrayList<>();
    public final List<ModelNode> nodes = new ArrayList<>();
    public final List<ModelAnimation> animations = new ArrayList<>();

    public void addMesh(ModelMesh mesh) {
        for (ModelMesh other : meshes) {
            if (other.id.equals(mesh.id)) {
                throw new GdxRuntimeException("Mesh with id '" + other.id + "' already in model");
            }
        }
        meshes.add(mesh);
    }
}
