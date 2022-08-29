package com.mbrlabs.mundus.commons.ac3d.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

//todo make unmodifable
@Getter
public class Ac3dModel {
    private final List<Ac3dMaterial> materials = new ArrayList<>();
    private final List<Ac3dObject> objects = new ArrayList<>();
}
