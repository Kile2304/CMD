package it.cm.cmd.core

import it.cm.cmd.core.terminal.frame.TerminalFrame
import it.cm.common.ui.UIHandler
import it.cm.parser.RuntimeCommand
import it.cm.ui.swing.utils.installTheme
import java.io.File
import javax.swing.SwingUtilities


class CliNow(sessionKey: String): TerminalFrame(false, sessionKey)

fun main() {
    installTheme("/themes/Dracula.theme.json")
    RuntimeCommand.COMMANDS.entries
    UIHandler.setDefaultCMD(CliNow::class.java)
    SwingUtilities.invokeLater {
        UIHandler.newCMDRequest("${System.currentTimeMillis()}", null)?.apply {
            isVisible = true
        }
    }
    println("here")
}