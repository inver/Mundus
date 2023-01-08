package com.mbrlabs.mundus.editor.ui.modules.dock

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.*
import com.kotcrab.vis.ui.widget.tabbedpane.Tab
import com.mbrlabs.mundus.editor.events.LogEvent
import com.mbrlabs.mundus.editor.events.LogType
import java.text.SimpleDateFormat
import java.util.*

/**
 * Docked log bar for displaying LogEvents with timestamps
 */
class LogBar : Tab(false, false), LogEvent.LogEventListener {

    private val root = VisTable()
    private val logTable = VisTable()
    private val pane = VisScrollPane(logTable)

    private val logOpsMenu = PopupMenu()
    private val clearLogsButton = MenuItem("Clear Logs")

    private val maxLogSize = 75
    private val dateFormat = SimpleDateFormat("HH:mm:ss")

    private val logTextPadding = 4f;
    private var errorColor = Color(222f / 255f, 67f / 255f, 67f / 255f, 1f);

    // True when new entries are in the log and log is not the active tab
    var newEntries = false

    init {
        initUi()
    }

    private fun initUi() {
        root.setBackground("window-bg")
        root.left().top()
        root.add(pane).top().fillX().expandX()

        pane.fadeScrollBars = false

        logOpsMenu.addItem(clearLogsButton)
        registerListeners()
    }

    private fun registerListeners() {
        // Pop up menu on right click
        //todo
//        root.addListener(object : InputListener() {
//            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
//                return true
//            }
//
//            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
//                if (event!!.button == Input.Buttons.RIGHT) {
//                    logOpsMenu.showMenu(
//                        UI, Gdx.input.x.toFloat(),
//                        (Gdx.graphics.height - Gdx.input.y).toFloat()
//                    )
//                }
//            }
//
//            override fun enter(event: InputEvent, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
//                // Give scroll focus to pane automatically when mouse enters
//                UI.scrollFocus = pane
//            }
//
//            override fun exit(event: InputEvent, x: Float, y: Float, pointer: Int, toActor: Actor?) {
//                // Only clear focus if the exit to another actor is NOT an actor within the LogBars root
//                if (toActor?.isDescendantOf(root) != true)
//                    UI.scrollFocus = null
//            }
//        })

        clearLogsButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                logTable.clearChildren()
            }
        })
    }

    override fun onShow() {
        super.onShow()
        newEntries = false
    }

    override fun getTabTitle(): String {
        if (newEntries)
            return "Log*"

        return "Log"
    }

    override fun getContentTable(): Table {
        return root
    }

    override fun onLogEvent(event: LogEvent) {
        addLogMessage(event)
    }

    /**
     * Appends new log message with a time stamp to the log table, then scrolls to most recent entry and
     * removes old entries.
     */
    private fun addLogMessage(event: LogEvent) {
        if (!isActiveTab) {
            newEntries = true
        }

        val timeStamp = dateFormat.format(Date())

        val logString = buildString {
            append("[")
            append(timeStamp)
            append("] ")
            append("[")
            append(event.logType)
            append("] ")
            append(event.logMessage)
        }

        val visLabel: VisLabel = when (event.logType) {
            LogType.INFO -> VisLabel(logString)
            LogType.ERROR -> VisLabel(logString, errorColor)
        }

        logTable.add(visLabel).left().pad(logTextPadding).expand().row()

        // Remove oldest entry
        if (logTable.cells.size > maxLogSize)
            logTable.removeActorAt(0, true)

        scrollToBottom()
    }

    private fun scrollToBottom() {
        // Update layout and scroll to the latest (bottom) log message
        pane.layout()
        pane.scrollTo(0f, 0f, 0f, 0f)
    }
}