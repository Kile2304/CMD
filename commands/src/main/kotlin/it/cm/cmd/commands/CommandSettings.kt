package it.cm.cmd.commands

import java.io.File

object CommandSettings {

    var INSTALLATION_FOLDER = System.getProperty("user.home") + "\\AppData\\Roaming\\CMTerm"
    internal fun getVariableFile() = File(INSTALLATION_FOLDER, "variables.txt")

}