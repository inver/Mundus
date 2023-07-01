package com.mbrlabs.mundus.commons.scene3d;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ToString(of = {"id", "name"})
@Getter
public class HierarchyNode implements Serializable {
    // Entity id
    private final int id;
    private final String name;

    //    private final Type type;
    private final List<HierarchyNode> children = new ArrayList<>();

    @JsonCreator()
    public HierarchyNode(@JsonProperty("id") int id, @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }

    public void addChild(int id, String name) {
        children.add(new HierarchyNode(id, name));
    }

    public void addChild(HierarchyNode child) {
        children.add(child);
    }


}
