package net.nevinsky.abyssus.lib.assets.gltf.extension;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class KHRMaterialsIOR implements Extension {

    public static final String NAME = "KHR_materials_ior";

    private float ior = 1.5f;
}