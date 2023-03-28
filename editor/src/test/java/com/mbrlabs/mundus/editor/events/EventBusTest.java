package com.mbrlabs.mundus.editor.events;

import com.mbrlabs.mundus.editor.events.child_package.SomeListenerClass;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

public class EventBusTest {

    private final EventBus eventBus = new EventBus();

    private static int eventCount = 0;

    @Test
    public void doTest() {
        var testClass = new TestClass();
        var testClass2 = new SomeListenerClass(eventBus);
        testClass2.init();
        eventBus.register(testClass);
        eventBus.register((LogEvent.LogEventListener) event -> {
            eventCount++;
        });
        eventBus.post(new LogEvent(""));

        Assert.assertEquals(2, eventCount);
        Assert.assertEquals(1, testClass2.getI());
    }

    private static class TestClass implements LogEvent.LogEventListener {

        @Override
        public void onLogEvent(@NotNull LogEvent event) {
            eventCount++;
        }
    }
}
