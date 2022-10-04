package it.cm.cmd.core

import it.cm.cmd.core.ui.cli.CLIFrame2
import it.cm.common.ui.UIHandler
import it.cm.parser.RuntimeCommand
import it.cm.ui.swing.utils.installTheme
import java.io.File

class CliNow(sessionKey: String) : CLIFrame2(false, sessionKey)

fun main() {
    installTheme("/themes/Dracula.theme.json")
    initCLIFrame(File("C:\\Sviluppo\\Software\\Runner\\"))
    RuntimeCommand.COMMANDS.entries
    UIHandler.setDefaultCMD(CliNow::class.java)
    UIHandler.newCMDRequest("${System.currentTimeMillis()}", null)?.apply {
        isVisible = true
    }
}