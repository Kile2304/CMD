package it.cm.cmd.core

import it.cm.cmd.commands.CommandSettings
import it.cm.cmd.core.ui.constant.CustomSettings
import java.io.File

internal var configuration: SConfiguration? = null
internal var HAS_BEEN_INITIALIZED = false

fun initCLIFrame(installationFolder: File, settingsFile: File) {
    CustomSettings.INSTALLATION_FOLDER = installationFolder
    CommandSettings.INSTALLATION_FOLDER = installationFolder
    HAS_BEEN_INITIALIZED = true
}

fun initCLIFrame(installationFolder: File) {
    configuration = SConfiguration()
    CustomSettings.INSTALLATION_FOLDER = installationFolder
    CommandSettings.INSTALLATION_FOLDER = installationFolder
    HAS_BEEN_INITIALIZED = true
}