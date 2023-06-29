package it.cm.cmd.commands.declared

import it.cm.cmd.commands.runner.JSetRunner
import it.cm.parser.BaseArgument
import it.cm.parser._interface.ICommandValidation
import it.cm.parser.annotation.Command
import it.cm.parser.annotation.Parameter

@Command(
    name = "JSET"
    , classExecutor = JSetRunner::class
    , description = "Allow you to create console environment variable that you can"
                    + " call use on all your command by writing \${var_name}"
)
class JSetCommand : BaseArgument(), ICommandValidation {

    companion object {
        private val IS_VALID_NAME = Regex("\\w+")
    }

    @Parameter(
        name = "NAME"
        , index = 0
        , names = [ "name", "n" ]
        , required = true
        , description = "The name of the variable"
    )
    var name: String = ""
        private set

    @Parameter(
        name = "VALUE"
        , index = 1
        , names = [ "value", "v" ]
        , required = true
        , description = "The value of the variable"
    )
    var value: String = ""
        private set

    @Parameter(
        name = "JAVA"
        , names = [ "java", "j" ]
        , required = false
        , hasArguments = false
        , description = "Deprated, i don't remember"
    )
    var javaVariable: Boolean = false
        private set

    override fun isValid(): Boolean = IS_VALID_NAME.matches(name)

    operator fun component1(): String = name
    operator fun component2(): String = value
    operator fun component3(): Boolean = javaVariable

    override fun toString() = "Name: $name; Value: $value; JavaVariable:$javaVariable"

}