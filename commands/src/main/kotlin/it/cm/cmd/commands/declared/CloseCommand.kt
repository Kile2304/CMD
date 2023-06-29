package it.cm.cmd.commands.declared

import it.cm.common.ui.UIHandler
import it.cm.parser.BaseArgument
import it.cm.parser._interface.ICommandRunner
import it.cm.parser.annotation.Command

@Command(
    name = "EXIT"
    , classExecutor = CloseCommand::class
    , description = "Allow you to close the current console"
)
class CloseCommand : BaseArgument(), ICommandRunner {

    override fun execute(arg: BaseArgument, sessionKey: String) {
        UIHandler.getFrameFromSession(sessionKey)?.closeNow(sessionKey)
    }

}