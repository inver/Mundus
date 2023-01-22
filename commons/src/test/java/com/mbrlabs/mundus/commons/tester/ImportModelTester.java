package com.mbrlabs.mundus.commons.tester;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImportModelTester {

    public static void main(String[] args) {
        var config = new Lwjgl3ApplicationConfiguration();
        var listener = new TesterListener();
        config.setWindowListener(listener);

        var dm = Lwjgl3ApplicationConfiguration.getDisplayMode();
        config.setWindowedMode(
                Float.valueOf(dm.width * 0.80f).intValue(), Float.valueOf(dm.height * 0.80f).intValue()
        );
        config.setTitle("Import model tester");
        config.setWindowSizeLimits(1350, 1, 9999, 9999);
        config.setWindowPosition(-1, -1);

        new Lwjgl3Application(listener, config);
        log.info("Shutting down");
    }
}