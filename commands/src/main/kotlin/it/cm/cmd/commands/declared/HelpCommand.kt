package it.cm.cmd.commands.declared

import it.cm.common.ui.UIHandler
import it.cm.parser.BaseArgument
import it.cm.parser.CommandHelper
import it.cm.parser._interface.ICommandRunner
import it.cm.parser.annotation.Command
import it.cm.parser.annotation.Parameter

@Command(
    name = "HELP"
    , classExecutor = HelpCommand::class
    , description = "Show an helper with all the command list"
)
class HelpCommand : BaseArgument(), ICommandRunner {

    @Parameter(
        name = "SYSTEM"
        , names = [ "system", "s" ]
        , required = false
        , hasArguments = false
        , description = "If specified the -s or -system, will be called the help on the system console and not the java"
    )
    var runSystem: Boolean = false
        private set

    override fun execute(arg: BaseArgument, sessionKey: String) {
        UIHandler.getFrameFromSession(sessionKey)?.let {
            CommandHelper.printHelp(it)
        }
    }

}