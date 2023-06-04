package com.mbrlabs.mundus.editor.events

class EntityModifiedEvent(var entityId: Int) : Event {
    interface EntityModifiedListener {
        @Subscribe
        fun onEntityModified(event: EntityModifiedEvent)
    }

}