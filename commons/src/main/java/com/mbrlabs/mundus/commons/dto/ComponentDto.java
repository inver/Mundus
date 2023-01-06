package com.mbrlabs.mundus.commons.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CameraDto.class, name = "CAMERA"),
        @JsonSubTypes.Type(value = TerrainComponentDto.class, name = "TERRAIN"),
})
@Getter
@Setter
public abstract class ComponentDto {
}
