package com.mbrlabs.mundus.editor.core.project;

import com.mbrlabs.mundus.editor.events.EventBus;
import com.mbrlabs.mundus.editor.events.ProjectChangedEvent;
import com.mbrlabs.mundus.editor.events.ProjectFileChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

@Slf4j
@Component
public class ProjectWatcher {

    private final EventBus eventBus;
    private WatchService watchService;

    private final ExecutorService watchExecutorService = Executors.newSingleThreadExecutor();
    private Future<?> watchFuture;

    public ProjectWatcher(EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register((ProjectChangedEvent.ProjectChangedListener) this::restart);
    }

    @SuppressWarnings("unchecked")
    public void restart(ProjectChangedEvent projectEvent) {
        if (watchFuture != null) {
            watchFuture.cancel(true);
        }
        watchFuture = watchExecutorService.submit(() -> {
            try {
                watchService = FileSystems.getDefault().newWatchService();
                var watchDir = Path.of(projectEvent.getProjectContext().path);
                watchDir(watchDir);

                WatchKey key;
                while ((key = watchService.take()) != null) {
                    for (WatchEvent<?> event : key.pollEvents()) {
                        var kind = event.kind();

                        if (kind == OVERFLOW) {
                            continue;
                        }

                        var filename = ((WatchEvent<Path>) event).context();
                        if (filename.toString().startsWith(".")) {
                            // ignore files and folders which name starts with "."
                            continue;
                        }

                        var child = ((Path) key.watchable()).resolve(filename);
                        if (child.toFile().isDirectory()) {
                            watchDir(child);
                        }

                        eventBus.post(new ProjectFileChangedEvent(child));
                        log.info("File changed: {}", child);
                    }

                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }
                }
            } catch (Exception e) {
                log.error("ERROR", e);
            }
        });
    }

    private void watchDir(Path watchDir) throws IOException {
        PathUtils.walk(watchDir, DirectoryFileFilter.DIRECTORY, 100500, false)
                .forEach(p -> {
                    try {
                        log.debug("register for watching changes: '{}'", p);
                        p.register(watchService, ENTRY_CREATE, ENTRY_DELETE);
                    } catch (Exception e) {
                        log.error("ERROR", e);
                    }
                });
    }
}
