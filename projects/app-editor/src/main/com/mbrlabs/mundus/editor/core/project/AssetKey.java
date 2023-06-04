package com.mbrlabs.mundus.editor.core.project;

import com.mbrlabs.mundus.commons.assets.AssetType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(of = {"type", "name"})
@ToString(of = {"type", "name"})
public class AssetKey {
    private final AssetType type;
    private final String name;
}