package com.mbrlabs.mundus.commons.model;

import com.badlogic.gdx.files.FileHandle;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.nevinsky.abyssus.core.model.Model;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class ImportedModel {
    private final Model model;
    private final FileHandle main;
    private final List<FileHandle> dependencies = new ArrayList<>();

    public String name() {
        return main.name();
    }

    public boolean exists() {
        return main.exists();
    }
}
