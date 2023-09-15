package com.mbrlabs.mundus.editor;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.kotcrab.vis.ui.util.OsUtils;
import com.mbrlabs.mundus.editor.config.InitListener;
import com.mbrlabs.mundus.editor.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
public class Main {
    public static void main(String[] args) {
        var config = new Lwjgl3ApplicationConfiguration();
        var listener = new InitListener();
        config.setWindowListener(listener);

        // Set initial window size. See https://github.com/mbrlabs/Mundus/issues/11
        var dm = Lwjgl3ApplicationConfiguration.getDisplayMode();
        if (OsUtils.isMac()) {
            config.setWindowedMode(
                    Float.valueOf(dm.width * 0.80f).intValue(), Float.valueOf(dm.height * 0.80f).intValue());
        } else {
            config.setWindowedMode(
                    Float.valueOf(dm.width * 0.95f).intValue(), Float.valueOf(dm.height * 0.95f).intValue());
        }

        config.setTitle(AppUtils.getAppVersion().toString());
        config.setWindowSizeLimits(1350, 1, 9999, 9999);
        config.setWindowPosition(-1, -1);
        config.setWindowIcon("icon/logo.png");

        new Lwjgl3Application(listener, config);
        log.info("Shutting down [{}]", AppUtils.getAppVersion());
    }
}