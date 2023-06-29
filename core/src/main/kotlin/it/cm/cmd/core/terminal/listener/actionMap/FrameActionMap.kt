package it.cm.cmd.core.terminal.listener.actionMap

import it.cm.cmd.core.terminal.frame.TerminalFrame
import it.cm.common.ui.UIHandler
import java.awt.event.ActionEvent
import javax.swing.AbstractAction

object FrameActionMap {

    fun NEW_TAB(sessionKey: String) =
        object: AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                UIHandler.getFrameFromSession(sessionKey)?.newTab()
            }
        }

    fun CLOSE_TAB(sessionKey: String) =
        object: AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                UIHandler.getFrameFromSession(sessionKey)?.closeTab()
            }
        }

    fun barMenuVisibility(sessionKey: String) =
        object: AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                val frame = UIHandler.getFrameFromSession(sessionKey)
                if (frame is TerminalFrame)
                    frame.barMenuVisibility()
            }
        }

}