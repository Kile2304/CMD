package it.cm.cmd.commands.declared

import it.cm.cmd.commands.runner.ETRunner
import it.cm.parser.BaseArgument
import it.cm.parser.annotation.Command
import it.cm.parser.annotation.Parameter

@Command(
    name = "ET"
    , classExecutor = ETRunner::class
    , description = "Allow you to create event for the event system"
)
class ETCommand : BaseArgument() {

    @Parameter(
        name = "TYPE"
        , index = 0
        , names = [ "type", "t" ]
        , hasArguments = true
        , required = true
        , description = "Event type: ...."
    )
    var type: String = ""
        private set

    @Parameter(
        name = "PATH"
        , index = 1
        , names = [ "path", "p" ]
        , hasArguments = true
        , required = true
    )
    var path: String = ""
        private set

}