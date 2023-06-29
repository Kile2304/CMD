package it.cm.cmd.commands.declared

import it.cm.common.ui.UIHandler
import it.cm.parser.BaseArgument
import it.cm.parser.CommandRunner
import it.cm.parser.annotation.Command
import it.cm.parser.annotation.Parameter

@Command(
    name = "COLOR"
    , classExecutor = ColorRunner::class
    , description = "Allow you to change the color of your console"
)
class ColorCommand : BaseArgument() {

    @Parameter(
        name = "COLOR"
        , index = 0
        , names = [ "color", "c" ]
        , required = true
        , description = "The color"
    )
    var color: String = ""
        private set

    @Parameter(
        name = "FOREGROUND"
        , index = 0
        , names = [ "foreground", "f" ]
        , hasArguments = false
        , description = "Means that will be changed the color of the foreground"
    )
    var isForeground: Boolean = false
        private set

    @Parameter(
        name = "BACKGROUND"
        , index = 0
        , names = [ "background", "b" ]
        , hasArguments = false
        , description = "Means that will be changed the color of the background"
    )
    var isBackground: Boolean = false
        private set

}

class ColorRunner: CommandRunner() {

    override fun execute(arg: BaseArgument, sessionKey: String) {
        UIHandler.getFrameFromSession(sessionKey)?.let {

        }
        TODO("Not yet implemented")
    }

}