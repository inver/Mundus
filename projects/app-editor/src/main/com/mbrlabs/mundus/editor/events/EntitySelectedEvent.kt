package com.mbrlabs.mundus.editor.events

class EntitySelectedEvent(val entityId: Int) : Event {
    interface EntitySelectedListener {
        @Subscribe
        fun onEntitySelected(event: EntitySelectedEvent)
    }

}