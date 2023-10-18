package com.mbrlabs.mundus.editor.utils;

import com.mbrlabs.mundus.editor.Main;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

@UtilityClass
@Slf4j
public final class AppUtils {

    public static Version getAppVersion() {
        Properties prop = new Properties();
        try (var is = Main.class.getResourceAsStream("/version.properties")) {
            prop.load(is);

            return Version.builder()
                    .appName(prop.getProperty("appName"))
                    .appVersion(prop.getProperty("appVersion"))
                    .buildTime(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(prop.getProperty("buildTime")))
                    .build();
        } catch (Exception e) {
            log.error("ERROR", e);
        }

        return Version.builder()
                .appName("3D scene editor")
                .appVersion("0.0.1")
                .buildTime(new Date())
                .build();
    }

    @Builder
    @Getter
    public static class Version {
        private String appName;
        private String appVersion;
        private Date buildTime;

        @Override
        public String toString() {
            return appName + " " + appVersion;
        }
    }
}
