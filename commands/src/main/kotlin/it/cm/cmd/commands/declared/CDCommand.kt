package it.cm.cmd.commands.declared

import it.cm.cmd.commands.runner.CDRunner
import it.cm.parser.BaseArgument
import it.cm.parser.annotation.Command
import it.cm.parser.annotation.Parameter

@Command(name = "CD", classExecutor = CDRunner::class, description = "Allow you to change the current directory")
class CDCommand : BaseArgument() {

    @Parameter(
        name = "PATH"
        , index = 0
        , names = [ "path", "p" ]
        , required = true
    )
    var path: String = ""
        private set

}