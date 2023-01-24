package com.mbrlabs.mundus.editor.ui.modules.dialogs.assets;

import com.mbrlabs.mundus.commons.assets.Asset;

public interface AssetPickerListener {
    void onSelected(Asset<?> asset);
}
