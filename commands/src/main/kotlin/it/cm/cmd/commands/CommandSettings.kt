package it.cm.cmd.commands

import java.io.File

object CommandSettings {

    var INSTALLATION_FOLDER = File("")
    internal fun getVariableFile() = File(INSTALLATION_FOLDER, "variables.txt")

}