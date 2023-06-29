package it.cm.cmd.core.constant

import java.io.File

object CustomSettings {

    private const val SETTINGS_DIRECTORY_NAME = "Runner"
    var INSTALLATION_FOLDER: File = File(System.getProperty("user.home") + "\\AppData\\Roaming\\CMTerm")
    fun settingDirectory() = INSTALLATION_FOLDER?.let { File(it, SETTINGS_DIRECTORY_NAME) } ?: RuntimeException("Devi impostare una directory di installazione")
//        private const val CMD_HISTORY_FILE_NAME = "history.txt"
//        val CMD_HISTORY_FILE = File(SETTINGS_DIRECTORY, CMD_HISTORY_FILE_NAME)

}