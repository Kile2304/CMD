package it.cm.cmd.commands.declared

import it.cm.common.ui.UIHandler
import it.cm.parser.BaseArgument
import it.cm.parser._interface.ICommandRunner
import it.cm.parser.annotation.Command

@Command(name = "EXIT", classExecutor = CloseCMDCommand::class)
class CloseCMDCommand : BaseArgument(), ICommandRunner {

    override fun execute(arg: BaseArgument, sessionKey: String) {
        UIHandler.getFrameFromSession(sessionKey).closeNow(sessionKey)
    }

}