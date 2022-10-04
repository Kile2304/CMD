package it.cm.cmd.commands.declared

import it.cm.common.ui.UIHandler
import it.cm.parser.BaseArgument
import it.cm.parser._interface.ICommandRunner
import it.cm.parser.annotation.Command
import it.cm.parser.annotation.Parameter

@Command(name = "TITLE", classExecutor = TitleCMDCommand::class)
class TitleCMDCommand : BaseArgument(), ICommandRunner {

    @Parameter(
        name = "TITLE"
        , index = 0
        , required = true
    )
    var title: String = ""
        private set

    @Parameter(
        name = "WINDOW"
        , names = [ "window", "w" ]
        , required = false
        , hasArguments = false
    )
    var toWindow: Boolean = false
        private set

    override fun execute(arg: BaseArgument, sessionKey: String) {
        arg as TitleCMDCommand
        UIHandler.getFrameFromSession(sessionKey)?.let {
            it.changeTitle(arg.title, arg.toWindow)
        }
    }

}