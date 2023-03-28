package com.mbrlabs.mundus.editor.core.shader;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.util.function.Consumer;

import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

@Slf4j
public class FileWatcher implements Runnable {

    private final Path watchDir;
    private final Consumer<String> shaderConsumer;

    public FileWatcher(String watchDir, Consumer<String> shaderConsumer) {
        this.watchDir = Path.of(watchDir);
        this.shaderConsumer = shaderConsumer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        try (
                var watcher = FileSystems.getDefault().newWatchService()
        ) {

            for (; ; ) {

                // wait for key to be signaled
                WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException x) {
                    return;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    // This key is registered only
                    // for ENTRY_CREATE events,
                    // but an OVERFLOW event can
                    // occur regardless if events
                    // are lost or discarded.
                    if (kind == OVERFLOW) {
                        continue;
                    }

                    // The filename is the
                    // context of the event.
                    Path filename = ((WatchEvent<Path>) event).context();

                    // Verify that the new
                    //  file is a text file.
                    try {
                        // Resolve the filename against the directory.
                        // If the filename is "test" and the directory is "foo",
                        // the resolved name is "test/foo".
                        var child = watchDir.resolve(filename);
                        if (!Files.probeContentType(child).equals("text/plain")) {
                            log.error("New file '{}' is not a plain text file.%n", filename);
                        }
                    } catch (IOException e) {
                        log.error("ERROR", e);
                    }
                }

                // Reset the key -- this step is critical if you want to
                // receive further watch events.  If the key is no longer valid,
                // the directory is inaccessible so exit the loop.
                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        } catch (Exception e) {
            log.error("ERROR", e);
        }
    }
}
