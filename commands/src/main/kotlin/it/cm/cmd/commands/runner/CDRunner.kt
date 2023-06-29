package it.cm.cmd.commands.runner

import it.cm.common.ui.UIHandler
import it.cm.cmd.commands.declared.CDCommand
import it.cm.cmd.common.AnsiUtil
import it.cm.parser.BaseArgument
import it.cm.parser.CommandRunner
import org.slf4j.LoggerFactory
import java.awt.Color
import java.io.File

class CDRunner : CommandRunner() {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun execute(arg: BaseArgument, sessionKey: String) {
        val cdCommand = (arg as CDCommand)
        var path = File(cdCommand.path)
        val sessionFrame = UIHandler.getFrameFromSession(sessionKey)
        if (!path.isAbsolute)
            path = File(sessionFrame!!.currentPath, cdCommand.path)
        if (path.exists() && path.isDirectory)
            sessionFrame?.currentPath = path.canonicalPath
        else
            sessionFrame?.appendAnsiLine("\n${AnsiUtil.ANSI_RED}Il percorso specificato non è valido${AnsiUtil.ANSI_RESET}")
    }

}