package it.cm.cmd.core.terminal.listener

import it.cm.cmd.commands.CommandConstants
import it.cm.common.ui.UIHandler
import it.cm.cmd.core.terminal.frame.TerminalFrame
import it.cm.cmd.core.terminal.frame.ITextPaneInputHandler
import it.cm.cmd.core.terminal.frame.ITextPaneOutputHandler
import it.cm.cmd.core.terminal.listener.actionMap.KeyMapAction
import it.cm.cmd.core.terminal.listener.keyboard.TabKey
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
) : KeyMapAction(sessionKey, textPaneInputHandler, textPaneOutputHandler), KeyListener {

    private val frame: TerminalFrame
        get() = UIHandler.getFrameFromSession(sessionKey) as TerminalFrame
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
        if (IS_RUNNING.get())
            e.consume()
    }

    override fun keyPressed(e: KeyEvent) {
        if (IS_RUNNING.get() && !(e.isControlDown && e.keyCode == KeyEvent.VK_Q))
            e.consume()
        when (e.keyCode) {
            KeyEvent.VK_ENTER -> { e.consume(); handleTab.clear() }
            KeyEvent.VK_ALT_GRAPH -> { e.consume(); handleTab.clear() }
            KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_TAB -> e.consume()
            else -> handleTab.clear()
        }
    }

    override fun keyReleased(e: KeyEvent) {
        if (IS_RUNNING.get() && !(e.isControlDown && e.keyCode == KeyEvent.VK_Q))
            e.consume()
        when (e.keyCode) {
            KeyEvent.VK_ENTER -> { e.consume(); handleEnterKey() }
            KeyEvent.VK_UP -> { e.consume(); handleUpKey() }
            KeyEvent.VK_DOWN -> { e.consume(); handleDownKey() }
            KeyEvent.VK_ESCAPE -> { e.consume(); handleDownEscape() }
            KeyEvent.VK_TAB -> { e.consume(); handleTab.handleTab(textPaneInputHandler, textPaneOutputHandler, sessionKey) }
        }

    }

    private fun handleDownEscape(): Unit = textPaneInputHandler.clearUserInput()
    private fun handleDownKey(): Unit = replaceUserInput(frame._history.nextCommand)
    private fun handleUpKey(): Unit = replaceUserInput(frame._history.previousCommand)
    private fun handleEnterKey() {
        val userInput = CommandConstants.replaceWithTemplate(textPaneInputHandler.getCommand(true))
        frame._history += userInput
        runCommand(userInput)
    }

    private fun replaceUserInput(command: String?) {
        command?.let {
            textPaneInputHandler.clearUserInput()
            textPaneOutputHandler.printInplace(it)
        }
    }

}