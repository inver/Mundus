package com.mbrlabs.mundus.commons.loader;

import com.badlogic.gdx.files.FileHandle;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Objects;

public class BaseTest {

    @SneakyThrows
    protected BufferedReader getReader(String path) {
        var f = new File(Objects.requireNonNull(this.getClass().getResource(path)).getFile());
        return new BufferedReader(new FileReader(f));
    }

    @SneakyThrows
    protected FileHandle getHandle(String path) {
        return new FileHandle(new File(Objects.requireNonNull(this.getClass().getResource(path)).getFile()));
    }

    @SneakyThrows
    protected File getFile(String path) {
        return new File(Objects.requireNonNull(this.getClass().getResource(path)).getFile());
    }
}
