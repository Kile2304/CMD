package it.cm.cmd.commands.declared

import it.cm.parser.BaseArgument
import it.cm.parser._interface.ICommandRunner
import it.cm.parser.annotation.Command
import it.cm.parser.annotation.Parameter
import it.cm.shortcut.ShortcutHandler
import it.cm.shortcut.updater.GitShortcutUpdater
import org.slf4j.LoggerFactory

@Command(name = "SHORTCUT", classExecutor = ShortcutCommand::class)
class ShortcutCommand: BaseArgument(), ICommandRunner {

    private val log = LoggerFactory.getLogger(ShortcutCommand::class.java)

    @Parameter(
        name = "ADD"
        , names = [ "add" ]
        , hasArguments = false
    )
    var toAdd: Boolean = false
        private set

    @Parameter(
        name = "UPDATE"
        , names = [ "update", "u" ]
        , hasArguments = false
    )
    var toUpdate: Boolean = false
        private set

    @Parameter(
        name = "UPDATE"
        , names = [ "show" ]
        , hasArguments = false
    )
    var show: Boolean = false
        private set

    @Parameter(
    name = "NAME"
    , names = [ "name", "n" ]
    , hasArguments = true
    )
    var name: String = ""
        private set

    @Parameter(
        name = "COMMAND"
        , names = [ "command", "c" ]
        , hasArguments = true
    )
    var command: String = ""
        private set

    @Parameter(
        name = "KEYS"
        , names = [ "keys", "k" ]
        , hasArguments = true
    )
    var keys: Array<String> = arrayOf()
        private set

    @Parameter(
        name = "PARAMETERS"
        , names = [ "parameters", "param" ]
        , hasArguments = true
    )
    var parameters: Array<String> = arrayOf()
        private set

    @Parameter(
        name = "WHERE NAME"
        , names = [ "wName" ]
        , hasArguments = true
    )
    var wName: String = ""
        private set

    @Parameter(
        name = "WHERE ID"
        , names = [ "wID" ]
        , hasArguments = true
    )
    var wID: Int = -1
        private set

    override fun execute(arg: BaseArgument, sessionKey: String) {
        arg as ShortcutCommand

        ShortcutHandler.runtime.find { (arg.wID == -1 || arg.wID == it.id) && (arg.wName.isEmpty() || arg.wName == it.name) }?.let {
            val updater = it.toUpdater()
            if (updater is GitShortcutUpdater) {
                updater.apply {
                    if (!arg.name.isEmpty())
                        name = arg.name
                    if (arg.wID != -1)
                        id = arg.wID
                    if (arg.parameters.isNotEmpty())
                        parameters = arg.parameters
                    if (arg.keys.isNotEmpty())
                        keys = arg.keys
                }
            }
            ShortcutHandler.updateShortcut(updater)

        } ?: log.error("Non ho trovato la shortcut indicata")
    }

}

//Gestione array