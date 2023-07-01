package com.mbrlabs.mundus.editor.config.ui;

import com.mbrlabs.mundus.editor.ui.AppUi;
import com.mbrlabs.mundus.editor.ui.modules.outline.Outline;
import com.mbrlabs.mundus.editor.ui.modules.outline.OutlinePresenter;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

public class TestOutline extends Outline {

    @Getter
    private final AtomicInteger getSelectedItemCount = new AtomicInteger(0);

    public TestOutline(AppUi appUi,
                       OutlinePresenter outlinePresenter) {
        super(appUi, outlinePresenter);
    }

    @Override
    public int getSelectedEntityId() {
        getSelectedItemCount.incrementAndGet();
        return super.getSelectedEntityId();
    }
}
