package it.cm.cmd.commands.declared

import it.cm.cmd.commands.runner.JSetRunner
import it.cm.parser.BaseArgument
import it.cm.parser._interface.ICommandValidation
import it.cm.parser.annotation.Command
import it.cm.parser.annotation.Parameter

@Command(name = "JSET", classExecutor = JSetRunner::class)
class JSetCommand : BaseArgument(), ICommandValidation {

    companion object {
        private val IS_VALID_NAME = Regex("\\w+")
    }

    @Parameter(
        name = "NAME"
        , index = 0
        , names = [ "name", "n" ]
        , required = true
    )
    var name: String = ""
        private set

    @Parameter(
        name = "VALUE"
        , index = 1
        , names = [ "value", "v" ]
        , required = true
    )
    var value: String = ""
        private set

    @Parameter(
        name = "JAVA"
        , names = [ "java", "j" ]
        , required = false
        , hasArguments = false
    )
    var javaVariable: Boolean = false
        private set

    override fun isValid(): Boolean = IS_VALID_NAME.matches(name)

    operator fun component1(): String = name
    operator fun component2(): String = value
    operator fun component3(): Boolean = javaVariable

    override fun toString() = "Name: $name; Value: $value; JavaVariable:$javaVariable"

}