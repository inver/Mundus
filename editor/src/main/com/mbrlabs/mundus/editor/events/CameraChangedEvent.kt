package com.mbrlabs.mundus.editor.events

class CameraChangedEvent(val entityId: Int) : Event {

    interface CameraChangedListener {
        @Subscribe
        fun onCameraChanged(event: CameraChangedEvent)
    }
}
