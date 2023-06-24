package net.nevinsky.abyssus.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import com.kotcrab.vis.ui.util.OsUtils;
import com.mbrlabs.mundus.editor.Editor;
import com.mbrlabs.mundus.editor.config.EditorAppListener;
import com.mbrlabs.mundus.editor.utils.AppUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DesktopEditor {
    public static void main(String[] args) {
        var config = new Lwjgl3ApplicationConfiguration();
        var listener = new DesktopListener();
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

    @Getter
    @Setter
    private static class DesktopListener extends Lwjgl3WindowAdapter implements EditorAppListener {
        private Editor editor;

        @Override
        public boolean closeRequested() {
            return getEditor().closeRequested();
        }
    }
}