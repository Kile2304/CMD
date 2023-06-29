package it.cm.cmd.commands.declared

import it.cm.cmd.commands.runner.CDRunner
import it.cm.common.ui.FrameOut
import it.cm.common.ui.UIHandler
import it.cm.parser.BaseArgument
import it.cm.parser.CommandRunner
import it.cm.parser._interface.ICommandValidation
import it.cm.parser.annotation.Command
import it.cm.parser.annotation.Parameter
import it.cm.service.QueryRun
import org.slf4j.LoggerFactory

@Command(name = "SQL", classExecutor = SQLCommandRunner::class)
class SQLCommand : BaseArgument(), ICommandValidation {

    @Parameter(
        name = "CONTEXT"
        , index = 0
        , names = [ "ctx", "-context" ]
        , required = false
        , hasArguments = true
    )
    var context: String = ""
        private set

    @Parameter(
        name = "DATASOURCES"
        , index = 0
        , names = [ "-Datasource" ]
        , required = false
        , hasArguments = true
    )
    var datasource: String = ""
        private set

    @Parameter(
        name = "SQL_FILE_PATH"
        , names = [ "-path" ]
        , required = false
        , hasArguments = true
    )
    var path: String = ""
        private set

    @Parameter(
        name = "QUERY"
        , names = [ "q", "-query" ]
        , required = false
        , hasArguments = true
    )
    var query: String = ""
        private set

    override fun isValid(): Boolean = query.isEmpty() xor path.isEmpty()

}

class SQLCommandRunner : CommandRunner() {

    companion object {
        private val defaultDS = mutableMapOf<String, String>()
    }

    private val log = LoggerFactory.getLogger(javaClass)

    override fun execute(arg: BaseArgument, sessionKey: String) {
        val setCommand = (arg as SQLCommand)
        val returned = QueryRun(key = "", query = setCommand.query, withMessage = true)
        returned.forEach {
            val frame = UIHandler.getFrameFromSession(sessionKey)
            when (it) {
                is String -> frame!!.appendLine(it)
                is Pair<*, *> -> {
                    writeHeader(frame!!, it.first as Array<String>)
                    writeBody(frame, it.second as Array<Array<String>>)
                } else -> println("Not supported query output")
            }
        }
    }

    private fun writeHeader(frame: FrameOut, first: Array<String>) {
        frame.appendLine(first.joinToString { " | " })
    }
    private fun writeBody(frame: FrameOut, first: Array<Array<String>>) {
        first.forEach {
            frame.appendLine(it.joinToString { " | " })
        }
    }
}