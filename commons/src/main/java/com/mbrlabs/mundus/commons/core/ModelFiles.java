package com.mbrlabs.mundus.commons.core;

import com.badlogic.gdx.files.FileHandle;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class ModelFiles {
    private final FileHandle main;
    private final List<FileHandle> dependencies = new ArrayList<>();

    public void copyTo(FileHandle dest) {
        main.copyTo(dest);
        dependencies.forEach(dep -> dep.copyTo(dest));
    }
}
