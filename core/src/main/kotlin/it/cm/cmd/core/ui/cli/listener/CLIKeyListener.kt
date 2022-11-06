package it.cm.cmd.core.ui.cli.listener

import it.cm.cmd.commands.CommandConstants
import it.cm.common.ui.UIHandler
import it.cm.cmd.core.ui.cli.CLIFrame2
import it.cm.cmd.core.ui.cli._interface.ITextPaneInputHandler
import it.cm.cmd.core.ui.cli._interface.ITextPaneOutputHandler
import it.cm.cmd.core.ui.cli.listener.actionMap.KeyMapAction
import it.cm.cmd.core.ui.cli.listener.keyboard.TabKey
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.Action
import javax.swing.KeyStroke
import javax.swing.text.DefaultEditorKit
import javax.swing.text.JTextComponent
import javax.swing.text.JTextComponent.KeyBinding
import javax.swing.text.Keymap


class CLIKeyListener(
    paneKeyBinding: Keymap
    , paneActions: Array<Action>
    , private val sessionKey: String
    , private val textPaneInputHandler: ITextPaneInputHandler
    , private val textPaneOutputHandler: ITextPaneOutputHandler
    , private val tabID: String
) : KeyMapAction(sessionKey, textPaneInputHandler, textPaneOutputHandler, tabID), KeyListener {

    private var process: Process? = null
    private val frame: CLIFrame2
        get() = UIHandler.getFrameFromSession(sessionKey) as CLIFrame2
    private val handleTab = TabKey()

    init {
        val newKeyBindings = keyBindings()
        JTextComponent.loadKeymap(paneKeyBinding, newKeyBindings, paneActions);
    }

    companion object {
        val IS_RUNNING = AtomicBoolean(false)
    }

    private fun keyBindings() =
        arrayOf(
            KeyBinding(
                KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK),
                DefaultEditorKit.copyAction
            )
            , KeyBinding(
                KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK),
                DefaultEditorKit.pasteAction
            )
            , KeyBinding(
                KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK),
                DefaultEditorKit.beepAction
            ) ,
        )

    override fun keyTyped(e: KeyEvent) {
//        fireKeyTyped(e);
        if (IS_RUNNING.get())
            e.consume()
    }

    override fun keyPressed(e: KeyEvent) {

        if (IS_RUNNING.get() && !(e.isControlDown && e.keyCode == KeyEvent.VK_Q))
            e.consume()
//        if (e.isControlDown) {
//            if (e.keyCode == KeyEvent.VK_V) {
////                e.consume();//must consume event to prevent default paste action
////                handlePasting();
//                return
//            }
//        } else if (e.keyCode == KeyEvent.VK_ENTER) {
//            e.consume()
//        } else if (e.keyCode == KeyEvent.VK_TAB) {
//            e.consume()
//            return
//        } else if (e.keyCode == KeyEvent.VK_ALT_GRAPH)
//            e.consume()
        when (e.keyCode) {
//            KeyEvent.VK_V -> e.consume()
            KeyEvent.VK_ENTER -> { e.consume(); handleTab.clear() }
            KeyEvent.VK_TAB -> e.consume()
            KeyEvent.VK_ALT_GRAPH -> { e.consume(); handleTab.clear() }
            else -> handleTab.clear()
        }
//        fireKeyPressed(e);
    }

    override fun keyReleased(e: KeyEvent) {
        if (IS_RUNNING.get() && !(e.isControlDown && e.keyCode == KeyEvent.VK_Q))
            e.consume()
        when (e.keyCode) {
            KeyEvent.VK_ENTER -> {
                val userInput = CommandConstants.replaceWithTemplate(textPaneInputHandler.getCommand(true))
                frame.getCurrentTab()?.let { it.history += userInput }
                runCommand(userInput)
    //            fireCommand(command)
            }
            KeyEvent.VK_UP -> handleUpKey()
            KeyEvent.VK_DOWN -> handleDownKey()
            KeyEvent.VK_ESCAPE -> handleDownEscape()
            KeyEvent.VK_TAB -> handleTab.handleTab(textPaneInputHandler, textPaneOutputHandler, sessionKey)
        }

//        fireKeyReleased(e)
    }

    private fun handleDownEscape() {
        textPaneInputHandler.clearUserInput();
    }

    private fun handleDownKey() {
        val command: String? = frame.getCurrentTab()?.history!!.nextCommand
        replaceUserInput(command)
    }

    private fun handleUpKey() {
        val command: String? = frame.getCurrentTab()?.history!!.previousCommand
        replaceUserInput(command)
    }

    private fun replaceUserInput(command: String?) {
        command?.let {
            textPaneInputHandler.clearUserInput()
            textPaneOutputHandler.printInplace(it)
        }
    }

}