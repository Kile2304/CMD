package it.cm.cmd.commands.declared

import it.cm.common.ui.UIHandler
import it.cm.parser.BaseArgument
import it.cm.parser._interface.ICommandRunner
import it.cm.parser.annotation.Command

@Command(name = "CLS", classExecutor = ClearCommand::class, description = "Allow you to clear the console")
class ClearCommand : BaseArgument(), ICommandRunner {

    override fun execute(arg: BaseArgument, sessionKey: String) {
        UIHandler.getFrameFromSession(sessionKey)?.clear()
    }

}